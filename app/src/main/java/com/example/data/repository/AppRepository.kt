package com.example.data.repository

import com.example.data.bible.BibleRepositoryData
import com.example.data.bible.BibleVerse
import com.example.data.bible.BibleVersion
import com.example.data.bible.ConcordanceTopic
import com.example.data.local.AppDatabase
import com.example.data.model.CustomReadingPlanEntity
import com.example.data.model.DictionaryTermEntity
import com.example.data.model.FaithDeclaration
import com.example.data.model.PrayerItem
import com.example.data.model.PreachingPrep
import com.example.data.model.SermonNote
import com.example.data.model.StudentProfile
import com.example.data.model.TestimonyEntity
import com.example.data.model.TheologicalBook
import com.example.data.model.TwoWayJournalEntry
import com.example.data.model.VerseBookmark
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class SearchResultItem {
    data class VerseResult(val verse: BibleVerse, val matchedText: String, val version: BibleVersion) : SearchResultItem()
    data class NoteResult(val note: SermonNote) : SearchResultItem()
    data class PreachingResult(val prep: PreachingPrep) : SearchResultItem()
    data class BookResult(val book: TheologicalBook) : SearchResultItem()
    data class PrayerResult(val prayer: PrayerItem) : SearchResultItem()
    data class JournalResult(val entry: TwoWayJournalEntry) : SearchResultItem()
    data class DictResult(val entry: DictionaryTermEntity) : SearchResultItem()
    data class TestimonyResult(val testimony: TestimonyEntity) : SearchResultItem()
    data class ConcordanceResult(val topic: ConcordanceTopic) : SearchResultItem()
}

class AppRepository(private val database: AppDatabase) {

    val sermonNotes: Flow<List<SermonNote>> = database.sermonNoteDao().getAllNotes()
    val preachingPreps: Flow<List<PreachingPrep>> = database.preachingPrepDao().getAllPreachings()
    val theologicalBooks: Flow<List<TheologicalBook>> = database.theologicalBookDao().getAllBooks()
    val prayerItems: Flow<List<PrayerItem>> = database.prayerItemDao().getAllPrayers()
    val faithDeclarations: Flow<List<FaithDeclaration>> = database.faithDeclarationDao().getAllDeclarations()
    val bookmarks: Flow<List<VerseBookmark>> = database.verseBookmarkDao().getAllBookmarks()
    val dictionaryTerms: Flow<List<DictionaryTermEntity>> = database.dictionaryDao().getAllTerms()
    val testimonies: Flow<List<TestimonyEntity>> = database.testimonyDao().getAllTestimonies()
    val twoWayJournalEntries: Flow<List<TwoWayJournalEntry>> = database.twoWayJournalDao().getAllEntries()
    val customReadingPlans: Flow<List<CustomReadingPlanEntity>> = database.customReadingPlanDao().getAllPlans()

    private val _studentProfile = MutableStateFlow(
        StudentProfile(
            studentName = "Pasteur & Disciple Étudiant",
            email = "ecole.saintesprit@gmail.com",
            studyStreakDays = 18,
            notesCount = 14,
            preachingsCount = 5,
            booksCount = 8,
            answeredPrayersCount = 7,
            isGoogleSynced = true,
            lastSyncTime = "Aujourd'hui à " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )
    )
    val studentProfile = _studentProfile.asStateFlow()

    // Notes
    suspend fun saveSermonNote(note: SermonNote) {
        if (note.id == 0L) {
            database.sermonNoteDao().insert(note)
        } else {
            database.sermonNoteDao().update(note)
        }
        triggerAutoSync()
    }

    suspend fun deleteSermonNote(note: SermonNote) {
        database.sermonNoteDao().delete(note)
    }

    // Preaching Prep
    suspend fun savePreachingPrep(prep: PreachingPrep) {
        if (prep.id == 0L) {
            database.preachingPrepDao().insert(prep)
        } else {
            database.preachingPrepDao().update(prep)
        }
        triggerAutoSync()
    }

