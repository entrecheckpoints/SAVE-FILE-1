package com.entrecheckpoints.savefile.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.entrecheckpoints.savefile.SaveFileApplication
import com.entrecheckpoints.savefile.audio.Sound
import com.entrecheckpoints.savefile.data.PreferenceState
import com.entrecheckpoints.savefile.data.SaveFileRepository
import com.entrecheckpoints.savefile.data.local.NoteEntity
import com.entrecheckpoints.savefile.domain.DailyQuest
import com.entrecheckpoints.savefile.domain.LevelSystem
import com.entrecheckpoints.savefile.domain.QuestEngine
import com.entrecheckpoints.savefile.ui.model.AppScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SaveFileViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as SaveFileApplication
    private val repository = app.repository
    private val preferences = app.preferences

    private val _uiState = MutableStateFlow(
        SaveFileUiState(preferences = preferences.state.value)
    )
    val uiState: StateFlow<SaveFileUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<UiEffect>(extraBufferCapacity = 16)
    val effects: SharedFlow<UiEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.notes.collect { notes ->
                _uiState.value = buildState(_uiState.value.copy(notes = notes))
            }
        }
        viewModelScope.launch {
            preferences.state.collect { pref ->
                _uiState.value = buildState(_uiState.value.copy(preferences = pref))
            }
        }
    }

    fun navigate(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(screen = screen, saveBanner = null)
        _effects.tryEmit(UiEffect.PlaySound(Sound.MENU))
    }

    fun newNote(prompt: String? = null) {
        _uiState.value = _uiState.value.copy(
            screen = AppScreen.EDITOR,
            editingId = null,
            editorTitle = "",
            editorBody = prompt?.let { "$it\n\n" }.orEmpty(),
            editorMood = "CALM",
            editorCreatedAt = null,
            saveBanner = null
        )
        _effects.tryEmit(UiEffect.PlaySound(Sound.MENU))
    }

    fun editNote(note: NoteEntity) {
        _uiState.value = _uiState.value.copy(
            screen = AppScreen.EDITOR,
            editingId = note.id,
            editorTitle = note.title,
            editorBody = note.body,
            editorMood = note.mood,
            editorCreatedAt = note.createdAt,
            saveBanner = null
        )
        _effects.tryEmit(UiEffect.PlaySound(Sound.MENU))
    }

    fun updateTitle(value: String) {
        _uiState.value = _uiState.value.copy(editorTitle = value)
    }

    fun updateBody(value: String) {
        val previous = _uiState.value.editorBody
        _uiState.value = _uiState.value.copy(editorBody = value)
        if (value.length > previous.length) {
            val char = value.lastOrNull()
            _effects.tryEmit(UiEffect.TypedCharacter(char ?: ' '))
            if (char in listOf('.', '!', '?')) _effects.tryEmit(UiEffect.PlaySound(Sound.SENTENCE))
            else _effects.tryEmit(UiEffect.PlaySound(Sound.KEY, 0.55f))
        }
    }

    fun updateMood(value: String) {
        _uiState.value = _uiState.value.copy(editorMood = value)
        _effects.tryEmit(UiEffect.PlaySound(Sound.MENU))
    }

    fun saveNote() {
        val state = _uiState.value
        if (state.editorTitle.isBlank() && state.editorBody.isBlank()) return
        viewModelScope.launch {
            val levelBefore = LevelSystem.levelForXp(preferences.state.value.xp)
            val saved = repository.saveNote(
                id = state.editingId,
                title = state.editorTitle,
                body = state.editorBody,
                mood = state.editorMood,
                createdAt = state.editorCreatedAt
            )
            val baseXp = (20 + saved.wordCount / 8).coerceAtMost(95)
            val day = QuestEngine.dayKey()
            val quests = QuestEngine.questsForDay(day)
            val notesForProgress = (_uiState.value.notes.filterNot { it.id == saved.id } + saved)
            val auto = QuestEngine.autoCompletedQuestIds(quests, notesForProgress, day)
            val pref = preferences.state.value
            val existing = if (pref.lastQuestDay == day) pref.completedQuestIds else emptySet()
            val newlyCompleted = auto - existing
            val questXp = quests.filter { it.id in newlyCompleted }.sumOf(DailyQuest::xp)
            preferences.setQuestState(day, existing + auto)
            preferences.addXp(baseXp + questXp)
            val levelAfter = LevelSystem.levelForXp(preferences.state.value.xp)

            _uiState.value = buildState(
                _uiState.value.copy(
                    editingId = saved.id,
                    editorTitle = saved.title,
                    editorBody = saved.body,
                    editorCreatedAt = saved.createdAt,
                    saveBanner = SaveBanner(
                        title = if (levelAfter > levelBefore) "LEVEL UP!" else "PROGRESO GUARDADO",
                        subtitle = "+${baseXp + questXp} XP${if (newlyCompleted.isNotEmpty()) " · ${newlyCompleted.size} MISIÓN" else ""}",
                        levelUp = levelAfter > levelBefore
                    )
                )
            )
            _effects.emit(UiEffect.StrongFeedback(if (levelAfter > levelBefore) Sound.LEVEL else Sound.SAVE))
            if (newlyCompleted.isNotEmpty()) _effects.emit(UiEffect.PlaySound(Sound.QUEST))
            delay(1600)
            _uiState.value = _uiState.value.copy(saveBanner = null)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.delete(note)
            _effects.emit(UiEffect.StrongFeedback(Sound.MENU))
        }
    }

    fun setSound(enabled: Boolean) = preferences.setSound(enabled)
    fun setHaptics(enabled: Boolean) = preferences.setHaptics(enabled)
    fun setTheme(id: String) {
        val palette = com.entrecheckpoints.savefile.ui.theme.SavePalettes.byId(id)
        if (_uiState.value.level >= palette.lockedUntilLevel) {
            preferences.setTheme(id)
            _effects.tryEmit(UiEffect.PlaySound(Sound.MENU))
        }
    }

    private fun buildState(state: SaveFileUiState): SaveFileUiState {
        val day = QuestEngine.dayKey()
        val quests = QuestEngine.questsForDay(day)
        val stored = if (state.preferences.lastQuestDay == day) state.preferences.completedQuestIds else emptySet()
        val completed = stored + QuestEngine.autoCompletedQuestIds(quests, state.notes, day)
        val xp = state.preferences.xp
        return state.copy(
            quests = quests,
            completedQuestIds = completed,
            level = LevelSystem.levelForXp(xp),
            levelProgress = LevelSystem.progress(xp),
            totalWords = state.notes.sumOf { it.wordCount }
        )
    }
}

data class SaveFileUiState(
    val screen: AppScreen = AppScreen.HOME,
    val notes: List<NoteEntity> = emptyList(),
    val preferences: PreferenceState,
    val quests: List<DailyQuest> = emptyList(),
    val completedQuestIds: Set<String> = emptySet(),
    val level: Int = 1,
    val levelProgress: Float = 0f,
    val totalWords: Int = 0,
    val editingId: Long? = null,
    val editorTitle: String = "",
    val editorBody: String = "",
    val editorMood: String = "CALM",
    val editorCreatedAt: Long? = null,
    val saveBanner: SaveBanner? = null
)

data class SaveBanner(val title: String, val subtitle: String, val levelUp: Boolean)

sealed interface UiEffect {
    data class PlaySound(val sound: Sound, val volume: Float = 1f) : UiEffect
    data class TypedCharacter(val char: Char) : UiEffect
    data class StrongFeedback(val sound: Sound) : UiEffect
}
