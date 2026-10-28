package com.example.data.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

enum class AppThemePreset(val id: String, val displayNameFr: String, val displayNameMg: String, val previewColor: Color) {
    OR_CELESTE("or_celeste", "Or Céleste (Classique)", "Volamena any An-danitra", Color(0xFFD97706)),
    PARCHEMIN_SACRE("parchemin", "Parchemin Sacré (Sépia)", "Soratra Masina (Taratasy fahiny)", Color(0xFF854D0E)),
    FEU_REVEIL("feu_reveil", "Feu du Réveil (Ambre & Feu)", "Afon'ny Fanahy Masina", Color(0xFFDC2626)),
    NUIT_PRIERE("nuit_priere", "Nuit de Prière (Bleu Nuit)", "Alim-bavaka (Manga Lalina)", Color(0xFF1E3A8A)),
    FORET_EDEN("foret_eden", "Jardin d'Éden (Vert Grâce)", "Sahan'i Edena (Maintso Fahasoavana)", Color(0xFF059669))
}

enum class AppFontFamily(val id: String, val displayNameFr: String, val displayNameMg: String, val family: FontFamily) {
    SERIF_BIBLIQUE("serif", "📜 Sérif Biblique (Majestueux)", "📜 Soratra Sérif Masina", FontFamily.Serif),
    SANS_SERIF_MODERNE("sans", "📱 Sans-Sérif Moderne (Épuré)", "📱 Soratra Maoderina", FontFamily.SansSerif),
    MONOSPACE_ETUDE("mono", "🔍 Monospace d'Étude (Précis)", "🔍 Soratra Fandalinana", FontFamily.Monospace),
    CURSIVE_SPIRITUELLE("cursive", "✨ Cursive d'Inspiration", "✨ Soratra Kanto", FontFamily.Cursive)
}

enum class AppFontColor(val id: String, val displayNameFr: String, val displayNameMg: String, val colorValue: Color?) {
    AUTOMATIQUE("auto", "Automatique (Selon Thème)", "Mifanaraka amin'ny loko", null),
    NOIR_ENCRE("noir", "Noir Encre Profond", "Mainty Manga Ranomainty", Color(0xFF0F172A)),
    BRUN_SEPIA("sepia", "Brun Sépia Ancien", "Mavokely / Volon-tany Sépia", Color(0xFF451A03)),
    BLEU_NUIT("bleu_nuit", "Bleu Nuit Spirituel", "Manga Lalina", Color(0xFF1E3A8A)),
    OR_DOUX("or_doux", "Or Chaud d'Onction", "Volamena Mafana", Color(0xFFB45309)),
    BLANC_ETOILE("blanc", "Blanc Étoile (Mode Sombre)", "Fotsy Kintana", Color(0xFFF8FAFC))
}

object ThemeFactory {

    fun getColorScheme(preset: AppThemePreset, isDark: Boolean): ColorScheme {
        return when (preset) {
            AppThemePreset.OR_CELESTE -> if (isDark) {
                darkColorScheme(
                    primary = Color(0xFFF59E0B),
                    onPrimary = Color(0xFF1B1F3B),
                    primaryContainer = Color(0xFF452D02),
                    onPrimaryContainer = Color(0xFFFEF3C7),
                    secondary = Color(0xFF93C5FD),
                    background = Color(0xFF0F172A),
                    surface = Color(0xFF1E293B),
                    surfaceVariant = Color(0xFF334155),
                    onSurface = Color(0xFFF8FAFC)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFFD97706),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFEF3C7),
                    onPrimaryContainer = Color(0xFF78350F),
                    secondary = Color(0xFF2563EB),
                    background = Color(0xFFFAF8F5),
                    surface = Color(0xFFFFFFFF),
                    surfaceVariant = Color(0xFFF3EFE6),
                    onSurface = Color(0xFF1E293B)
                )
            }

            AppThemePreset.PARCHEMIN_SACRE -> if (isDark) {
                darkColorScheme(
                    primary = Color(0xFFEAB308),
                    onPrimary = Color(0xFF291B00),
                    primaryContainer = Color(0xFF3B2805),
                    background = Color(0xFF1C1917),
                    surface = Color(0xFF292524),
                    surfaceVariant = Color(0xFF44403C),
                    onSurface = Color(0xFFF5F5F4)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF854D0E),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFEF08A),
                    onPrimaryContainer = Color(0xFF422006),
                    secondary = Color(0xFF78350F),
                    background = Color(0xFFFDFBF7),
                    surface = Color(0xFFF5EFE6),
                    surfaceVariant = Color(0xFFE7DEC8),
                    onSurface = Color(0xFF292524)
                )
            }

            AppThemePreset.FEU_REVEIL -> if (isDark) {
                darkColorScheme(
                    primary = Color(0xFFEF4444),
                    onPrimary = Color(0xFF450A0A),
                    primaryContainer = Color(0xFF7F1D1D),
                    secondary = Color(0xFFF97316),
                    background = Color(0xFF180A0A),
                    surface = Color(0xFF2A1212),
                    surfaceVariant = Color(0xFF451A1A),
                    onSurface = Color(0xFFFEF2F2)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFFDC2626),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFEE2E2),
                    onPrimaryContainer = Color(0xFF7F1D1D),
                    secondary = Color(0xFFEA580C),
                    background = Color(0xFFFFF7ED),
                    surface = Color(0xFFFFFFFF),
                    surfaceVariant = Color(0xFFFFEDD5),
                    onSurface = Color(0xFF431407)
                )
            }

            AppThemePreset.NUIT_PRIERE -> if (isDark) {
                darkColorScheme(
                    primary = Color(0xFF60A5FA),
                    onPrimary = Color(0xFF082F49),
                    primaryContainer = Color(0xFF1E3A8A),
                    secondary = Color(0xFF38BDF8),
                    background = Color(0xFF030712),
                    surface = Color(0xFF0F172A),
                    surfaceVariant = Color(0xFF1E293B),
                    onSurface = Color(0xFFF0F9FF)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF1E3A8A),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFDBEAFE),
                    onPrimaryContainer = Color(0xFF172554),
                    secondary = Color(0xFF0284C7),
                    background = Color(0xFFF0F9FF),
                    surface = Color(0xFFFFFFFF),
                    surfaceVariant = Color(0xFFE0F2FE),
                    onSurface = Color(0xFF082F49)
                )
            }

            AppThemePreset.FORET_EDEN -> if (isDark) {
                darkColorScheme(
                    primary = Color(0xFF34D399),
                    onPrimary = Color(0xFF064E3B),
                    primaryContainer = Color(0xFF065F46),
                    secondary = Color(0xFF10B981),
                    background = Color(0xFF06231A),
                    surface = Color(0xFF0F382A),
                    surfaceVariant = Color(0xFF1A4F3C),
                    onSurface = Color(0xFFECFDF5)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF059669),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFD1FAE5),
                    onPrimaryContainer = Color(0xFF064E3B),
                    secondary = Color(0xFF0D9488),
                    background = Color(0xFFF0FDF4),
                    surface = Color(0xFFFFFFFF),
                    surfaceVariant = Color(0xFFDCFCE7),
                    onSurface = Color(0xFF064E3B)
                )
            }
        }
    }
}