    suspend fun deletePreachingPrep(prep: PreachingPrep) {
        database.preachingPrepDao().delete(prep)
    }

    // Books
    suspend fun saveBook(book: TheologicalBook) {
        if (book.id == 0L) {
            database.theologicalBookDao().insert(book)
        } else {
            database.theologicalBookDao().update(book)
        }
        triggerAutoSync()
    }

    suspend fun deleteBook(book: TheologicalBook) {
        database.theologicalBookDao().delete(book)
    }

    // Prayers
    suspend fun savePrayer(prayer: PrayerItem) {
        if (prayer.id == 0L) {
            database.prayerItemDao().insert(prayer)
        } else {
            database.prayerItemDao().update(prayer)
        }
        triggerAutoSync()
    }

    suspend fun markPrayerAnswered(prayer: PrayerItem, testimony: String) {
        database.prayerItemDao().update(
            prayer.copy(
                isAnswered = true,
                answeredDateMillis = System.currentTimeMillis(),
                testimony = testimony
            )
        )
        // Also save to unified testimonies
        if (testimony.isNotBlank()) {
            saveTestimony(
                TestimonyEntity(
                    title = "Exaucement : ${prayer.title}",
                    sourceType = "PRIERE",
                    sourceReference = prayer.title,
                    story = testimony
                )
            )
        }
        triggerAutoSync()
    }

    suspend fun deletePrayer(prayer: PrayerItem) {
        database.prayerItemDao().delete(prayer)
    }

    // Testimonies (Unified across Verses, Declarations, Prayers)
    suspend fun saveTestimony(testimony: TestimonyEntity) {
        if (testimony.id == 0L) {
            database.testimonyDao().insert(testimony)
        } else {
            database.testimonyDao().update(testimony)
        }
        triggerAutoSync()
    }

    suspend fun deleteTestimony(testimony: TestimonyEntity) {
        database.testimonyDao().delete(testimony)
    }

    // Two-Way Journaling
    suspend fun saveTwoWayJournal(entry: TwoWayJournalEntry) {
        if (entry.id == 0L) {
            database.twoWayJournalDao().insert(entry)
        } else {
            database.twoWayJournalDao().update(entry)
        }
        triggerAutoSync()
    }

    suspend fun deleteTwoWayJournal(entry: TwoWayJournalEntry) {
        database.twoWayJournalDao().delete(entry)
    }

    suspend fun toggleJournalBreakthrough(entry: TwoWayJournalEntry, breakthroughNote: String = "") {
        val updated = entry.copy(
            isBreakthrough = !entry.isBreakthrough,
            breakthroughNote = if (!entry.isBreakthrough && breakthroughNote.isNotBlank()) breakthroughNote else entry.breakthroughNote
        )
        database.twoWayJournalDao().update(updated)
        // If breakthrough achieved and note exists, also link to Testimonies!
        if (updated.isBreakthrough && (breakthroughNote.isNotBlank() || updated.godsResponse.isNotBlank())) {
            saveTestimony(
                TestimonyEntity(
                    title = "Percée : ${updated.title}",
                    sourceType = "LIBRE",
                    sourceReference = updated.scriptureAnchor.ifBlank { "Dialogue avec le Saint-Esprit" },
                    story = if (breakthroughNote.isNotBlank()) breakthroughNote else updated.godsResponse
                )
            )
        }
        triggerAutoSync()
    }

    // Custom Reading Plans
    suspend fun saveCustomReadingPlan(plan: CustomReadingPlanEntity) {
        if (plan.id == 0L) {
            database.customReadingPlanDao().insert(plan)
        } else {
            database.customReadingPlanDao().update(plan)
        }
        triggerAutoSync()
    }

    suspend fun deleteCustomReadingPlan(plan: CustomReadingPlanEntity) {
        database.customReadingPlanDao().delete(plan)
    }

