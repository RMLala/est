package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sermon_notes")
data class SermonNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val preacher: String,
    val churchOrEvent: String,
    val scriptureRef: String,
    val content: String,
    val keyTakeaways: String,
    val tags: String, // comma-separated e.g. "SaintEsprit, Foi, Priere"
    val category: String = "Culte Dominical", // Culte Dominical, Étude Biblique, Conférence, Réveil, etc.
    val audioFilePath: String? = null,
    val audioDurationSeconds: Int = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

@Entity(tableName = "two_way_journal")
data class TwoWayJournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val myPrayer: String,             // Ce que je dépose / Ma voix
    val godsResponse: String,         // Ce que Dieu me dit / Murmure du Saint-Esprit
    val scriptureAnchor: String = "", // Verset biblique d'ancrage
    val mood: String = "PAIX",        // ZELE, RECONNAISSANCE, PAIX, COMBAT, ATTENTE, EPROUVE, REVELATION, AMOUR
    val moodIntensity: Int = 5,       // 1 à 5
    val category: String = "Intimité & Adoration", // Intimité, Direction, Combat, Santé, etc.
    val tags: String = "",
    val audioFilePath: String? = null,
    val audioDurationSeconds: Int = 0,
    val isBreakthrough: Boolean = false,
    val breakthroughNote: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

@Entity(tableName = "custom_reading_plans")
data class CustomReadingPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: String,
    val title: String,
    val categoryName: String,
    val description: String,
    val durationDays: Int,
    val daysJson: String, // format "dayNumber|title|passage|focus" per line
    val dateCreatedMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "preaching_preparations")
data class PreachingPrep(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val mainPassage: String,
    val theme: String,
    val targetAudience: String,
    val introduction: String,
    val pointOne: String,
    val pointTwo: String,
    val pointThree: String,
    val illustrations: String,
    val application: String,
    val altarCall: String,
    val durationMinutes: Int = 35,
    val dateMillis: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

@Entity(tableName = "theological_books")
data class TheologicalBook(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val category: String, // Pneumatologie, Théologie Systématique, Histoire des Réveils, etc.
    val summary: String,
    val status: String = "En cours", // À lire, En cours, Lu
    val rating: Int = 5,
    val keyQuote: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

@Entity(tableName = "prayer_items")
data class PrayerItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Intercession, Ministère, Guérison, Foi, Action de grâce
    val requestDetails: String,
    val isAnswered: Boolean = false,
    val answeredDateMillis: Long? = null,
    val testimony: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

@Entity(tableName = "faith_declarations")
data class FaithDeclaration(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val theme: String, // Identité, Puissance du Saint-Esprit, Guérison, Victoire, Direction
    val title: String,
    val declarationText: String,
    val scriptureRef: String,
    val recitationCount: Int = 0,
    val isFavorite: Boolean = false,
    val dateMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "verse_bookmarks")
data class VerseBookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val version: String,
    val text: String,
    val highlightColor: String = "GOLD", // NONE, GOLD, BLUE, GREEN, ROSE, PURPLE
    val categoryTag: String = "Saint-Esprit", // Saint-Esprit, Foi, Guérison, Paix, Combat, etc.
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// Room Entity for Biblical Dictionary
@Entity(tableName = "bible_dictionary")
data class DictionaryTermEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val term: String,
    val originalWord: String,
    val language: String, // Grec, Hébreu
    val strongRef: String,
    val transliteration: String,
    val definition: String,
    val theologicalSignificance: String,
    val keyVerses: String,
    val isCustom: Boolean = false
)

// Room Entity for Unified Testimonies (linked to Verses, Declarations, Prayers, or Life events)
@Entity(tableName = "testimonies")
data class TestimonyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val sourceType: String, // VERSET, DECLARATION, PRIERE, LIBRE
    val sourceReference: String, // e.g. "Jean 14:26", "Je suis Temple du Saint-Esprit", etc.
    val story: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isSynced: Boolean = true
)

data class StudentProfile(
    val studentName: String = "Disciple du Saint-Esprit",
    val email: String = "etudiant.bible@gmail.com",
    val studyStreakDays: Int = 18,
    val notesCount: Int = 12,
    val preachingsCount: Int = 4,
    val booksCount: Int = 8,
    val answeredPrayersCount: Int = 6,
    val isGoogleSynced: Boolean = true,
    val lastSyncTime: String = "Aujourd'hui à 11:45"
)
