package com.entrecheckpoints.savefile.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.entrecheckpoints.savefile.audio.FeedbackManager
import com.entrecheckpoints.savefile.audio.Sound
import com.entrecheckpoints.savefile.data.local.NoteEntity
import com.entrecheckpoints.savefile.ui.components.BottomGameBar
import com.entrecheckpoints.savefile.ui.model.AppScreen
import com.entrecheckpoints.savefile.ui.theme.SaveFileTheme
import com.entrecheckpoints.savefile.ui.theme.SavePalettes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun SaveFileApp(viewModel: SaveFileViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val palette = SavePalettes.byId(state.preferences.themeId)
    val context = LocalContext.current
    val feedback = remember { FeedbackManager(context.applicationContext) }
    val latestPreferences by rememberUpdatedState(state.preferences)
    var typedGlyph by remember { mutableStateOf<Char?>(null) }
    var pendingDelete by remember { mutableStateOf<NoteEntity?>(null) }

    DisposableEffect(feedback) {
        onDispose { feedback.release() }
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is UiEffect.PlaySound -> feedback.play(effect.sound, latestPreferences.soundEnabled, effect.volume)
                is UiEffect.StrongFeedback -> {
                    feedback.play(effect.sound, latestPreferences.soundEnabled)
                    feedback.tap(latestPreferences.hapticsEnabled, strong = true)
                }
                is UiEffect.TypedCharacter -> {
                    typedGlyph = effect.char
                    feedback.tap(latestPreferences.hapticsEnabled, strong = false)
                    launch {
                        val captured = effect.char
                        delay(170)
                        if (typedGlyph == captured) typedGlyph = null
                    }
                }
            }
        }
    }

    SaveFileTheme(palette) {
        if (state.screen == AppScreen.EDITOR) {
            BackHandler { viewModel.navigate(AppScreen.HOME) }
            EditorScreen(
                state = state,
                palette = palette,
                typedGlyph = typedGlyph,
                onBack = { viewModel.navigate(AppScreen.HOME) },
                onTitleChange = viewModel::updateTitle,
                onBodyChange = viewModel::updateBody,
                onMoodChange = viewModel::updateMood,
                onSave = viewModel::saveNote
            )
        } else {
            Scaffold(
                containerColor = palette.background,
                bottomBar = {
                    BottomGameBar(
                        current = state.screen,
                        palette = palette,
                        onNavigate = viewModel::navigate,
                        onNew = { viewModel.newNote() }
                    )
                }
            ) { padding ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(palette.background)
                        .padding(padding)
                ) {
                    when (state.screen) {
                        AppScreen.HOME -> HomeScreen(
                            state,
                            palette,
                            onNewNote = viewModel::newNote,
                            onEditNote = viewModel::editNote
                        )
                        AppScreen.JOURNAL -> JournalScreen(
                            state,
                            palette,
                            onNew = { viewModel.newNote() },
                            onEdit = viewModel::editNote,
                            onDelete = { pendingDelete = it }
                        )
                        AppScreen.SETTINGS -> SettingsScreen(
                            state,
                            palette,
                            onSound = viewModel::setSound,
                            onHaptics = viewModel::setHaptics,
                            onTheme = viewModel::setTheme
                        )
                        AppScreen.EDITOR -> Unit
                    }
                }
            }
        }

        pendingDelete?.let { note ->
            AlertDialog(
                onDismissRequest = { pendingDelete = null },
                title = { Text("DELETE SAVE SLOT?") },
                text = { Text("Se borrará “${note.title}” del dispositivo. Esta acción no concede XP, sorprendentemente.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteNote(note)
                        pendingDelete = null
                    }) { Text("BORRAR", color = palette.secondary) }
                },
                dismissButton = {
                    TextButton(onClick = { pendingDelete = null }) { Text("CANCELAR", color = palette.primary) }
                },
                containerColor = palette.surface,
                titleContentColor = palette.text,
                textContentColor = palette.muted
            )
        }
    }
}
