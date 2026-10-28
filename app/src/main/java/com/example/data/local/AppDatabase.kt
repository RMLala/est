package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.bible.BibleRepositoryData
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SermonNote::class,
        PreachingPrep::class,
        TheologicalBook::class,
        PrayerItem::class,
        FaithDeclaration::class,
        VerseBookmark::class,
        DictionaryTermEntity::class,
        TestimonyEntity::class,
        TwoWayJournalEntry::class,
        CustomReadingPlanEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sermonNoteDao(): SermonNoteDao
    abstract fun preachingPrepDao(): PreachingPrepDao
    abstract fun theologicalBookDao(): TheologicalBookDao
    abstract fun prayerItemDao(): PrayerItemDao
    abstract fun faithDeclarationDao(): FaithDeclarationDao
    abstract fun verseBookmarkDao(): VerseBookmarkDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun testimonyDao(): TestimonyDao
    abstract fun twoWayJournalDao(): TwoWayJournalDao
    abstract fun customReadingPlanDao(): CustomReadingPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ecole_saint_esprit_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default data asynchronously
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            database.sermonNoteDao().insertAll(BibleRepositoryData.seedSermonNotes)
                            database.preachingPrepDao().insertAll(BibleRepositoryData.seedPreachingPreps)
                            database.theologicalBookDao().insertAll(BibleRepositoryData.seedBooks)
                            database.prayerItemDao().insertAll(BibleRepositoryData.seedPrayers)
                            database.faithDeclarationDao().insertAll(BibleRepositoryData.seedDeclarations)

                            // Seed dictionary terms from BibleRepositoryData
                            val dictEntities = BibleRepositoryData.dictionaryEntries.map { d ->
                                DictionaryTermEntity(
                                    term = d.term,
                                    originalWord = d.originalWord,
                                    language = d.language,
                                    strongRef = d.strongRef,
                                    transliteration = d.transliteration,
                                    definition = d.definition,
                                    theologicalSignificance = d.theologicalSignificance,
                                    keyVerses = d.keyVerses.joinToString(", ")
                                )
                            }
                            database.dictionaryDao().insertAll(dictEntities)

                            // Seed cross-source testimonies (Prière, Verset, Déclaration)
                            val seedTestimonies = listOf(
                                TestimonyEntity(
                                    title = "Restauration physique confirmée",
                                    sourceType = "PRIERE",
                                    sourceReference = "Prière pour la guérison de maman",
                                    story = "Après des semaines de prière fervente, les examens de contrôle sont redevenus impeccables. Les médecins n'ont trouvé aucune trace de lésion."
                                ),
                                TestimonyEntity(
                                    title = "Paix surnaturelle dans l'épreuve",
                                    sourceType = "VERSET",
                                    sourceReference = "Jean 14:27 (BDS)",
                                    story = "Alors que je faisais face à une crise professionnelle majeure, j'ai médité ce verset chaque matin. Le Saint-Esprit a rempli mon cœur d'un shalom inébranlable qui a ouvert une porte providentielle inespérée."
                                ),
                                TestimonyEntity(
                                    title = "Libération par la proclamation",
                                    sourceType = "DECLARATION",
                                    sourceReference = "Je suis Temple du Saint-Esprit",
                                    story = "En proclamant cette vérité à voix haute pendant 7 jours contre des oppressions de peur nocturne, la présence du Saint-Esprit a chassé toute anxiété. Gloire à Jésus !"
                                )
                            )
                            database.testimonyDao().insertAll(seedTestimonies)

                            // Seed Two-Way Journal entry
                            val seedJournal = listOf(
                                TwoWayJournalEntry(
                                    title = "Dialogue au lever du jour : Paix dans la tempête",
                                    myPrayer = "Seigneur, mon cœur était troublé ce matin face aux décisions ministérielles et aux défis qui s'accumulent. Je dépose toute cette charge à Tes pieds et je refuse de porter l'anxiété seul.",
                                    godsResponse = "Mon enfant, Je suis ton Berger. Rien de ce qui t'arrive ne M'échappe. Reste tranquille dans Ma présence, car ce n'est ni par la force ni par la puissance humaine, mais par Mon Esprit. Marche un pas à la fois, Ma grâce te suffit.",
                                    scriptureAnchor = "Jean 14:27; Zacharie 4:6",
                                    mood = "PAIX",
                                    moodIntensity = 5,
                                    category = "Intimité & Adoration",
                                    tags = "Paix, Direction, Repos",
                                    isBreakthrough = true,
                                    breakthroughNote = "Paix surnaturelle descendue au moment de la prière silencieuse."
                                ),
                                TwoWayJournalEntry(
                                    title = "Question de direction pour l'enseignement",
                                    myPrayer = "Saint-Esprit, comment guider au mieux les disciples ce trimestre ? Montre-moi les thèmes fondamentaux qui allumeront un réveil durable dans les cœurs.",
                                    godsResponse = "Enseigne-leur Mon amour et la plénitude du Paraclet. Quand ils comprendront qui Je suis en eux, la crainte disparaîtra et l'audace de la foi se manifestera naturellement.",
                                    scriptureAnchor = "Romains 8:14-16; Actes 1:8",
                                    mood = "ZELE",
                                    moodIntensity = 4,
                                    category = "Ministère & Appel",
                                    tags = "Ministère, Saint-Esprit, Révélation",
                                    isBreakthrough = false
                                )
                            )
                            database.twoWayJournalDao().insertAll(seedJournal)

                            // Seed a sample custom reading plan
                            val sampleCustomPlan = CustomReadingPlanEntity(
                                planId = "custom_plan_intimite_7j",
                                title = "7 Jours d'Intimité avec le Saint-Esprit",
                                categoryName = "Pneumatologie & Intimité",
                                description = "Parcours personnel pour cultiver la communion intime et sensible avec le Consolateur chaque matin.",
                                durationDays = 7,
                                daysJson = listOf(
                                    "1|La Présence Silencieuse|Jean 14:16-18|Méditer sur le Consolateur qui demeure pour toujours",
                                    "2|La Voix Douce et Subtile|1 Rois 19:11-13|Apprendre à faire taire le bruit pour écouter l'Esprit",
                                    "3|La Révélation des Profondeurs|1 Corinthiens 2:9-12|Sonder ce que Dieu a préparé pour ceux qui l'aiment",
                                    "4|Le Sceau d'Adoption|Romains 8:14-17|Vivre comme un fils et non comme un esclave",
                                    "5|L'Eau Vive Intérieure|Jean 7:37-39|Laisser jaillir les fleuves de bénédiction",
                                    "6|La Transformation à Son Image|2 Corinthiens 3:17-18|De gloire en gloire par le Seigneur, l'Esprit",
                                    "7|Revêtu pour Servir|Actes 10:38|Manifester la bonté et l'onction de Christ"
                                ).joinToString("\n")
                            )
                            database.customReadingPlanDao().insert(sampleCustomPlan)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
