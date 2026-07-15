package com.entrecheckpoints.savefile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class SavePalette(
    val id: String,
    val name: String,
    val background: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val text: Color,
    val muted: Color,
    val success: Color,
    val lockedUntilLevel: Int
)

object SavePalettes {
    val Lavender = SavePalette(
        id = "lavender", name = "LAVENDER DREAM",
        background = Color(0xFF171329), surface = Color(0xFF241D3B), surfaceAlt = Color(0xFF30254D),
        primary = Color(0xFFB79AE8), secondary = Color(0xFFFF7EA8), accent = Color(0xFFFFD66B),
        text = Color(0xFFF8F2FF), muted = Color(0xFFB9AEC9), success = Color(0xFF86E3B0), lockedUntilLevel = 1
    )
    val Mint = SavePalette(
        id = "mint", name = "MINT CARTRIDGE",
        background = Color(0xFF10241F), surface = Color(0xFF17352D), surfaceAlt = Color(0xFF20473C),
        primary = Color(0xFF82E6BD), secondary = Color(0xFF8FCBFF), accent = Color(0xFFFFDE82),
        text = Color(0xFFF0FFF9), muted = Color(0xFFA4C9BB), success = Color(0xFFA7F0A0), lockedUntilLevel = 3
    )
    val Sunset = SavePalette(
        id = "sunset", name = "SUNSET CRT",
        background = Color(0xFF2B1520), surface = Color(0xFF442034), surfaceAlt = Color(0xFF5A2942),
        primary = Color(0xFFFF8FB2), secondary = Color(0xFFFFB366), accent = Color(0xFFFFE28A),
        text = Color(0xFFFFF1F5), muted = Color(0xFFD4A9B7), success = Color(0xFFA3E6B7), lockedUntilLevel = 5
    )
    val Midnight = SavePalette(
        id = "midnight", name = "MIDNIGHT SAVE",
        background = Color(0xFF07111F), surface = Color(0xFF0D1C31), surfaceAlt = Color(0xFF142944),
        primary = Color(0xFF65B8FF), secondary = Color(0xFF9C8CFF), accent = Color(0xFF56F0D2),
        text = Color(0xFFEAF5FF), muted = Color(0xFF8CA5BD), success = Color(0xFF66E39B), lockedUntilLevel = 8
    )

    val all = listOf(Lavender, Mint, Sunset, Midnight)
    fun byId(id: String) = all.firstOrNull { it.id == id } ?: Lavender
}

val PixelTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 36.sp, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 24.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 17.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.6.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 0.7.sp)
)

@Composable
fun SaveFileTheme(palette: SavePalette, content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = palette.primary,
        secondary = palette.secondary,
        tertiary = palette.accent,
        background = palette.background,
        surface = palette.surface,
        surfaceVariant = palette.surfaceAlt,
        onPrimary = palette.background,
        onSecondary = palette.background,
        onBackground = palette.text,
        onSurface = palette.text,
        onSurfaceVariant = palette.muted
    )
    MaterialTheme(colorScheme = colors, typography = PixelTypography, content = content)
}