    // Dictionary (Room Persisted)
    fun searchDictionary(query: String): Flow<List<DictionaryTermEntity>> {
        return if (query.isBlank()) database.dictionaryDao().getAllTerms()
        else database.dictionaryDao().searchTerms(query)
    }

    suspend fun saveDictionaryTerm(term: DictionaryTermEntity) {
        if (term.id == 0L) {
            database.dictionaryDao().insert(term)
        } else {
            database.dictionaryDao().update(term)
        }
    }

    suspend fun deleteDictionaryTerm(term: DictionaryTermEntity) {
        database.dictionaryDao().delete(term)
    }

    // Declarations
    suspend fun incrementDeclarationRecitation(declaration: FaithDeclaration) {
        database.faithDeclarationDao().update(
            declaration.copy(recitationCount = declaration.recitationCount + 1)
        )
    }

    suspend fun saveDeclaration(declaration: FaithDeclaration) {
        if (declaration.id == 0L) {
            database.faithDeclarationDao().insert(declaration)
        } else {
            database.faithDeclarationDao().update(declaration)
        }
    }

    suspend fun toggleDeclarationFavorite(declaration: FaithDeclaration) {
        database.faithDeclarationDao().update(
            declaration.copy(isFavorite = !declaration.isFavorite)
        )
    }

    // Verse Bookmarks & Highlights & Categorization
    suspend fun saveBookmark(bookmark: VerseBookmark) {
        database.verseBookmarkDao().insert(bookmark)
    }

    suspend fun removeBookmark(id: Long) {
        database.verseBookmarkDao().delete(id)
    }

    suspend fun removeBookmarkByCoordinates(book: String, chapter: Int, verse: Int) {
        database.verseBookmarkDao().deleteByCoordinates(book, chapter, verse)
    }

    // Sync trigger
    fun triggerAutoSync() {
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        _studentProfile.value = _studentProfile.value.copy(
            lastSyncTime = "À l'instant ($timeStr)",
            isGoogleSynced = true
        )
    }

