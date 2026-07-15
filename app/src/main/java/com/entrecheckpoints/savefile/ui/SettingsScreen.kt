package com.entrecheckpoints.savefile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entrecheckpoints.savefile.ui.components.PixelPanel
import com.entrecheckpoints.savefile.ui.components.SectionLabel
import com.entrecheckpoints.savefile.ui.theme.SavePalette
import com.entrecheckpoints.savefile.ui.theme.SavePalettes

@Composable
fun SettingsScreen(
    state: SaveFileUiState,
    palette: SavePalette,
    onSound: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onTheme: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.background(palette.background).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text("OPTIONS MENU", color = palette.text, fontWeight = FontWeight.Black, fontSize = 28.sp)
            Text("PERSONALIZA TU PARTIDA", color = palette.primary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }

        item {
            SectionLabel("FEEDBACK", palette, "SATISFYING MODE")
            PixelPanel(palette, Modifier.fillMaxWidth()) {
                SettingToggle(
                    icon = Icons.Default.VolumeUp,
                    title = "SONIDOS DE ESCRITURA",
                    subtitle = "Clics, frases, guardados y level up.",
                    checked = state.preferences.soundEnabled,
                    palette = palette,
                    onChecked = onSound
                )
                Spacer(Modifier.height(8.dp))
                SettingToggle(
                    icon = Icons.Default.Vibration,
                    title = "VIBRACIÓN SUAVE",
                    subtitle = "Pequeños pulsos al guardar y navegar.",
                    checked = state.preferences.hapticsEnabled,
                    palette = palette,
                    onChecked = onHaptics
                )
            }
        }

        item {
            SectionLabel("COLOR CARTRIDGES", palette, "LV.${state.level}")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SavePalettes.all.forEach { option ->
                    val unlocked = state.level >= option.lockedUntilLevel
                    ThemeCard(
                        option = option,
                        selected = state.preferences.themeId == option.id,
                        unlocked = unlocked,
                        palette = palette,
                        onClick = { onTheme(option.id) }
                    )
                }
            }
        }

        item {
            SectionLabel("ABOUT THIS SAVE", palette)
            PixelPanel(palette, Modifier.fillMaxWidth(), borderColor = palette.secondary.copy(alpha = .5f)) {
                Text("SAVE FILE 1.0.0", color = palette.secondary, fontWeight = FontWeight.Black)
                Text("Bloc de notas y diario gamificado, local y sin cuentas.", color = palette.text)
                Spacer(Modifier.height(6.dp))
                Text("Tus textos viven únicamente en el dispositivo y en las copias de seguridad de Android que tú permitas.", color = palette.muted, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text("by ENTRE CHECKPOINTS", color = palette.primary, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun SettingToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    palette: SavePalette,
    onChecked: (Boolean) -> Unit
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(40.dp).background(palette.surfaceAlt).border(1.dp, palette.primary.copy(alpha = .5f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = palette.primary) }
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = palette.text, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Text(subtitle, color = palette.muted, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.background,
                checkedTrackColor = palette.primary,
                uncheckedThumbColor = palette.muted,
                uncheckedTrackColor = palette.surfaceAlt
            )
        )
    }
}

@Composable
private fun ThemeCard(
    option: SavePalette,
    selected: Boolean,
    unlocked: Boolean,
    palette: SavePalette,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (selected) option.primary.copy(alpha = .13f) else palette.surface)
            .border(2.dp, if (selected) option.primary else palette.primary.copy(alpha = .3f))
            .clickable(enabled = unlocked, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(48.dp).background(option.background).border(2.dp, option.primary),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.size(20.dp).background(option.primary).border(2.dp, option.accent))
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(option.name, color = if (unlocked) palette.text else palette.muted, fontWeight = FontWeight.Black, fontSize = 13.sp)
            Text(
                if (unlocked) {
                    if (selected) "EQUIPPED" else "TAP TO EQUIP"
                } else "UNLOCKS AT LV.${option.lockedUntilLevel}",
                color = if (selected) option.primary else palette.muted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(if (unlocked) Icons.Default.Palette else Icons.Default.Lock, null, tint = if (unlocked) option.primary else palette.muted)
    }
}
