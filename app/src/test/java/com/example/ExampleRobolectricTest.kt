package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.ChatMessage
import com.example.data.ai.GeminiAssistantService
import com.example.data.localization.AppLanguage
import com.example.data.localization.LanguageStrings
import com.example.data.readingplan.PlanCategory
import com.example.data.readingplan.ReadingPlanRepository
import com.example.data.theme.AppThemePreset
import com.example.data.theme.ThemeFactory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("École du Saint Esprit", appName)
    }

    @Test
    fun `reading plans repository provides all six categories and days`() {
        val plans = ReadingPlanRepository.allPlans
        assertEquals(6, plans.size)

        val categories = plans.map { it.category }.toSet()
        assertTrue(categories.contains(PlanCategory.CHRONOLOGIQUE))
        assertTrue(categories.contains(PlanCategory.THEMATIQUE))
        assertTrue(categories.contains(PlanCategory.NOUVEAU_TESTAMENT))
        assertTrue(categories.contains(PlanCategory.ANNUEL))

        plans.forEach { plan ->
            assertTrue(plan.days.isNotEmpty())
            assertNotNull(plan.titleFr)
            assertNotNull(plan.titleMg)
        }
    }

    @Test
    fun `localization provides French and Malagasy translations`() {
        val frAppName = LanguageStrings.get("app_name", AppLanguage.FRANCAIS)
        val mgAppName = LanguageStrings.get("app_name", AppLanguage.MALAGASY)

        assertEquals("École du Saint Esprit", frAppName)
        assertEquals("Sekolin'ny Fanahy Masina", mgAppName)

        val frAssistant = LanguageStrings.get("assistant_gemini", AppLanguage.FRANCAIS)
        val mgAssistant = LanguageStrings.get("assistant_gemini", AppLanguage.MALAGASY)

        assertEquals("Assistant IA Gemini", frAssistant)
        assertEquals("Mpanolo-tsaina Gemini IA", mgAssistant)
    }

    @Test
    fun `theme factory returns valid colors for all presets`() {
        AppThemePreset.values().forEach { preset ->
            val lightScheme = ThemeFactory.getColorScheme(preset, isDark = false)
            val darkScheme = ThemeFactory.getColorScheme(preset, isDark = true)
            assertNotNull(lightScheme)
            assertNotNull(darkScheme)
        }
    }

    @Test
    fun `gemini assistant service provides spiritual guidance fallback`() = runBlocking {
        val responseFr = GeminiAssistantService.generateAssistantResponse(
            history = emptyList(),
            userPrompt = "Plan de prédication sur le Saint-Esprit",
            language = AppLanguage.FRANCAIS
        )
        assertTrue(responseFr.contains("Plan de Prédication") || responseFr.contains("Saint-Esprit"))

        val responseMg = GeminiAssistantService.generateAssistantResponse(
            history = emptyList(),
            userPrompt = "Drafitra toriteny momba ny Fanahy Masina",
            language = AppLanguage.MALAGASY
        )
        assertTrue(responseMg.contains("Toriteny") || responseMg.contains("Fanahy Masina"))
    }

    @Test
    fun `audio player manager formats time correctly`() {
        assertEquals("00:00", com.example.util.AudioPlayerManager.formatTime(0))
        assertEquals("01:05", com.example.util.AudioPlayerManager.formatTime(65))
        assertEquals("10:30", com.example.util.AudioPlayerManager.formatTime(630))
        assertEquals("01:05", com.example.util.AudioPlayerManager.formatTimeMs(65000))
    }

    @Test
    fun `two-way journal entry supports moods and breakthrough`() {
        val entry = com.example.data.model.TwoWayJournalEntry(
            title = "Mon Dialogue Spirituel",
            myPrayer = "Seigneur, donne-moi ta sagesse.",
            godsResponse = "Ma sagesse est au-delà de toute intelligence humaine.",
            mood = "PAIX",
            moodIntensity = 5,
            category = "Intimité & Adoration",
            isBreakthrough = true,
            breakthroughNote = "Dieu a répondu par un shalom profond"
        )
        assertEquals("PAIX", entry.mood)
        assertEquals(5, entry.moodIntensity)
        assertTrue(entry.isBreakthrough)
        assertEquals("Intimité & Adoration", entry.category)
    }

    @Test
    fun `sermon note supports audio recording link and customizable category`() {
        val note = com.example.data.model.SermonNote(
            title = "L'Onction du Paraclet",
            preacher = "Pasteur David",
            churchOrEvent = "Culte de Réveil",
            scriptureRef = "Jean 14:16-26",
            content = "L'Esprit de vérité demeure en vous.",
            keyTakeaways = "Prendre du temps chaque jour pour la communion avec le Paraclet.",
            tags = "SaintEsprit, Onction",
            category = "Pneumatologie & Réveil",
            audioFilePath = "/data/user/0/com.example/files/audio_records/sermon_123.m4a",
            audioDurationSeconds = 2450
        )
        assertEquals("Pneumatologie & Réveil", note.category)
        assertNotNull(note.audioFilePath)
        assertEquals(2450, note.audioDurationSeconds)
    }
}