    fun toggleGoogleSync(enabled: Boolean) {
        _studentProfile.value = _studentProfile.value.copy(
            isGoogleSynced = enabled,
            lastSyncTime = if (enabled) "Synchronisé à " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) else "Désactivé"
        )
    }

    // Universal Search across everything (including Room dictionary terms, testimonies, and two-way journal)
    fun searchUniversal(
        query: String,
        currentNotes: List<SermonNote>,
        currentPreachings: List<PreachingPrep>,
        currentBooks: List<TheologicalBook>,
        currentPrayers: List<PrayerItem>,
        currentDict: List<DictionaryTermEntity>,
        currentTestimonies: List<TestimonyEntity>,
        currentJournal: List<TwoWayJournalEntry> = emptyList()
    ): List<SearchResultItem> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase(Locale.getDefault())
        val results = mutableListOf<SearchResultItem>()

        // 1. Search Bible Verses (Malagasy, Semeur, Amplified)
        for (v in BibleRepositoryData.verses) {
            if (v.textMalagasy.lowercase(Locale.getDefault()).contains(q) ||
                "${v.book} ${v.chapter}:${v.verse}".lowercase(Locale.getDefault()).contains(q)
            ) {
                results.add(SearchResultItem.VerseResult(v, v.textMalagasy, BibleVersion.MALAGASY))
            }
            if (v.textSemeur.lowercase(Locale.getDefault()).contains(q)) {
                results.add(SearchResultItem.VerseResult(v, v.textSemeur, BibleVersion.SEMEUR))
            }
            if (v.textAmplified.lowercase(Locale.getDefault()).contains(q)) {
                results.add(SearchResultItem.VerseResult(v, v.textAmplified, BibleVersion.AMPLIFIED))
            }
        }

        // 2. Search Room Dictionary Terms
        for (dict in currentDict) {
            if (dict.term.lowercase(Locale.getDefault()).contains(q) ||
                dict.originalWord.lowercase(Locale.getDefault()).contains(q) ||
                dict.transliteration.lowercase(Locale.getDefault()).contains(q) ||
                dict.definition.lowercase(Locale.getDefault()).contains(q) ||
                dict.theologicalSignificance.lowercase(Locale.getDefault()).contains(q) ||
                dict.strongRef.lowercase(Locale.getDefault()).contains(q)
            ) {
                results.add(SearchResultItem.DictResult(dict))
            }
        }

        // 3. Search Concordance
        for (conc in BibleRepositoryData.concordanceTopics) {
            if (conc.topic.lowercase(Locale.getDefault()).contains(q) ||
                conc.category.lowercase(Locale.getDefault()).contains(q) ||
                conc.description.lowercase(Locale.getDefault()).contains(q) ||
                conc.references.any { it.lowercase(Locale.getDefault()).contains(q) }
            ) {
                results.add(SearchResultItem.ConcordanceResult(conc))
            }
        }

        // 4. Search Testimonies
        for (t in currentTestimonies) {
            if (t.title.lowercase(Locale.getDefault()).contains(q) ||
                t.story.lowercase(Locale.getDefault()).contains(q) ||
                t.sourceReference.lowercase(Locale.getDefault()).contains(q)
            ) {
                results.add(SearchResultItem.TestimonyResult(t))
            }
        }

        // 5. Search Notes
        for (n in currentNotes) {
            if (n.title.lowercase(Locale.getDefault()).contains(q) ||
                n.preacher.lowercase(Locale.getDefault()).contains(q) ||
                n.scriptureRef.lowercase(Locale.getDefault()).contains(q) ||
                n.content.lowercase(Locale.getDefault()).contains(q) ||
                n.tags.lowercase(Locale.getDefault()).contains(q)
            ) {
                results.add(SearchResultItem.NoteResult(n))
            }
        }

        // 6. Search Preachings
        for (p in currentPreachings) {
            if (p.title.lowercase(Locale.getDefault()).contains(q) ||
                p.mainPassage.lowercase(Locale.getDefault()).contains(q) ||
                p.theme.lowercase(Locale.getDefault()).contains(q) ||
                p.introduction.lowercase(Locale.getDefault()).contains(q) ||
                p.pointOne.lowercase(Locale.getDefault()).contains(q)
            ) {
                results.add(SearchResultItem.PreachingResult(p))
            }
        }

        // 7. Search Theological Books
        for (b in currentBooks) {
            if (b.title.lowercase(Locale.getDefault()).contains(q) ||
                b.author.lowercase(Locale.getDefault()).contains(q) ||
                b.category.lowercase(Locale.getDefault()).contains(q) ||
                b.summary.lowercase(Locale.getDefault()).contains(q)
            ) {
                results.add(SearchResultItem.BookResult(b))
            }
        }

        // 8. Search Prayers
        for (pr in currentPrayers) {
            if (pr.title.lowercase(Locale.getDefault()).contains(q) ||
                pr.category.lowercase(Locale.getDefault()).contains(q) ||
                pr.requestDetails.lowercase(Locale.getDefault()).contains(q) ||
                pr.testimony.lowercase(Locale.getDefault()).contains(q)
            ) {
                results.add(SearchResultItem.PrayerResult(pr))
            }
        }

        // 9. Search Two-Way Journal Entries
        for (j in currentJournal) {
            if (j.title.lowercase(Locale.getDefault()).contains(q) ||
                j.myPrayer.lowercase(Locale.getDefault()).contains(q) ||
                j.godsResponse.lowercase(Locale.getDefault()).contains(q) ||
                j.scriptureAnchor.lowercase(Locale.getDefault()).contains(q) ||
                j.category.lowercase(Locale.getDefault()).contains(q) ||
                j.tags.lowercase(Locale.getDefault()).contains(q)
            ) {
                results.add(SearchResultItem.JournalResult(j))
            }
        }

        return results
    }
}
