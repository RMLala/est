package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.localization.AppLanguage
import com.example.data.localization.LanguageStrings
import com.example.data.model.FaithDeclaration
import com.example.data.model.SermonNote
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppTopBar
import com.example.ui.screens.BibleScreen
import com.example.ui.screens.DictionaryConcordanceScreen
import com.example.ui.screens.FaithDeclarationsScreen
import com.example.ui.screens.GeminiAssistantScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrayerJournalScreen
import com.example.ui.screens.PreachingPrepScreen
import com.example.ui.screens.ReadingPlansScreen
import com.example.ui.screens.SermonNotesScreen
import com.example.ui.screens.SyncProfileScreen
import com.example.ui.screens.ThemeCustomizerDialog
import com.example.ui.screens.TheologyLibraryScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val themePreset by mainViewModel.currentThemePreset.collectAsStateWithLifecycle()
            val fontFamily by mainViewModel.currentFontFamily.collectAsStateWithLifecycle()
            val fontColor by mainViewModel.currentFontColor.collectAsStateWithLifecycle()

            MyApplicationTheme(
                preset = themePreset,
                fontFamily = fontFamily,
                fontColor = fontColor
            ) {
                MainAppContent(viewModel = mainViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val currentThemePreset by viewModel.currentThemePreset.collectAsStateWithLifecycle()
    val currentFontFamily by viewModel.currentFontFamily.collectAsStateWithLifecycle()
    val currentFontColor by viewModel.currentFontColor.collectAsStateWithLifecycle()
    val studentProfile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val sermonNotes by viewModel.sermonNotes.collectAsStateWithLifecycle()
    val preachingPreps by viewModel.preachingPreps.collectAsStateWithLifecycle()
    val theologicalBooks by viewModel.theologicalBooks.collectAsStateWithLifecycle()
    val prayerItems by viewModel.prayerItems.collectAsStateWithLifecycle()
    val faithDeclarations by viewModel.faithDeclarations.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val dictionaryTerms by viewModel.dictionaryTerms.collectAsStateWithLifecycle()
    val testimonies by viewModel.testimonies.collectAsStateWithLifecycle()
    val twoWayJournalEntries by viewModel.twoWayJournalEntries.collectAsStateWithLifecycle()
    val customReadingPlans by viewModel.customReadingPlans.collectAsStateWithLifecycle()
    val recordingState by viewModel.recordingState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    val selectedBook by viewModel.selectedBook.collectAsStateWithLifecycle()
    val selectedChapter by viewModel.selectedChapter.collectAsStateWithLifecycle()
    val selectedVersion by viewModel.selectedVersion.collectAsStateWithLifecycle()
    val parallelMode by viewModel.parallelMode.collectAsStateWithLifecycle()
    val currentVerses by viewModel.currentVerses.collectAsStateWithLifecycle()

    val verseFontSize by viewModel.verseFontSize.collectAsStateWithLifecycle()
    val useSerifFont by viewModel.useSerifFont.collectAsStateWithLifecycle()

    val completedPlanDays by viewModel.completedPlanDays.collectAsStateWithLifecycle()
    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchFilter by viewModel.searchFilter.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    var showMoreBottomSheet by remember { mutableStateOf(false) }
    var showThemeCustomizerDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var passageForNewNote by remember { mutableStateOf("") }
    val isMg = currentLanguage == AppLanguage.MALAGASY

    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        if (!viewModel.handleBack()) {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        topBar = {
            AppTopBar(
                currentScreen = currentScreen,
                currentLanguage = currentLanguage,
                isGoogleSynced = studentProfile.isGoogleSynced,
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onOpenThemeCustomizer = { showThemeCustomizerDialog = true },
                onProfileClick = { viewModel.navigateTo(AppScreen.SYNC_PROFILE) }
            )
        },
        bottomBar = {
            AppBottomBar(
                currentScreen = currentScreen,
                currentLanguage = currentLanguage,
                onNavigate = { screen -> viewModel.navigateTo(screen) },
                onOpenMoreMenu = { showMoreBottomSheet = true }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    profile = studentProfile,
                    currentLanguage = currentLanguage,
                    recentNotes = sermonNotes,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    onVerseClick = { viewModel.navigateTo(AppScreen.BIBLE) }
                )

                AppScreen.BIBLE -> BibleScreen(
                    selectedBook = selectedBook,
                    selectedChapter = selectedChapter,
                    selectedVersion = selectedVersion,
                    parallelMode = parallelMode,
                    fontSizeSp = verseFontSize,
                    useSerifFont = useSerifFont,
                    verses = currentVerses,
                    bookmarks = bookmarks,
                    onSelectBook = { viewModel.selectBook(it) },
                    onSelectChapter = { viewModel.selectChapter(it) },
                    onSetVersion = { viewModel.setVersion(it) },
                    onToggleParallel = { viewModel.toggleParallelMode() },
                    onChangeFontSize = { viewModel.setVerseFontSize(it) },
                    onToggleFontFamily = { viewModel.toggleFontFamily() },
                    onSaveVerseHighlight = { verse, color, category, note ->
                        viewModel.saveVerseHighlightAndCategory(verse, color, category, note)
                    },
                    onRemoveBookmarkByCoordinates = { book, ch, vs ->
                        viewModel.removeBookmarkByCoordinates(book, ch, vs)
                    },
                    onCreateSermonFromVerse = { verse ->
                        passageForNewNote = "${verse.book} ${verse.chapter}:${verse.verse}"
                        viewModel.navigateTo(AppScreen.SERMON_NOTES)
                    },
                    onSaveTestimony = { viewModel.saveTestimony(it) }
                )

                AppScreen.READING_PLANS -> ReadingPlansScreen(
                    currentLanguage = currentLanguage,
                    customPlans = customReadingPlans,
                    completedDays = completedPlanDays,
                    onToggleDayCompleted = { planId, day -> viewModel.togglePlanDayCompleted(planId, day) },
                    onOpenPassage = { book, ch -> viewModel.navigateToBiblePassage(book, ch) },
                    onSaveCustomPlan = { viewModel.saveCustomReadingPlan(it) },
                    onDeleteCustomPlan = { viewModel.deleteCustomReadingPlan(it) }
                )

                AppScreen.ASSISTANT_GEMINI -> GeminiAssistantScreen(
                    currentLanguage = currentLanguage,
                    messages = aiMessages,
                    isLoading = isAiLoading,
                    onSendMessage = { prompt -> viewModel.sendAiPrompt(prompt) },
                    onCreateSermonFromAi = { aiText ->
                        viewModel.saveSermonNote(
                            SermonNote(
                                title = if (isMg) "Fandalinana nentin'i Gemini" else "Étude inspirée par Gemini",
                                preacher = "Conseiller Spirituel IA",
                                churchOrEvent = "École du Saint Esprit",
                                scriptureRef = "Jean 14:26",
                                content = aiText,
                                keyTakeaways = aiText.take(180),
                                tags = "Gemini, Saint-Esprit, Révélation"
                            )
                        )
                        viewModel.navigateTo(AppScreen.SERMON_NOTES)
                    },
                    onCreateDeclarationFromAi = { aiText ->
                        viewModel.saveDeclaration(
                            FaithDeclaration(
                                theme = if (isMg) "Finoana" else "Foi & Victoire",
                                title = if (isMg) "Fiekem-pinoana Gemini" else "Décret Spirituel Gemini",
                                declarationText = aiText.lines().firstOrNull { it.isNotBlank() } ?: aiText,
                                scriptureRef = "Romains 8 / Actes 1"
                            )
                        )
                        viewModel.navigateTo(AppScreen.DECLARATIONS)
                    }
                )

                AppScreen.SERMON_NOTES -> SermonNotesScreen(
                    notes = sermonNotes,
                    initialPassage = passageForNewNote,
                    recordingState = recordingState,
                    playerState = playerState,
                    onStartRecording = { viewModel.startAudioRecording(it) },
                    onPauseRecording = { viewModel.pauseAudioRecording() },
                    onResumeRecording = { viewModel.resumeAudioRecording() },
                    onStopRecording = { viewModel.stopAudioRecording() },
                    onCancelRecording = { viewModel.cancelAudioRecording() },
                    onTogglePlayAudio = { viewModel.togglePlayAudio(it) },
                    onSeekAudio = { viewModel.seekAudio(it) },
                    onSetAudioSpeed = { viewModel.setAudioSpeed(it) },
                    onSaveNote = { note ->
                        viewModel.saveSermonNote(note)
                        passageForNewNote = ""
                    },
                    onDeleteNote = { viewModel.deleteSermonNote(it) }
                )

                AppScreen.PREACHING_PREP -> PreachingPrepScreen(
                    preachings = preachingPreps,
                    onSavePreaching = { viewModel.savePreachingPrep(it) },
                    onDeletePreaching = { viewModel.deletePreachingPrep(it) }
                )

                AppScreen.LIBRARY -> TheologyLibraryScreen(
                    books = theologicalBooks,
                    onSaveBook = { viewModel.saveBook(it) },
                    onDeleteBook = { viewModel.deleteBook(it) }
                )

                AppScreen.PRAYER -> PrayerJournalScreen(
                    journalEntries = twoWayJournalEntries,
                    prayers = prayerItems,
                    testimonies = testimonies,
                    recordingState = recordingState,
                    playerState = playerState,
                    onStartRecording = { viewModel.startAudioRecording(it) },
                    onPauseRecording = { viewModel.pauseAudioRecording() },
                    onResumeRecording = { viewModel.resumeAudioRecording() },
                    onStopRecording = { viewModel.stopAudioRecording() },
                    onCancelRecording = { viewModel.cancelAudioRecording() },
                    onTogglePlayAudio = { viewModel.togglePlayAudio(it) },
                    onSeekAudio = { viewModel.seekAudio(it) },
                    onSetAudioSpeed = { viewModel.setAudioSpeed(it) },
                    onSaveJournalEntry = { viewModel.saveTwoWayJournal(it) },
                    onDeleteJournalEntry = { viewModel.deleteTwoWayJournal(it) },
                    onToggleJournalBreakthrough = { entry, note -> viewModel.toggleJournalBreakthrough(entry, note) },
                    onSavePrayer = { viewModel.savePrayer(it) },
                    onMarkAnswered = { prayer, testimony -> viewModel.markPrayerAnswered(prayer, testimony) },
                    onDeletePrayer = { viewModel.deletePrayer(it) },
                    onSaveTestimony = { viewModel.saveTestimony(it) },
                    onDeleteTestimony = { viewModel.deleteTestimony(it) }
                )

                AppScreen.DECLARATIONS -> FaithDeclarationsScreen(
                    declarations = faithDeclarations,
                    onIncrementRecitation = { viewModel.incrementDeclaration(it) },
                    onToggleFavorite = { viewModel.toggleDeclarationFavorite(it) },
                    onSaveDeclaration = { viewModel.saveDeclaration(it) },
                    onSaveTestimony = { viewModel.saveTestimony(it) }
                )

                AppScreen.DICTIONARY -> DictionaryConcordanceScreen(
                    roomTerms = dictionaryTerms,
                    onSaveTerm = { viewModel.saveDictionaryTerm(it) },
                    onDeleteTerm = { viewModel.deleteDictionaryTerm(it) }
                )

                AppScreen.SEARCH -> GlobalSearchScreen(
                    searchQuery = searchQuery,
                    searchFilter = searchFilter,
                    results = searchResults,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onFilterChange = { viewModel.setSearchFilter(it) },
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                AppScreen.SYNC_PROFILE -> SyncProfileScreen(
                    profile = studentProfile,
                    totalNotes = sermonNotes.size,
                    totalPreachings = preachingPreps.size,
                    totalBooks = theologicalBooks.size,
                    answeredPrayers = prayerItems.count { it.isAnswered },
                    onToggleSync = { viewModel.toggleGoogleSync(it) },
                    onTriggerSync = { viewModel.triggerManualSync() }
                )
            }
        }
    }

    // Modal Bottom Sheet with all modules
    if (showMoreBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isMg) "Ireo Fitaovana rehetra (Modules)" else "Modules de l'École du Saint-Esprit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                MoreMenuItem(
                    title = if (isMg) "Teti-pamakiana Baiboly" else "Plans de Lecture de la Bible",
                    subtitle = if (isMg) "Ara-potoana, Fanahy Masina, Finoana, Isan-taona" else "Chronologique, Saint-Esprit, Foi & Annuel",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    color = Color(0xFF059669),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMoreBottomSheet = false
                            viewModel.navigateTo(AppScreen.READING_PLANS)
                        }
                    }
                )

                MoreMenuItem(
                    title = if (isMg) "Mpanolo-tsaina Gemini IA" else "Conseiller Spirituel Gemini IA",
                    subtitle = if (isMg) "Fandinihana teny grika/hebreo, drafitra toriteny, vavaka" else "Exégèse biblique, homilétique & conducteur de prière",
                    icon = Icons.Default.AutoAwesome,
                    color = Color(0xFF0284C7),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMoreBottomSheet = false
                            viewModel.navigateTo(AppScreen.ASSISTANT_GEMINI)
                        }
                    }
                )

                MoreMenuItem(
                    title = if (isMg) "Endrika, Loko & Soratra" else "Thèmes, Couleurs & Polices",
                    subtitle = if (isMg) "Hanova ny lokon'ny fampiharana sy ny fiteny" else "Personnaliser l'apparence, polices et langue",
                    icon = Icons.Default.Palette,
                    color = Color(0xFFD97706),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMoreBottomSheet = false
                            showThemeCustomizerDialog = true
                        }
                    }
                )

                MoreMenuItem(
                    title = if (isMg) "Trano Fitehirizam-boky Teolojika" else "Bibliothèque Théologique",
                    subtitle = if (isMg) "Boky masina, fanazavana sy famintinana" else "Ouvrages des pères, pneumatologie & résumés",
                    icon = Icons.Default.AutoStories,
                    color = Color(0xFF4338CA),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMoreBottomSheet = false
                            viewModel.navigateTo(AppScreen.LIBRARY)
                        }
                    }
                )

                MoreMenuItem(
                    title = if (isMg) "Vavaka & Fijoroana Vavolombelona" else "Journal de Prière & Témoignages",
                    subtitle = if (isMg) "Efitrano fivavahana, fangatahana & asa mahagaga" else "Chambre haute, requêtes & livre des miracles",
                    icon = Icons.Default.VolunteerActivism,
                    color = Color(0xFF7C3AED),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMoreBottomSheet = false
                            viewModel.navigateTo(AppScreen.PRAYER)
                        }
                    }
                )

                MoreMenuItem(
                    title = if (isMg) "Fiekem-pinoana Velona" else "Déclarations de Foi Vivante",
                    subtitle = if (isMg) "Fitenenan-teny masina sy fampitomboana finoana" else "Décrets bibliques, proclamations & répétiteur",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFDC2626),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMoreBottomSheet = false
                            viewModel.navigateTo(AppScreen.DECLARATIONS)
                        }
                    }
                )

                MoreMenuItem(
                    title = if (isMg) "Rakibolana Baiboly Room & Concordance" else "Dictionnaire Biblique Room & Concordance",
                    subtitle = if (isMg) "Teny fototra Grika, Hebreo & Strong" else "Racines Grec, Hébreu, Strong & recherche interactive",
                    icon = Icons.Default.Translate,
                    color = Color(0xFF2563EB),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMoreBottomSheet = false
                            viewModel.navigateTo(AppScreen.DICTIONARY)
                        }
                    }
                )

                MoreMenuItem(
                    title = if (isMg) "Mombamomba & Fampitahana Google" else "Profil Étudiant & Sync Google",
                    subtitle = if (isMg) "Fiarovana rahona Google Drive sy antontan'isa" else "Sauvegarde Drive, série de jours & statistiques",
                    icon = Icons.Default.CloudDone,
                    color = Color(0xFF10B981),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showMoreBottomSheet = false
                            viewModel.navigateTo(AppScreen.SYNC_PROFILE)
                        }
                    }
                )
            }
        }
    }

    // Live Theme Customizer Dialog
    if (showThemeCustomizerDialog) {
        ThemeCustomizerDialog(
            currentLanguage = currentLanguage,
            currentThemePreset = currentThemePreset,
            currentFontFamily = currentFontFamily,
            currentFontColor = currentFontColor,
            currentFontSizeSp = verseFontSize,
            onSelectLanguage = { viewModel.setLanguage(it) },
            onSelectThemePreset = { viewModel.setThemePreset(it) },
            onSelectFontFamily = { viewModel.setFontFamily(it) },
            onSelectFontColor = { viewModel.setFontColor(it) },
            onSelectFontSizeSp = { viewModel.setVerseFontSize(it) },
            onDismiss = { showThemeCustomizerDialog = false }
        )
    }
}

@Composable
fun MoreMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
