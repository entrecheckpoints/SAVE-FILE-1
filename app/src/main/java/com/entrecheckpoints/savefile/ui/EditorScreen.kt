package com.entrecheckpoints.savefile.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entrecheckpoints.savefile.data.SaveFileRepository
import com.entrecheckpoints.savefile.ui.components.PixelButton
import com.entrecheckpoints.savefile.ui.components.PixelProgressBar
import com.entrecheckpoints.savefile.ui.theme.SavePalette

private val moods = listOf(
    "CALM" to "○ CALMA",
    "HAPPY" to "✦ FELIZ",
    "SAD" to "☂ TRISTE",
    "ANGRY" to "⚡ MOLESTO",
    "TIRED" to "☾ CANSADO"
)

@Composable
fun EditorScreen(
    state: SaveFileUiState,
    palette: SavePalette,
    typedGlyph: Char?,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onMoodChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val words = SaveFileRepository.countWords(state.editorBody)
    val writingProgress = (words / 160f).coerceIn(0f, 1f)

    Box(Modifier.fillMaxSize().background(palette.background)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(42.dp)
                        .background(palette.surface)
                        .border(2.dp, palette.primary.copy(alpha = .65f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.ArrowBack, null, tint = palette.primary) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("WRITE MODE", color = palette.primary, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.sp)
                    Text(if (state.editingId == null) "NEW SAVE SLOT" else "EDITING SLOT ${state.editingId}", color = palette.muted, fontSize = 10.sp)
                }
                Text("$words W", color = palette.accent, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }

            Spacer(Modifier.height(12.dp))
            BasicTextField(
                value = state.editorTitle,
                onValueChange = onTitleChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(2.dp, palette.secondary.copy(alpha = .55f))
                    .padding(13.dp),
                textStyle = TextStyle(
                    color = palette.text,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                ),
                singleLine = true,
                cursorBrush = SolidColor(palette.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                decorationBox = { inner ->
                    if (state.editorTitle.isBlank()) Text("TÍTULO DEL SAVE...", color = palette.muted, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    inner()
                }
            )

            Spacer(Modifier.height(9.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                moods.forEach { (id, label) ->
                    val selected = state.editorMood == id
                    Text(
                        text = label,
                        color = if (selected) palette.background else palette.muted,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .background(if (selected) palette.secondary else palette.surface)
                            .border(1.dp, if (selected) palette.accent else palette.primary.copy(alpha = .35f))
                            .clickable { onMoodChange(id) }
                            .padding(horizontal = 9.dp, vertical = 7.dp)
                    )
                }
            }

            Spacer(Modifier.height(9.dp))
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(2.dp, palette.primary.copy(alpha = .5f))
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = state.editorBody,
                    onValueChange = onBodyChange,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(
                        color = palette.text,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 18.sp,
                        lineHeight = 27.sp
                    ),
                    cursorBrush = SolidColor(palette.accent),
                    visualTransformation = EmotionColorTransformation(palette),
                    keyboardActions = KeyboardActions(onDone = { onSave() }),
                    decorationBox = { inner ->
                        if (state.editorBody.isBlank()) {
                            Text(
                                "Escribe lo que tengas en la cabeza. No hace falta resolverlo todo hoy...",
                                color = palette.muted,
                                fontSize = 17.sp,
                                lineHeight = 25.sp
                            )
                        }
                        inner()
                    }
                )

                Column(Modifier.align(Alignment.TopEnd), horizontalAlignment = Alignment.End) {
                    AnimatedContent(
                        targetState = typedGlyph,
                        transitionSpec = { (fadeIn() + scaleIn(initialScale = .45f)) togetherWith (fadeOut() + scaleOut(targetScale = 1.5f)) },
                        label = "typedGlyph"
                    ) { glyph ->
                        if (glyph != null && !glyph.isWhitespace()) {
                            Text(glyph.toString(), color = palette.accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 28.sp)
                        } else Spacer(Modifier.size(32.dp))
                    }
                    Text(if (words < 80) "COMBO BUILDING" else "FLOW ACTIVE", color = if (words < 80) palette.muted else palette.success, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        when {
                            words == 0 -> "READY PLAYER"
                            words < 35 -> "FIRST THOUGHTS"
                            words < 80 -> "WRITING COMBO"
                            words < 160 -> "DEEP SAVE"
                            else -> "FULL HEART MODE"
                        },
                        color = palette.secondary,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                    PixelProgressBar(writingProgress, palette, Modifier.fillMaxWidth())
                }
                Spacer(Modifier.width(10.dp))
                PixelButton(
                    text = "GUARDAR",
                    palette = palette,
                    enabled = state.editorTitle.isNotBlank() || state.editorBody.isNotBlank(),
                    icon = Icons.Default.Save,
                    onClick = onSave
                )
            }
            Spacer(Modifier.height(10.dp))
        }

        AnimatedVisibility(
            visible = state.saveBanner != null,
            enter = fadeIn() + scaleIn(initialScale = .8f),
            exit = fadeOut() + scaleOut(targetScale = 1.15f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            val banner = state.saveBanner
            Column(
                Modifier
                    .background(if (banner?.levelUp == true) palette.accent else palette.primary)
                    .border(4.dp, palette.text)
                    .padding(horizontal = 30.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(banner?.title.orEmpty(), color = palette.background, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 22.sp)
                Text(banner?.subtitle.orEmpty(), color = palette.background.copy(alpha = .8f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}


private class EmotionColorTransformation(private val palette: SavePalette) : VisualTransformation {
    private val positive = Regex("\\b(feliz|alegría|alegre|gracias|agradezco|amo|orgulloso|orgullosa|bien)\\b", RegexOption.IGNORE_CASE)
    private val calm = Regex("\\b(calma|tranquilo|tranquila|paz|descanso|suave)\\b", RegexOption.IGNORE_CASE)
    private val heavy = Regex("\\b(triste|miedo|ansiedad|enojo|enojado|enojada|dolor|cansado|cansada)\\b", RegexOption.IGNORE_CASE)

    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val styled = buildAnnotatedString {
            append(text.text)
            positive.findAll(text.text).forEach { addStyle(SpanStyle(color = palette.accent, fontWeight = FontWeight.Bold), it.range.first, it.range.last + 1) }
            calm.findAll(text.text).forEach { addStyle(SpanStyle(color = palette.primary, fontWeight = FontWeight.Bold), it.range.first, it.range.last + 1) }
            heavy.findAll(text.text).forEach { addStyle(SpanStyle(color = palette.secondary, fontWeight = FontWeight.Bold), it.range.first, it.range.last + 1) }
        }
        return TransformedText(styled, OffsetMapping.Identity)
    }
}
