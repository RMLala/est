package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CustomReadingPlanEntity
import com.example.data.model.DictionaryTermEntity
import com.example.data.model.FaithDeclaration
import com.example.data.model.PrayerItem
import com.example.data.model.PreachingPrep
import com.example.data.model.SermonNote
import com.example.data.model.TestimonyEntity
import com.example.data.model.TheologicalBook
import com.example.data.model.TwoWayJournalEntry
import com.example.data.model.VerseBookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface SermonNoteDao {
    @Query("SELECT * FROM sermon_notes ORDER BY dateMillis DESC")
    fun getAllNotes(): Flow<List<SermonNote>>

    @Query("SELECT * FROM sermon_notes WHERE title LIKE '%' || :query || '%' OR preacher LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY dateMillis DESC")
    fun searchNotes(query: String): Flow<List<SermonNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: SermonNote): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<SermonNote>)

    @Update
    suspend fun update(note: SermonNote)

    @Delete
    suspend fun delete(note: SermonNote)

    @Query("SELECT COUNT(*) FROM sermon_notes")
    suspend fun getCount(): Int
}

@Dao
interface PreachingPrepDao {
    @Query("SELECT * FROM preaching_preparations ORDER BY dateMillis DESC")
    fun getAllPreachings(): Flow<List<PreachingPrep>>

    @Query("SELECT * FROM preaching_preparations WHERE title LIKE '%' || :query || '%' OR mainPassage LIKE '%' || :query || '%' OR theme LIKE '%' || :query || '%' ORDER BY dateMillis DESC")
    fun searchPreachings(query: String): Flow<List<PreachingPrep>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prep: PreachingPrep): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(preps: List<PreachingPrep>)

    @Update
    suspend fun update(prep: PreachingPrep)

    @Delete
    suspend fun delete(prep: PreachingPrep)

    @Query("SELECT COUNT(*) FROM preaching_preparations")
    suspend fun getCount(): Int
}

@Dao
interface TheologicalBookDao {
    @Query("SELECT * FROM theological_books ORDER BY dateMillis DESC")
    fun getAllBooks(): Flow<List<TheologicalBook>>

    @Query("SELECT * FROM theological_books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY dateMillis DESC")
    fun searchBooks(query: String): Flow<List<TheologicalBook>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: TheologicalBook): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<TheologicalBook>)

    @Update
    suspend fun update(book: TheologicalBook)

    @Delete
    suspend fun delete(book: TheologicalBook)

    @Query("SELECT COUNT(*) FROM theological_books")
    suspend fun getCount(): Int
}

@Dao
interface PrayerItemDao {
    @Query("SELECT * FROM prayer_items ORDER BY dateMillis DESC")
    fun getAllPrayers(): Flow<List<PrayerItem>>

    @Query("SELECT * FROM prayer_items WHERE title LIKE '%' || :query || '%' OR requestDetails LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY dateMillis DESC")
    fun searchPrayers(query: String): Flow<List<PrayerItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prayer: PrayerItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prayers: List<PrayerItem>)

    @Update
    suspend fun update(prayer: PrayerItem)

    @Delete
    suspend fun delete(prayer: PrayerItem)

    @Query("SELECT COUNT(*) FROM prayer_items WHERE isAnswered = 1")
    suspend fun getAnsweredCount(): Int
}

@Dao
interface FaithDeclarationDao {
    @Query("SELECT * FROM faith_declarations ORDER BY recitationCount DESC")
    fun getAllDeclarations(): Flow<List<FaithDeclaration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(declaration: FaithDeclaration): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(declarations: List<FaithDeclaration>)

    @Update
    suspend fun update(declaration: FaithDeclaration)

    @Delete
    suspend fun delete(declaration: FaithDeclaration)
}

@Dao
interface VerseBookmarkDao {
    @Query("SELECT * FROM verse_bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<VerseBookmark>>

    @Query("SELECT * FROM verse_bookmarks WHERE categoryTag = :category ORDER BY timestamp DESC")
    fun getBookmarksByCategory(category: String): Flow<List<VerseBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: VerseBookmark): Long

    @Update
    suspend fun update(bookmark: VerseBookmark)

    @Query("DELETE FROM verse_bookmarks WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM verse_bookmarks WHERE book = :book AND chapter = :chapter AND verse = :verse")
    suspend fun deleteByCoordinates(book: String, chapter: Int, verse: Int)
}

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM bible_dictionary ORDER BY term ASC")
    fun getAllTerms(): Flow<List<DictionaryTermEntity>>

    @Query("SELECT * FROM bible_dictionary WHERE term LIKE '%' || :query || '%' OR originalWord LIKE '%' || :query || '%' OR transliteration LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%' OR strongRef LIKE '%' || :query || '%' OR theologicalSignificance LIKE '%' || :query || '%' ORDER BY term ASC")
    fun searchTerms(query: String): Flow<List<DictionaryTermEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(term: DictionaryTermEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(terms: List<DictionaryTermEntity>)

    @Update
    suspend fun update(term: DictionaryTermEntity)

    @Delete
    suspend fun delete(term: DictionaryTermEntity)

    @Query("SELECT COUNT(*) FROM bible_dictionary")
    suspend fun getCount(): Int
}

@Dao
interface TestimonyDao {
    @Query("SELECT * FROM testimonies ORDER BY dateMillis DESC")
    fun getAllTestimonies(): Flow<List<TestimonyEntity>>

    @Query("SELECT * FROM testimonies WHERE sourceType = :sourceType ORDER BY dateMillis DESC")
    fun getTestimoniesBySource(sourceType: String): Flow<List<TestimonyEntity>>

    @Query("SELECT * FROM testimonies WHERE title LIKE '%' || :query || '%' OR story LIKE '%' || :query || '%' OR sourceReference LIKE '%' || :query || '%' ORDER BY dateMillis DESC")
    fun searchTestimonies(query: String): Flow<List<TestimonyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(testimony: TestimonyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(testimonies: List<TestimonyEntity>)

    @Update
    suspend fun update(testimony: TestimonyEntity)

    @Delete
    suspend fun delete(testimony: TestimonyEntity)

    @Query("SELECT COUNT(*) FROM testimonies")
    suspend fun getCount(): Int
}

@Dao
interface TwoWayJournalDao {
    @Query("SELECT * FROM two_way_journal ORDER BY dateMillis DESC")
    fun getAllEntries(): Flow<List<TwoWayJournalEntry>>

    @Query("SELECT * FROM two_way_journal WHERE title LIKE '%' || :query || '%' OR myPrayer LIKE '%' || :query || '%' OR godsResponse LIKE '%' || :query || '%' OR scriptureAnchor LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY dateMillis DESC")
    fun searchEntries(query: String): Flow<List<TwoWayJournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: TwoWayJournalEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<TwoWayJournalEntry>)

    @Update
    suspend fun update(entry: TwoWayJournalEntry)

    @Delete
    suspend fun delete(entry: TwoWayJournalEntry)

    @Query("SELECT COUNT(*) FROM two_way_journal")
    suspend fun getCount(): Int
}

@Dao
interface CustomReadingPlanDao {
    @Query("SELECT * FROM custom_reading_plans ORDER BY dateCreatedMillis DESC")
    fun getAllPlans(): Flow<List<CustomReadingPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: CustomReadingPlanEntity): Long

    @Update
    suspend fun update(plan: CustomReadingPlanEntity)

    @Delete
    suspend fun delete(plan: CustomReadingPlanEntity)
}
