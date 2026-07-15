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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entrecheckpoints.savefile.data.local.NoteEntity
import com.entrecheckpoints.savefile.ui.components.NoteSlot
import com.entrecheckpoints.savefile.ui.components.PixelButton
import com.entrecheckpoints.savefile.ui.components.PixelPanel
import com.entrecheckpoints.savefile.ui.components.SectionLabel
import com.entrecheckpoints.savefile.ui.theme.SavePalette

@Composable
fun JournalScreen(
    state: SaveFileUiState,
    palette: SavePalette,
    onNew: () -> Unit,
    onEdit: (NoteEntity) -> Unit,
    onDelete: (NoteEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .background(palette.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text("MEMORY SLOTS", color = palette.text, fontWeight = FontWeight.Black, fontSize = 28.sp)
            Text("${state.notes.size} ENTRADAS · ${state.totalWords} PALABRAS GUARDADAS", color = palette.primary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Spacer(Modifier.height(14.dp))
            PixelButton("NUEVA ENTRADA", palette, Modifier.fillMaxWidth(), icon = Icons.Default.Edit, onClick = onNew)
        }

        item { SectionLabel("ARCHIVOS DE GUARDADO", palette, "LOCAL / OFFLINE") }

        if (state.notes.isEmpty()) {
            item {
                PixelPanel(palette, Modifier.fillMaxWidth()) {
                    Text("EMPTY MEMORY CARD", color = palette.primary, fontWeight = FontWeight.Black)
                    Text("Todavía no hay nada guardado. Extrañamente limpio para un cerebro humano.", color = palette.muted)
                }
            }
        } else {
            itemsIndexed(state.notes, key = { _, note -> note.id }) { index, note ->
                Column {
                    NoteSlot(note, index + 1, palette, onClick = { onEdit(note) })
                    Row(
                        Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("EDIT", color = palette.primary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Icon(Icons.Default.Edit, null, tint = palette.primary, modifier = Modifier.padding(horizontal = 6.dp))
                        Spacer(Modifier.weight(1f))
                        PixelButton("BORRAR", palette, secondary = true, icon = Icons.Default.Delete) { onDelete(note) }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}
