package com.entrecheckpoints.savefile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entrecheckpoints.savefile.ui.components.NoteSlot
import com.entrecheckpoints.savefile.ui.components.PixelButton
import com.entrecheckpoints.savefile.ui.components.PixelPanel
import com.entrecheckpoints.savefile.ui.components.PixelProgressBar
import com.entrecheckpoints.savefile.ui.components.PixelRoomScene
import com.entrecheckpoints.savefile.ui.components.QuestCard
import com.entrecheckpoints.savefile.ui.components.SectionLabel
import com.entrecheckpoints.savefile.ui.theme.SavePalette
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    state: SaveFileUiState,
    palette: SavePalette,
    onNewNote: (String?) -> Unit,
    onEditNote: (com.entrecheckpoints.savefile.data.local.NoteEntity) -> Unit
) {
    val latest = state.notes.firstOrNull()
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd MMM", Locale("es", "MX"))).uppercase()

    LazyColumn(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    listOf(palette.background, palette.surface.copy(alpha = .6f), palette.background)
                )
            )
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("SAVE FILE", color = palette.text, fontWeight = FontWeight.Black, fontSize = 34.sp, letterSpacing = (-1).sp)
                    Text("A TINY GAME ABOUT YOUR THOUGHTS", color = palette.primary, fontWeight = FontWeight.Black, fontSize = 10.sp, letterSpacing = 1.1.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("LV.${state.level.toString().padStart(2, '0')}", color = palette.accent, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("$date", color = palette.muted, fontSize = 9.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            PixelProgressBar(state.levelProgress, palette, Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth()) {
                Text("${state.preferences.xp} XP", color = palette.muted, fontSize = 10.sp)
                Spacer(Modifier.weight(1f))
                Text("NEXT SAVE LEVEL", color = palette.muted, fontSize = 10.sp)
            }
        }

        item {
            PixelRoomScene(state.notes.size, state.totalWords, state.level, palette)
        }

        item {
            SectionLabel("CONTINUAR PARTIDA", palette, if (latest == null) "EMPTY SLOT" else "AUTOSAVE")
            PixelPanel(palette, modifier = Modifier.fillMaxWidth(), borderColor = palette.accent.copy(alpha = .7f)) {
                if (latest == null) {
                    Text("Tu primer slot está vacío.", color = palette.text, fontWeight = FontWeight.Bold)
                    Text("Escribe algo pequeño. No tiene que ser profundo ni digno de una novela de 900 páginas.", color = palette.muted, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    PixelButton("CREAR PRIMERA ENTRADA", palette, icon = Icons.Default.Edit, onClick = { onNewNote(null) })
                } else {
                    Text(latest.title, color = palette.text, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text(latest.body.replace('\n', ' '), color = palette.muted, maxLines = 2, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PixelButton("CONTINUAR", palette, Modifier.weight(1f), icon = Icons.Default.PlayArrow) { onEditNote(latest) }
                        PixelButton("NUEVO SLOT", palette, Modifier.weight(1f), secondary = true, icon = Icons.Default.Edit) { onNewNote(null) }
                    }
                }
            }
        }

        item { SectionLabel("MISIONES DE HOY", palette, "${state.completedQuestIds.size}/${state.quests.size}") }
        itemsIndexed(state.quests, key = { _, quest -> quest.id }) { _, quest ->
            QuestCard(
                quest = quest,
                completed = quest.id in state.completedQuestIds,
                palette = palette,
                onClick = { onNewNote(quest.description) }
            )
        }

        item { SectionLabel("RECENT SAVE SLOTS", palette, "${state.notes.size} TOTAL") }
        if (state.notes.isEmpty()) {
            item {
                PixelPanel(palette, Modifier.fillMaxWidth()) {
                    Text("NO SAVE DATA", color = palette.primary, fontWeight = FontWeight.Black)
                    Text("Tus entradas aparecerán aquí como archivos de guardado.", color = palette.muted, fontSize = 13.sp)
                }
            }
        } else {
            itemsIndexed(state.notes.take(4), key = { _, note -> note.id }) { index, note ->
                NoteSlot(note, index + 1, palette, onClick = { onEditNote(note) })
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}
