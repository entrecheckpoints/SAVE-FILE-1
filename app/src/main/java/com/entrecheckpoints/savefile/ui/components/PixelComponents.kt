package com.entrecheckpoints.savefile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entrecheckpoints.savefile.data.local.NoteEntity
import com.entrecheckpoints.savefile.domain.DailyQuest
import com.entrecheckpoints.savefile.ui.model.AppScreen
import com.entrecheckpoints.savefile.ui.theme.SavePalette
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PixelPanel(
    palette: SavePalette,
    modifier: Modifier = Modifier,
    borderColor: Color = palette.primary.copy(alpha = 0.45f),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(palette.surface, RectangleShape)
            .border(2.dp, borderColor, RectangleShape)
            .padding(14.dp),
        content = content
    )
}

@Composable
fun PixelButton(
    text: String,
    palette: SavePalette,
    modifier: Modifier = Modifier,
    secondary: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = when {
            !enabled -> palette.surfaceAlt
            secondary -> palette.surfaceAlt
            else -> palette.primary
        }, label = "buttonColor"
    )
    val fg = if (!enabled || secondary) palette.text else palette.background
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.52f)
            .background(bg, RectangleShape)
            .border(2.dp, if (secondary) palette.primary.copy(alpha = .55f) else palette.text.copy(alpha = .18f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = fg, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp)
    }
}

@Composable
fun SectionLabel(text: String, palette: SavePalette, right: String? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = palette.primary, fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontSize = 13.sp)
        Spacer(Modifier.weight(1f))
        if (right != null) Text(right, color = palette.muted, fontSize = 11.sp)
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun PixelProgressBar(progress: Float, palette: SavePalette, modifier: Modifier = Modifier) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), label = "xpProgress")
    Box(
        modifier
            .height(10.dp)
            .background(palette.surfaceAlt)
            .border(1.dp, palette.muted.copy(alpha = .45f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(animated)
                .height(10.dp)
                .background(palette.accent)
        )
    }
}

