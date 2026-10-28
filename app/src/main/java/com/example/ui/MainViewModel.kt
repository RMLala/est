package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ChatMessage
import com.example.data.ai.GeminiAssistantService
import com.example.data.ai.MessageSender
import com.example.data.bible.BibleRepositoryData
import com.example.data.bible.BibleVerse
import com.example.data.bible.BibleVersion
import com.example.data.local.AppDatabase
import com.example.data.localization.AppLanguage
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
import com.example.data.repository.AppRepository
import com.example.data.repository.SearchResultItem
import com.example.data.theme.AppFontColor
import com.example.data.theme.AppFontFamily
import com.example.data.theme.AppThemePreset
import com.example.util.AudioPlayerManager
import com.example.util.AudioPlayerState
import com.example.util.AudioRecorderManager
import com.example.util.AudioRecordingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val iconLabel: String) {
    HOME("Accueil", "Tableau de bord"),
    BIBLE("Sainte Bible", "Malagasy - Semeur - AMP"),
    SERMON_NOTES("Notes de Sermon", "Prise de note"),
    PREACHING_PREP("Homilétique", "Préparation prédication"),
    LIBRARY("Bibliothèque", "Livres théologiques"),
    PRAYER("Prières & Témoignages", "Chambre haute"),
    DECLARATIONS("Foi & Paroles", "Déclarations"),
    DICTIONARY("Dictionnaire", "Grec, Hébreu & Concordance"),
    READING_PLANS("Plans de Lecture", "Teti-pamakiana Baiboly"),
    ASSISTANT_GEMINI("Assistant Gemini", "Mpanolo-tsaina IA"),
    SEARCH("Recherche", "Recherche universelle"),
    SYNC_PROFILE("Profil & Cloud", "Synchronisation Google")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = AppRepository(db)

    // Language & Appearance states
    private val _currentLanguage = MutableStateFlow(AppLanguage.FRANCAIS)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentThemePreset = MutableStateFlow(AppThemePreset.OR_CELESTE)
    val currentThemePreset: StateFlow<AppThemePreset> = _currentThemePreset.asStateFlow()

    private val _currentFontFamily = MutableStateFlow(AppFontFamily.SERIF_BIBLIQUE)
    val currentFontFamily: StateFlow<AppFontFamily> = _currentFontFamily.asStateFlow()

    private val _currentFontColor = MutableStateFlow(AppFontColor.AUTOMATIQUE)
    val currentFontColor: StateFlow<AppFontColor> = _currentFontColor.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun setThemePreset(preset: AppThemePreset) {
        _currentThemePreset.value = preset
    }

    fun setFontFamily(font: AppFontFamily) {
        _currentFontFamily.value = font
    }

    fun setFontColor(color: AppFontColor) {
        _currentFontColor.value = color
    }

    // Current screen navigation
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenBackStack = mutableListOf<AppScreen>()

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (_screenBackStack.isNotEmpty()) {
            _currentScreen.value = _screenBackStack.removeAt(_screenBackStack.size - 1)
            return true
        }
        return false
    }

    // Data streams
    val sermonNotes = repository.sermonNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val preachingPreps = repository.preachingPreps.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val theologicalBooks = repository.theologicalBooks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val prayerItems = repository.prayerItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val faithDeclarations = repository.faithDeclarations.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val bookmarks = repository.bookmarks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val dictionaryTerms = repository.dictionaryTerms.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val testimonies = repository.testimonies.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val twoWayJournalEntries = repository.twoWayJournalEntries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val customReadingPlans = repository.customReadingPlans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val studentProfile = repository.studentProfile

    // Audio Recorder & Player
    val audioRecorderManager = AudioRecorderManager(application)
    val recordingState: StateFlow<AudioRecordingState> = audioRecorderManager.recordingState

    val audioPlayerManager = AudioPlayerManager(application)
    val playerState: StateFlow<AudioPlayerState> = audioPlayerManager.playerState

    // Bible reader state
    private val _selectedBook = MutableStateFlow("Jean")
    val selectedBook: StateFlow<String> = _selectedBook.asStateFlow()

    private val _selectedChapter = MutableStateFlow(14)
    val selectedChapter: StateFlow<Int> = _selectedChapter.asStateFlow()

    private val _selectedVersion = MutableStateFlow(BibleVersion.MALAGASY)
    val selectedVersion: StateFlow<BibleVersion> = _selectedVersion.asStateFlow()

    private val _parallelMode = MutableStateFlow(false)
    val parallelMode: StateFlow<Boolean> = _parallelMode.asStateFlow()

    // Bible formatting options
    private val _verseFontSize = MutableStateFlow(16) // 14 to 24 sp
    val verseFontSize: StateFlow<Int> = _verseFontSize.asStateFlow()

    private val _useSerifFont = MutableStateFlow(true)
    val useSerifFont: StateFlow<Boolean> = _useSerifFont.asStateFlow()

    fun setVerseFontSize(sizeSp: Int) {
        _verseFontSize.value = sizeSp
    }

    fun toggleFontFamily() {
        _useSerifFont.value = !_useSerifFont.value
    }

    fun selectBook(book: String) {
        _selectedBook.value = book
        val availableChapters = BibleRepositoryData.verses
            .filter { it.book == book }
            .map { it.chapter }
            .distinct()
        if (availableChapters.isNotEmpty() && !availableChapters.contains(_selectedChapter.value)) {
            _selectedChapter.value = availableChapters.first()
        }
    }

    fun selectChapter(chapter: Int) {
        _selectedChapter.value = chapter
    }

    fun setVersion(version: BibleVersion) {
        _selectedVersion.value = version
    }

    fun toggleParallelMode() {
        _parallelMode.value = !_parallelMode.value
    }

    fun navigateToBiblePassage(book: String, chapter: Int) {
        selectBook(book)
        selectChapter(chapter)
        navigateTo(AppScreen.BIBLE)
    }

    val currentVerses: StateFlow<List<BibleVerse>> = combine(_selectedBook, _selectedChapter) { book, chapter ->
        val list = BibleRepositoryData.verses.filter { it.book == book && it.chapter == chapter }
        if (list.isEmpty()) {
            val inBook = BibleRepositoryData.verses.filter { it.book == book }
            if (inBook.isNotEmpty()) inBook else BibleRepositoryData.verses.take(6)
        } else {
            list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reading Plans progress state
    private val _completedPlanDays = MutableStateFlow<Set<String>>(
        setOf("plan_saint_esprit_day_1")
    )
    val completedPlanDays: StateFlow<Set<String>> = _completedPlanDays.asStateFlow()

    fun togglePlanDayCompleted(planId: String, day: Int) {
        val key = "${planId}_day_${day}"
        val current = _completedPlanDays.value.toMutableSet()
        if (current.contains(key)) {
            current.remove(key)
        } else {
            current.add(key)
        }
        _completedPlanDays.value = current
    }

    // Gemini AI Assistant state
    private val _aiMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.ASSISTANT,
                text = "Shalom ! Je suis votre conseiller spirituel propulsé par Gemini. Comment puis-je vous guider aujourd'hui dans votre exégèse, préparation de sermon ou intercession ?"
            )
        )
    )
    val aiMessages: StateFlow<List<ChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun sendAiPrompt(prompt: String) {
        val userMsg = ChatMessage(sender = MessageSender.USER, text = prompt)
        val currentHistory = _aiMessages.value + userMsg
        _aiMessages.value = currentHistory
        _isAiLoading.value = true

        viewModelScope.launch {
            try {
                val responseText = GeminiAssistantService.generateAssistantResponse(
                    history = currentHistory,
                    userPrompt = prompt,
                    language = _currentLanguage.value
                )
                val assistantMsg = ChatMessage(sender = MessageSender.ASSISTANT, text = responseText)
                _aiMessages.value = _aiMessages.value + assistantMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    sender = MessageSender.ASSISTANT,
                    text = "Que la grâce du Seigneur soit avec vous. Une erreur s'est produite : ${e.localizedMessage}"
                )
                _aiMessages.value = _aiMessages.value + errorMsg
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow("TOUS")
    val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilter(filter: String) {
        _searchFilter.value = filter
    }

    val searchResults: StateFlow<List<SearchResultItem>> = combine(
        _searchQuery,
        sermonNotes,
        preachingPreps,
        theologicalBooks,
        prayerItems
    ) { query, notes, preps, books, prayers ->
        repository.searchUniversal(
            query = query,
            currentNotes = notes,
            currentPreachings = preps,
            currentBooks = books,
            currentPrayers = prayers,
            currentDict = dictionaryTerms.value,
            currentTestimonies = testimonies.value,
            currentJournal = twoWayJournalEntries.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun saveSermonNote(note: SermonNote) = viewModelScope.launch {
        repository.saveSermonNote(note)
    }

    fun deleteSermonNote(note: SermonNote) = viewModelScope.launch {
        repository.deleteSermonNote(note)
    }

    fun savePreachingPrep(prep: PreachingPrep) = viewModelScope.launch {
        repository.savePreachingPrep(prep)
    }

    fun deletePreachingPrep(prep: PreachingPrep) = viewModelScope.launch {
        repository.deletePreachingPrep(prep)
    }

    fun saveBook(book: TheologicalBook) = viewModelScope.launch {
        repository.saveBook(book)
    }

    fun deleteBook(book: TheologicalBook) = viewModelScope.launch {
        repository.deleteBook(book)
    }

    fun savePrayer(prayer: PrayerItem) = viewModelScope.launch {
        repository.savePrayer(prayer)
    }

    fun markPrayerAnswered(prayer: PrayerItem, testimony: String) = viewModelScope.launch {
        repository.markPrayerAnswered(prayer, testimony)
    }

    fun deletePrayer(prayer: PrayerItem) = viewModelScope.launch {
        repository.deletePrayer(prayer)
    }

    fun saveTestimony(testimony: TestimonyEntity) = viewModelScope.launch {
        repository.saveTestimony(testimony)
    }

    fun deleteTestimony(testimony: TestimonyEntity) = viewModelScope.launch {
        repository.deleteTestimony(testimony)
    }

    fun saveDictionaryTerm(term: DictionaryTermEntity) = viewModelScope.launch {
        repository.saveDictionaryTerm(term)
    }

    fun deleteDictionaryTerm(term: DictionaryTermEntity) = viewModelScope.launch {
        repository.deleteDictionaryTerm(term)
    }

    fun incrementDeclaration(decl: FaithDeclaration) = viewModelScope.launch {
        repository.incrementDeclarationRecitation(decl)
    }

    fun toggleDeclarationFavorite(decl: FaithDeclaration) = viewModelScope.launch {
        repository.toggleDeclarationFavorite(decl)
    }

    fun saveDeclaration(decl: FaithDeclaration) = viewModelScope.launch {
        repository.saveDeclaration(decl)
    }

    // Verse formatting, highlighting & categorizing
    fun saveVerseHighlightAndCategory(
        verse: BibleVerse,
        highlightColor: String,
        categoryTag: String,
        note: String
    ) = viewModelScope.launch {
        repository.saveBookmark(
            VerseBookmark(
                book = verse.book,
                chapter = verse.chapter,
                verse = verse.verse,
                version = _selectedVersion.value.name,
                text = verse.getText(_selectedVersion.value),
                highlightColor = highlightColor,
                categoryTag = categoryTag,
                note = note
            )
        )
    }

    fun removeBookmark(id: Long) = viewModelScope.launch {
        repository.removeBookmark(id)
    }

    fun removeBookmarkByCoordinates(book: String, chapter: Int, verse: Int) = viewModelScope.launch {
        repository.removeBookmarkByCoordinates(book, chapter, verse)
    }

    fun toggleGoogleSync(enabled: Boolean) {
        repository.toggleGoogleSync(enabled)
    }

    fun triggerManualSync() {
        repository.triggerAutoSync()
    }

    // Audio recording & playback controls
    fun startAudioRecording(prefix: String = "sermon"): String? {
        return audioRecorderManager.startRecording(prefix)
    }

    fun pauseAudioRecording() {
        audioRecorderManager.pauseRecording()
    }

    fun resumeAudioRecording() {
        audioRecorderManager.resumeRecording()
    }

    fun stopAudioRecording(): Pair<String?, Int> {
        return audioRecorderManager.stopRecording()
    }

    fun cancelAudioRecording() {
        audioRecorderManager.cancelRecording()
    }

    fun togglePlayAudio(filePath: String) {
        audioPlayerManager.togglePlayPause(filePath)
    }

    fun seekAudio(positionMs: Int) {
        audioPlayerManager.seekTo(positionMs)
    }

    fun setAudioSpeed(speed: Float) {
        audioPlayerManager.setSpeed(speed)
    }

    fun stopAudio() {
        audioPlayerManager.stop()
    }

    // Two-Way Journaling actions
    fun saveTwoWayJournal(entry: TwoWayJournalEntry) = viewModelScope.launch {
        repository.saveTwoWayJournal(entry)
    }

    fun deleteTwoWayJournal(entry: TwoWayJournalEntry) = viewModelScope.launch {
        repository.deleteTwoWayJournal(entry)
    }

    fun toggleJournalBreakthrough(entry: TwoWayJournalEntry, note: String = "") = viewModelScope.launch {
        repository.toggleJournalBreakthrough(entry, note)
    }

    // Custom Reading Plan actions
    fun saveCustomReadingPlan(plan: CustomReadingPlanEntity) = viewModelScope.launch {
        repository.saveCustomReadingPlan(plan)
    }

    fun deleteCustomReadingPlan(plan: CustomReadingPlanEntity) = viewModelScope.launch {
        repository.deleteCustomReadingPlan(plan)
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorderManager.cancelRecording()
        audioPlayerManager.release()
    }
}
