package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import com.example.data.theme.AppFontColor
import com.example.data.theme.AppFontFamily
import com.example.data.theme.AppThemePreset
import com.example.data.theme.ThemeFactory

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    preset: AppThemePreset = AppThemePreset.OR_CELESTE,
    fontFamily: AppFontFamily = AppFontFamily.SERIF_BIBLIQUE,
    fontColor: AppFontColor = AppFontColor.AUTOMATIQUE,
    content: @Composable () -> Unit
) {
    val baseScheme = ThemeFactory.getColorScheme(preset, darkTheme)
    val colorScheme = if (fontColor.colorValue != null) {
        baseScheme.copy(
            onBackground = fontColor.colorValue,
            onSurface = fontColor.colorValue
        )
    } else {
        baseScheme
    }

    val dynamicTypography = Typography(
        bodyLarge = Typography.bodyLarge.copy(fontFamily = fontFamily.family),
        bodyMedium = Typography.bodyMedium.copy(fontFamily = fontFamily.family),
        bodySmall = Typography.bodySmall.copy(fontFamily = fontFamily.family),
        titleLarge = Typography.titleLarge.copy(fontFamily = fontFamily.family),
        titleMedium = Typography.titleMedium.copy(fontFamily = fontFamily.family),
        titleSmall = Typography.titleSmall.copy(fontFamily = fontFamily.family),
        headlineLarge = Typography.headlineLarge.copy(fontFamily = fontFamily.family),
        headlineMedium = Typography.headlineMedium.copy(fontFamily = fontFamily.family),
        headlineSmall = Typography.headlineSmall.copy(fontFamily = fontFamily.family),
        labelLarge = Typography.labelLarge.copy(fontFamily = fontFamily.family),
        labelMedium = Typography.labelMedium.copy(fontFamily = fontFamily.family),
        labelSmall = Typography.labelSmall.copy(fontFamily = fontFamily.family)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = dynamicTypography,
        content = content
    )
}
