package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.localization.AppLanguage
import com.example.data.localization.LanguageStrings
import com.example.data.theme.AppFontColor
import com.example.data.theme.AppFontFamily
import com.example.data.theme.AppThemePreset

@Composable
fun ThemeCustomizerDialog(
    currentLanguage: AppLanguage,
    currentThemePreset: AppThemePreset,
    currentFontFamily: AppFontFamily,
    currentFontColor: AppFontColor,
    currentFontSizeSp: Int,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectThemePreset: (AppThemePreset) -> Unit,
    onSelectFontFamily: (AppFontFamily) -> Unit,
    onSelectFontColor: (AppFontColor) -> Unit,
    onSelectFontSizeSp: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val isMg = currentLanguage == AppLanguage.MALAGASY

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .testTag("theme_customizer_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = LanguageStrings.get("themes_and_fonts", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = LanguageStrings.get("customize_appearance", currentLanguage),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_theme_dialog")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Language Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMg) "Safidy ny Fiteny (Langue)" else "Langue de l'application",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        val selected = lang == currentLanguage
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectLanguage(lang) }
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = lang.flag, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = lang.displayName,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Theme Presets
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatPaint,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LanguageStrings.get("choose_theme", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppThemePreset.values().forEach { preset ->
                        val selected = preset == currentThemePreset
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectThemePreset(preset) }
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) preset.previewColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) preset.previewColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(preset.previewColor)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isMg) preset.displayNameMg else preset.displayNameFr,
                                        fontSize = 13.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sélectionné",
                                        tint = preset.previewColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Font Family Selection
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LanguageStrings.get("choose_font", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppFontFamily.values().forEach { font ->
                        val selected = font == currentFontFamily
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectFontFamily(font) }
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isMg) font.displayNameMg else font.displayNameFr,
                                    fontFamily = font.family,
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sélectionné",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Font Color
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LanguageStrings.get("choose_font_color", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppFontColor.values().take(4).forEach { colorOption ->
                        val selected = colorOption == currentFontColor
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectFontColor(colorOption) }
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(colorOption.colorValue ?: MaterialTheme.colorScheme.onSurface)
                                        .border(1.dp, Color.LightGray, CircleShape)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = colorOption.id.take(4).uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Font Size Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LanguageStrings.get("choose_font_size", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "${currentFontSizeSp} sp",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }

                Slider(
                    value = currentFontSizeSp.toFloat(),
                    onValueChange = { onSelectFontSizeSp(it.toInt()) },
                    valueRange = 13f..24f,
                    steps = 10,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("font_size_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