@Composable
fun QuestCard(
    quest: DailyQuest,
    completed: Boolean,
    palette: SavePalette,
    onClick: () -> Unit
) {
    val border = if (completed) palette.success else palette.primary.copy(alpha = .45f)
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (completed) palette.success.copy(alpha = .10f) else palette.surface)
            .border(2.dp, border)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(30.dp)
                .background(if (completed) palette.success else palette.surfaceAlt)
                .border(2.dp, border),
            contentAlignment = Alignment.Center
        ) {
            if (completed) Icon(Icons.Default.Check, null, tint = palette.background, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(quest.title, color = if (completed) palette.success else palette.text, fontWeight = FontWeight.Black, fontSize = 13.sp)
            Text(quest.description, color = palette.muted, fontSize = 12.sp, lineHeight = 16.sp)
        }
        Text("+${quest.xp} XP", color = palette.accent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@Composable
fun NoteSlot(
    note: NoteEntity,
    index: Int,
    palette: SavePalette,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM · HH:mm")
    val date = Instant.ofEpochMilli(note.updatedAt).atZone(ZoneId.systemDefault()).format(formatter).uppercase()
    Row(
        modifier
            .fillMaxWidth()
            .background(palette.surface)
            .border(2.dp, palette.primary.copy(alpha = .34f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(48.dp)
                .background(palette.surfaceAlt)
                .border(2.dp, palette.secondary.copy(alpha = .65f)),
            contentAlignment = Alignment.Center
        ) {
            Text(index.toString().padStart(2, '0'), color = palette.secondary, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(note.title, color = palette.text, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("$date · ${note.wordCount} PALABRAS", color = palette.muted, fontSize = 10.sp)
            if (note.body.isNotBlank()) {
                Text(note.body.replace('\n', ' '), color = palette.muted.copy(alpha = .82f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Text(moodGlyph(note.mood), fontSize = 20.sp)
    }
}

fun moodGlyph(mood: String): String = when (mood) {
    "HAPPY" -> "✦"
    "SAD" -> "☂"
    "ANGRY" -> "⚡"
    "TIRED" -> "☾"
    else -> "○"
}

@Composable
fun PixelRoomScene(noteCount: Int, totalWords: Int, level: Int, palette: SavePalette) {
    PixelPanel(palette, modifier = Modifier.fillMaxWidth(), borderColor = palette.secondary.copy(alpha = .6f)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("MY SAVE ROOM", color = palette.secondary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Text("LV.$level · $noteCount RECUERDOS", color = palette.muted, fontSize = 10.sp)
            }
            Spacer(Modifier.weight(1f))
            Text("${totalWords}W", color = palette.accent, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(10.dp))
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RectangleShape)
                .background(palette.background)
        ) {
            val px = size.width / 32f
            fun tile(x: Int, y: Int, w: Int, h: Int, color: Color) =
                drawRect(color, Offset(x * px, y * px), Size(w * px, h * px))

            tile(0, 0, 32, 19, palette.background)
            tile(0, 13, 32, 6, palette.surfaceAlt)
            for (x in 0 until 32 step 2) tile(x, 13, 1, 6, palette.surface.copy(alpha = .55f))

            tile(3, 2, 9, 7, palette.surface)
            tile(4, 3, 7, 5, palette.primary.copy(alpha = .45f))
            tile(7, 3, 1, 5, palette.text.copy(alpha = .45f))
            tile(4, 5, 7, 1, palette.text.copy(alpha = .35f))

            tile(16, 10, 11, 2, palette.secondary.copy(alpha = .8f))
            tile(17, 12, 2, 4, palette.secondary.copy(alpha = .55f))
            tile(24, 12, 2, 4, palette.secondary.copy(alpha = .55f))
            tile(18, 7, 6, 3, palette.surface)
            tile(19, 8, 4, 1, palette.primary)
            tile(21, 10, 1, 2, palette.primary.copy(alpha = .8f))

            tile(28, 9, 2, 4, palette.accent.copy(alpha = .85f))
            tile(27, 8, 4, 1, palette.accent)
            tile(28, 13, 2, 2, palette.surface)

            if (noteCount >= 3) {
                tile(5, 10, 3, 3, palette.success.copy(alpha = .7f))
                tile(6, 8, 1, 2, palette.success)
                tile(4, 9, 1, 2, palette.success.copy(alpha = .8f))
                tile(8, 9, 1, 2, palette.success.copy(alpha = .8f))
            }
            if (noteCount >= 7) {
                tile(13, 4, 1, 5, palette.accent)
                tile(12, 4, 3, 1, palette.accent)
                tile(12, 9, 3, 1, palette.accent.copy(alpha = .4f))
            }
            if (noteCount >= 15) {
                tile(10, 14, 5, 2, palette.primary.copy(alpha = .9f))
                tile(9, 15, 1, 1, palette.primary)
                tile(15, 15, 1, 1, palette.primary)
                tile(11, 13, 1, 1, palette.text)
                tile(13, 13, 1, 1, palette.text)
            }
            if (level >= 5) {
                for (i in 0 until 5) {
                    val x = 2 + i * 6
                    tile(x, 1 + (i % 2), 1, 1, palette.accent.copy(alpha = .8f))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            when {
                noteCount < 3 -> "Escribe 3 entradas para desbloquear una planta."
                noteCount < 7 -> "La planta llegó. A las 7 entradas aparece una lámpara."
                noteCount < 15 -> "Tu cuarto está creciendo. Una mascota espera en el slot 15."
                else -> "Tu habitación guarda todo lo que has escrito."
            },
            color = palette.muted,
            fontSize = 11.sp
        )
    }
}

@Composable
fun BottomGameBar(
    current: AppScreen,
    palette: SavePalette,
    onNavigate: (AppScreen) -> Unit,
    onNew: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(palette.surface)
            .border(2.dp, palette.primary.copy(alpha = .45f))
            .padding(horizontal = 6.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(Icons.Default.Home, "INICIO", current == AppScreen.HOME, palette) { onNavigate(AppScreen.HOME) }
        NavItem(Icons.Default.Description, "DIARIO", current == AppScreen.JOURNAL, palette) { onNavigate(AppScreen.JOURNAL) }
        Box(
            Modifier
                .size(52.dp)
                .background(palette.primary)
                .border(3.dp, palette.accent)
                .clickable(onClick = onNew),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Add, null, tint = palette.background, modifier = Modifier.size(30.dp)) }
        NavItem(Icons.Default.Settings, "AJUSTES", current == AppScreen.SETTINGS, palette) { onNavigate(AppScreen.SETTINGS) }
    }
}

@Composable
private fun NavItem(icon: ImageVector, label: String, selected: Boolean, palette: SavePalette, onClick: () -> Unit) {
    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = if (selected) palette.primary else palette.muted, modifier = Modifier.size(21.dp))
        Text(label, color = if (selected) palette.primary else palette.muted, fontSize = 9.sp, fontWeight = FontWeight.Black)
        AnimatedVisibility(selected) {
            Box(Modifier.padding(top = 3.dp).width(20.dp).height(2.dp).background(palette.accent))
        }
    }
}
