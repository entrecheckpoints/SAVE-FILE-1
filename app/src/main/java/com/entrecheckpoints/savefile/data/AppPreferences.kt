package com.entrecheckpoints.savefile.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("save_file_preferences", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(readState())
    val state: StateFlow<PreferenceState> = _state

    private fun readState() = PreferenceState(
        xp = prefs.getInt(KEY_XP, 0),
        soundEnabled = prefs.getBoolean(KEY_SOUND, true),
        hapticsEnabled = prefs.getBoolean(KEY_HAPTICS, true),
        themeId = prefs.getString(KEY_THEME, "lavender") ?: "lavender",
        lastQuestDay = prefs.getLong(KEY_QUEST_DAY, -1L),
        completedQuestIds = prefs.getStringSet(KEY_COMPLETED_QUESTS, emptySet()).orEmpty()
    )

    fun addXp(amount: Int) = update { copy(xp = xp + amount.coerceAtLeast(0)) }
    fun setSound(enabled: Boolean) = update { copy(soundEnabled = enabled) }
    fun setHaptics(enabled: Boolean) = update { copy(hapticsEnabled = enabled) }
    fun setTheme(themeId: String) = update { copy(themeId = themeId) }

    fun setQuestState(day: Long, ids: Set<String>) = update {
        copy(lastQuestDay = day, completedQuestIds = ids)
    }

    private fun update(transform: PreferenceState.() -> PreferenceState) {
        val next = _state.value.transform()
        prefs.edit()
            .putInt(KEY_XP, next.xp)
            .putBoolean(KEY_SOUND, next.soundEnabled)
            .putBoolean(KEY_HAPTICS, next.hapticsEnabled)
            .putString(KEY_THEME, next.themeId)
            .putLong(KEY_QUEST_DAY, next.lastQuestDay)
            .putStringSet(KEY_COMPLETED_QUESTS, next.completedQuestIds)
            .apply()
        _state.value = next
    }

    companion object {
        private const val KEY_XP = "xp"
        private const val KEY_SOUND = "sound"
        private const val KEY_HAPTICS = "haptics"
        private const val KEY_THEME = "theme"
        private const val KEY_QUEST_DAY = "quest_day"
        private const val KEY_COMPLETED_QUESTS = "completed_quests"
    }
}

data class PreferenceState(
    val xp: Int,
    val soundEnabled: Boolean,
    val hapticsEnabled: Boolean,
    val themeId: String,
    val lastQuestDay: Long,
    val completedQuestIds: Set<String>
)
