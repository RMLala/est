package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.PrayerItem
import com.example.data.model.TestimonyEntity
import com.example.data.model.TwoWayJournalEntry
import com.example.ui.components.AudioPlayerCard
import com.example.ui.components.AudioRecordingWidget
import com.example.ui.components.FormattedContentText
import com.example.ui.components.RichTextFormattingBar
import com.example.util.AudioPlayerManager
import com.example.util.AudioPlayerState
import com.example.util.AudioRecordingState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val DEFAULT_PRAYER_CATEGORIES = listOf(
    "Famille & Foyer",
    "Santé & Guérison",
    "Salut des Âmes",
    "Combat Spirituel",
    "Ministère & Église",
    "Finances & Emploi",
    "Nation & Autorités",
    "Intercession Libre"
)

data class MoodDescriptor(
    val code: String,
    val emoji: String,
    val label: String,
    val color: Color
)

val APP_MOODS = listOf(
    MoodDescriptor("ZELE", "🔥", "Zélé & Enflammé", Color(0xFFEA580C)),
    MoodDescriptor("RECONNAISSANCE", "🙏", "Reconnaissance", Color(0xFFD97706)),
    MoodDescriptor("PAIX", "🕊️", "Paix & Shalom", Color(0xFF0284C7)),
    MoodDescriptor("COMBAT", "⚡", "Combat & Victoire", Color(0xFF7C3AED)),
    MoodDescriptor("ATTENTE", "⏳", "En Attente & Foi", Color(0xFF4F46E5)),
    MoodDescriptor("EPROUVE", "💧", "Besoin de Réconfort", Color(0xFF64748B)),
    MoodDescriptor("REVELATION", "💡", "Révélation & Clarté", Color(0xFF059669)),
    MoodDescriptor("AMOUR", "💖", "Amour & Intimité", Color(0xFFE11D48))
)

val DEFAULT_JOURNAL_CATEGORIES = listOf(
    "Intimité & Adoration",
    "Direction & Choix",
    "Combat Spirituel",
    "Guérison & Santé",
    "Famille & Couple",
    "Finances & Bénédiction",
    "Ministère & Appel",
    "Autre"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerJournalScreen(
    journalEntries: List<TwoWayJournalEntry>,
    prayers: List<PrayerItem>,
    testimonies: List<TestimonyEntity>,
    recordingState: AudioRecordingState,
    playerState: AudioPlayerState,
    onStartRecording: (String) -> String?,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onStopRecording: () -> Pair<String?, Int>,
    onCancelRecording: () -> Unit,
    onTogglePlayAudio: (String) -> Unit,
    onSeekAudio: (Int) -> Unit,
    onSetAudioSpeed: (Float) -> Unit,
    onSaveJournalEntry: (TwoWayJournalEntry) -> Unit,
    onDeleteJournalEntry: (TwoWayJournalEntry) -> Unit,
    onToggleJournalBreakthrough: (TwoWayJournalEntry, String) -> Unit,
    onSavePrayer: (PrayerItem) -> Unit,
    onMarkAnswered: (PrayerItem, String) -> Unit,
    onDeletePrayer: (PrayerItem) -> Unit,
    onSaveTestimony: (TestimonyEntity) -> Unit,
    onDeleteTestimony: (TestimonyEntity) -> Unit
) {
    val context = LocalContext.current
    var mainTab by remember { mutableIntStateOf(0) } // 0: Two-Way Journal, 1: Requêtes & Intercession, 2: Témoignages

    // Two-Way Journal Filter States
    var journalSearchQuery by remember { mutableStateOf("") }
    var selectedMoodFilter by remember { mutableStateOf("TOUTES") }
    var selectedJournalCategoryFilter by remember { mutableStateOf("TOUTES") }
    var filterBreakthroughOnly by remember { mutableStateOf(false) }

    // Dialog states
    var showAddJournalDialog by remember { mutableStateOf(false) }
    var editingJournalEntry by remember { mutableStateOf<TwoWayJournalEntry?>(null) }
    var showAddPrayerDialog by remember { mutableStateOf(false) }
    var showAddTestimonyDialog by remember { mutableStateOf(false) }
    var testimonyDialogPrayer by remember { mutableStateOf<PrayerItem?>(null) }
    var breakthroughDialogEntry by remember { mutableStateOf<TwoWayJournalEntry?>(null) }

    // Permission launcher for voice memo in journal
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartRecording("journal")
        } else {
            Toast.makeText(context, "Microphone requis pour enregistrer une note vocale", Toast.LENGTH_SHORT).show()
        }
    }

    val requestMicAndRecord = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            onStartRecording("journal")
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Filtered lists
    val filteredJournal = remember(journalEntries, journalSearchQuery, selectedMoodFilter, selectedJournalCategoryFilter, filterBreakthroughOnly) {
        journalEntries.filter { item ->
            val matchesQuery = if (journalSearchQuery.isBlank()) true else {
                item.title.contains(journalSearchQuery, ignoreCase = true) ||
                        item.myPrayer.contains(journalSearchQuery, ignoreCase = true) ||
                        item.godsResponse.contains(journalSearchQuery, ignoreCase = true) ||
                        item.scriptureAnchor.contains(journalSearchQuery, ignoreCase = true) ||
                        item.tags.contains(journalSearchQuery, ignoreCase = true) ||
                        item.category.contains(journalSearchQuery, ignoreCase = true)
            }
            val matchesMood = if (selectedMoodFilter == "TOUTES") true else item.mood == selectedMoodFilter
            val matchesCat = if (selectedJournalCategoryFilter == "TOUTES") true else item.category.equals(selectedJournalCategoryFilter, ignoreCase = true)
            val matchesBreakthrough = if (!filterBreakthroughOnly) true else item.isBreakthrough
            matchesQuery && matchesMood && matchesCat && matchesBreakthrough
        }
    }

    // Categorized Prayer List filters
    var selectedPrayerFilter by remember { mutableStateOf("TOUTES") }
    var selectedPrayerCategoryFilter by remember { mutableStateOf("TOUTES") }
    var prayerSearchQuery by remember { mutableStateOf("") }
    val filteredPrayers = remember(prayers, selectedPrayerFilter, selectedPrayerCategoryFilter, prayerSearchQuery) {
        prayers.filter { item ->
            val matchesQuery = if (prayerSearchQuery.isBlank()) true else {
                item.title.contains(prayerSearchQuery, ignoreCase = true) ||
                        item.requestDetails.contains(prayerSearchQuery, ignoreCase = true) ||
                        item.category.contains(prayerSearchQuery, ignoreCase = true)
            }
            val matchesStatus = when (selectedPrayerFilter) {
                "EN_ATTENTE" -> !item.isAnswered
                "EXAUCEES" -> item.isAnswered
                else -> true
            }
            val matchesCat = if (selectedPrayerCategoryFilter == "TOUTES") true else {
                item.category.equals(selectedPrayerCategoryFilter, ignoreCase = true)
            }
            matchesQuery && matchesStatus && matchesCat
        }
    }

    // Testimony filters
    var selectedTestimonyFilter by remember { mutableStateOf("TOUS") }
    val filteredTestimonies = remember(testimonies, selectedTestimonyFilter) {
        if (selectedTestimonyFilter == "TOUS") testimonies
        else testimonies.filter { it.sourceType == selectedTestimonyFilter }
    }

    val breakthroughCount = remember(journalEntries) { journalEntries.count { it.isBreakthrough } }
    val answeredCount = remember(prayers) { prayers.count { it.isAnswered } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (mainTab) {
                        0 -> {
                            editingJournalEntry = null
                            showAddJournalDialog = true
                        }
                        1 -> showAddPrayerDialog = true
                        2 -> showAddTestimonyDialog = true
                    }
                },
                modifier = Modifier.testTag("add_prayer_or_testimony_fab"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("prayer_journal_screen")
        ) {
            // Tab Row (Two-Way Journal, Requêtes, Témoignages)
            PrimaryTabRow(
                selectedTabIndex = mainTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = mainTab == 0,
                    onClick = { mainTab = 0 },
                    text = { Text("Two-Way Journal (${journalEntries.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(17.dp)) }
                )
                Tab(
                    selected = mainTab == 1,
                    onClick = { mainTab = 1 },
                    text = { Text("Requêtes (${prayers.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(17.dp)) }
                )
                Tab(
                    selected = mainTab == 2,
                    onClick = { mainTab = 2 },
                    text = { Text("Miracles (${testimonies.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(17.dp)) }
                )
            }

            // ----------------------------------------------------
            // TAB 0: TWO-WAY JOURNALING (Dialogue avec Dieu & Humeurs)
            // ----------------------------------------------------
            if (mainTab == 0) {
                // Active Audio recording banner if recording inside journal
                AnimatedVisibility(visible = recordingState.isRecording) {
                    AudioRecordingWidget(
                        recordingState = recordingState,
                        onPause = onPauseRecording,
                        onResume = onResumeRecording,
                        onStop = {
                            val (filePath, durationSec) = onStopRecording()
                            if (filePath != null) {
                                if (editingJournalEntry != null) {
                                    editingJournalEntry = editingJournalEntry?.copy(
                                        audioFilePath = filePath,
                                        audioDurationSeconds = durationSec
                                    )
                                } else {
                                    editingJournalEntry = TwoWayJournalEntry(
                                        title = "Prière vocale du " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                                        myPrayer = "Méditation enregistrée à voix haute...",
                                        godsResponse = "",
                                        audioFilePath = filePath,
                                        audioDurationSeconds = durationSec
                                    )
                                    showAddJournalDialog = true
                                }
                            }
                        },
                        onCancel = onCancelRecording,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Insights & Stats Banner
                Surface(
                    color = Color(0xFF7C3AED).copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Two-Way Journaling • Habacuc 2:1-2",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF5B21B6)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDDD6FE)) {
                                    Text(
                                        text = "PRO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF5B21B6),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Je dépose mon fardeau • J'écoute le murmure du Saint-Esprit",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF059669)
                        ) {
                            Text(
                                text = "$breakthroughCount percées ✨",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = journalSearchQuery,
                    onValueChange = { journalSearchQuery = it },
                    label = { Text("Rechercher dans les dialogues divins...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    singleLine = true
                )

                // Mood Filter Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            selected = selectedMoodFilter == "TOUTES",
                            onClick = { selectedMoodFilter = "TOUTES" },
                            label = { Text("Toutes Humeurs", fontSize = 11.sp) }
                        )
                    }

                    item {
                        FilterChip(
                            selected = filterBreakthroughOnly,
                            onClick = { filterBreakthroughOnly = !filterBreakthroughOnly },
                            label = { Text("✨ Percées ($breakthroughCount)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF059669),
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    items(APP_MOODS) { mood ->
                        val isSelected = selectedMoodFilter == mood.code
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMoodFilter = if (isSelected) "TOUTES" else mood.code },
                            label = {
                                Text("${mood.emoji} ${mood.label}", fontSize = 11.sp)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = mood.color.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                // Category Filter Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedJournalCategoryFilter == "TOUTES",
                            onClick = { selectedJournalCategoryFilter = "TOUTES" },
                            label = { Text("Toutes Catégories", fontSize = 11.sp) }
                        )
                    }
                    items(DEFAULT_JOURNAL_CATEGORIES) { cat ->
                        FilterChip(
                            selected = selectedJournalCategoryFilter == cat,
                            onClick = { selectedJournalCategoryFilter = if (selectedJournalCategoryFilter == cat) "TOUTES" else cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                if (filteredJournal.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(52.dp), tint = Color(0xFF7C3AED).copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aucun dialogue spirituel trouvé",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Commencez votre premier Two-Way Journaling en appuyant sur le bouton +",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredJournal, key = { it.id }) { entry ->
                            val moodDesc = APP_MOODS.find { it.code == entry.mood } ?: APP_MOODS[2]

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("journal_card_${entry.id}")
                                    .clickable {
                                        editingJournalEntry = entry
                                        showAddJournalDialog = true
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (entry.isBreakthrough) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Header: Title + Mood Pill + Breakthrough Badge
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = entry.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (entry.isBreakthrough) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFFDCFCE7),
                                                    modifier = Modifier.padding(end = 6.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text("Percée", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                                    }
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = moodDesc.color.copy(alpha = 0.15f)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                ) {
                                                    Text(moodDesc.emoji, fontSize = 12.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = moodDesc.label,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = moodDesc.color
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Category, Mood intensity (stars), Date
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${entry.category} • ",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.Medium
                                            )
                                            // Star rating
                                            Row {
                                                for (i in 1..5) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = if (i <= entry.moodIntensity) moodDesc.color else Color.LightGray,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(entry.dateMillis)),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (entry.scriptureAnchor.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Ancrage : ${entry.scriptureAnchor}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Two-Way Content: Part 1 - My Voice
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("🙋‍♂️", fontSize = 13.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Ma Prière / Ce que je dépose :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = entry.myPrayer,
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Two-Way Content: Part 2 - God's Response
                                    if (entry.godsResponse.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFFEF3C7),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("🕊️", fontSize = 13.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Ce que Dieu me répond (Saint-Esprit) :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = entry.godsResponse,
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp,
                                                    fontStyle = FontStyle.Italic,
                                                    color = Color(0xFF78350F)
                                                )
                                            }
                                        }
                                    }

                                    // Audio memo if recorded
                                    if (!entry.audioFilePath.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        AudioPlayerCard(
                                            filePath = entry.audioFilePath,
                                            title = "Prière audio (${AudioPlayerManager.formatTime(entry.audioDurationSeconds)})",
                                            durationSeconds = entry.audioDurationSeconds,
                                            playerState = playerState,
                                            onTogglePlay = { onTogglePlayAudio(entry.audioFilePath) },
                                            onSeek = onSeekAudio,
                                            onSetSpeed = onSetAudioSpeed,
                                            onDelete = {
                                                onSaveJournalEntry(entry.copy(audioFilePath = null, audioDurationSeconds = 0))
                                                Toast.makeText(context, "Note vocale supprimée", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }

                                    // Action buttons: Percée Spirituelle, Share, Delete
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                if (entry.isBreakthrough) {
                                                    onToggleJournalBreakthrough(entry, "")
                                                } else {
                                                    breakthroughDialogEntry = entry
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = if (entry.isBreakthrough) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(
                                                imageVector = if (entry.isBreakthrough) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (entry.isBreakthrough) "Percée validée !" else "Dieu a répondu !",
                                                fontSize = 11.sp
                                            )
                                        }

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val shareText = "Two-Way Journal : ${entry.title}\nHumeur : ${entry.mood} (${entry.category})\nAncrage : ${entry.scriptureAnchor}\n\n🙋‍♂️ Ma Prière :\n${entry.myPrayer}\n\n🕊️ Réponse de Dieu :\n${entry.godsResponse}"
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Journal", shareText))
                                                    Toast.makeText(context, "Journal copié !", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = "Partager", modifier = Modifier.size(17.dp))
                                            }

                                            IconButton(
                                                onClick = { onDeleteJournalEntry(entry) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(17.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // TAB 1: LISTE DE PRIÈRES CATÉGORISÉE (Intercession & Requêtes)
            // ----------------------------------------------------
            if (mainTab == 1) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Liste de Prières & Intercession",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Requêtes catégorisées • L'Esprit intercède avec nous",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF059669)
                        ) {
                            Text(
                                text = "$answeredCount exaucées ✨",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Prayer Search Bar
                OutlinedTextField(
                    value = prayerSearchQuery,
                    onValueChange = { prayerSearchQuery = it },
                    label = { Text("Rechercher une prière, un proche, une catégorie...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    singleLine = true
                )

                // Prayer Category Filters Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedPrayerCategoryFilter == "TOUTES",
                            onClick = { selectedPrayerCategoryFilter = "TOUTES" },
                            label = { Text("Toutes Catégories", fontSize = 11.sp) }
                        )
                    }
                    items(DEFAULT_PRAYER_CATEGORIES) { cat ->
                        FilterChip(
                            selected = selectedPrayerCategoryFilter == cat,
                            onClick = { selectedPrayerCategoryFilter = if (selectedPrayerCategoryFilter == cat) "TOUTES" else cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Status Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedPrayerFilter == "TOUTES",
                        onClick = { selectedPrayerFilter = "TOUTES" },
                        label = { Text("Toutes (${prayers.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedPrayerFilter == "EN_ATTENTE",
                        onClick = { selectedPrayerFilter = "EN_ATTENTE" },
                        label = { Text("En attente (${prayers.count { !it.isAnswered }})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedPrayerFilter == "EXAUCEES",
                        onClick = { selectedPrayerFilter = "EXAUCEES" },
                        label = { Text("✨ Exaucées ($answeredCount)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF059669),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                if (filteredPrayers.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Aucune requête de prière trouvée", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ajoutez un sujet de prière avec le bouton + ci-dessous", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredPrayers, key = { it.id }) { prayer ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("prayer_card_${prayer.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (prayer.isAnswered) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = prayer.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (prayer.isAnswered) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (prayer.isAnswered) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                                    contentDescription = null,
                                                    tint = if (prayer.isAnswered) Color(0xFF16A34A) else MaterialTheme.colorScheme.outline,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.size(4.dp))
                                                Text(
                                                    text = if (prayer.isAnswered) "Exaucée" else "En attente",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (prayer.isAnswered) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        ) {
                                            Text(
                                                text = prayer.category,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(prayer.dateMillis))}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    FormattedContentText(text = prayer.requestDetails)

                                    if (prayer.isAnswered && prayer.testimony.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFFEF3C7),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.size(4.dp))
                                                    Text("Témoignage d'Exaucement", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                FormattedContentText(text = prayer.testimony, color = Color(0xFF78350F))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            if (!prayer.isAnswered) {
                                                Button(
                                                    onClick = { testimonyDialogPrayer = prayer },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.size(4.dp))
                                                    Text("Dieu a exaucé !", fontSize = 11.sp)
                                                }
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    Toast.makeText(context, "🙏 Prière déposée pour : ${prayer.title} ! L'Esprit intercède avec vous.", Toast.LENGTH_SHORT).show()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("🙏 J'ai prié", fontSize = 11.sp)
                                            }
                                        }

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val shareText = "Sujet de prière : ${prayer.title}\nCatégorie : ${prayer.category}\n\n${prayer.requestDetails}"
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Priere", shareText))
                                                    Toast.makeText(context, "Sujet de prière copié !", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = "Partager", modifier = Modifier.size(16.dp))
                                            }

                                            IconButton(
                                                onClick = { onDeletePrayer(prayer) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // TAB 2: TÉMOIGNAGES DE GLOIRE
            // ----------------------------------------------------
            if (mainTab == 2) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Livre des Merveilles de Dieu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "Témoignages liés aux Versets, Déclarations, Two-Way Journaling & Prières",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                val testimonyTypes = listOf(
                    "TOUS" to "Tous les témoignages",
                    "VERSET" to "📜 Liés aux Versets",
                    "DECLARATION" to "🔥 Liés aux Déclarations",
                    "PRIERE" to "🙏 Liés aux Prières",
                    "LIBRE" to "✨ Percées du Journal"
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(testimonyTypes) { (type, label) ->
                        FilterChip(
                            selected = selectedTestimonyFilter == type,
                            onClick = { selectedTestimonyFilter = type },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                        )
                    }
                }

                if (filteredTestimonies.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Aucun témoignage dans cette catégorie", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Ajoutez un témoignage personnel ou reliez-en un depuis un verset biblique !", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredTestimonies) { testimony ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("testimony_card_${testimony.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = testimony.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (testimony.sourceType) {
                                                "VERSET" -> MaterialTheme.colorScheme.primaryContainer
                                                "DECLARATION" -> Color(0xFFFEE2E2)
                                                else -> Color(0xFFDCFCE7)
                                            }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = when (testimony.sourceType) {
                                                        "VERSET" -> Icons.Default.AutoAwesome
                                                        "DECLARATION" -> Icons.Default.RecordVoiceOver
                                                        else -> Icons.Default.VolunteerActivism
                                                    },
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = testimony.sourceType,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    if (testimony.sourceReference.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Source : ${testimony.sourceReference}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = testimony.story,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(testimony.dateMillis)),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        IconButton(onClick = { onDeleteTestimony(testimony) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ----------------------------------------------------
    // DIALOGS
    // ----------------------------------------------------

    // Two-Way Journal Add/Edit Dialog
    if (showAddJournalDialog) {
        var title by remember { mutableStateOf(editingJournalEntry?.title ?: "") }
        var mood by remember { mutableStateOf(editingJournalEntry?.mood ?: "PAIX") }
        var moodIntensity by remember { mutableIntStateOf(editingJournalEntry?.moodIntensity ?: 5) }
        var category by remember { mutableStateOf(editingJournalEntry?.category ?: "Intimité & Adoration") }
        var scriptureAnchor by remember { mutableStateOf(editingJournalEntry?.scriptureAnchor ?: "") }
        var myPrayer by remember { mutableStateOf(editingJournalEntry?.myPrayer ?: "") }
        var godsResponse by remember { mutableStateOf(editingJournalEntry?.godsResponse ?: "") }
        var tags by remember { mutableStateOf(editingJournalEntry?.tags ?: "Dialogue, Esprit") }
        var audioPath by remember { mutableStateOf(editingJournalEntry?.audioFilePath) }
        var audioDuration by remember { mutableStateOf(editingJournalEntry?.audioDurationSeconds ?: 0) }

        AlertDialog(
            onDismissRequest = {
                if (recordingState.isRecording) onCancelRecording()
                showAddJournalDialog = false
            },
            title = {
                Text(
                    text = if (editingJournalEntry == null) "Nouveau Two-Way Journaling" else "Modifier le Dialogue",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Titre du dialogue *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Mood selector
                    item {
                        Column {
                            Text("Humeur spirituelle du cœur :", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                APP_MOODS.forEach { m ->
                                    FilterChip(
                                        selected = mood == m.code,
                                        onClick = { mood = m.code },
                                        label = { Text("${m.emoji} ${m.label}", fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }

                    // Mood Intensity slider
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Intensité de l'humeur :", fontSize = 12.sp)
                                Text("$moodIntensity / 5", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = moodIntensity.toFloat(),
                                onValueChange = { moodIntensity = it.toInt() },
                                valueRange = 1f..5f,
                                steps = 3
                            )
                        }
                    }

                    // Category
                    item {
                        Column {
                            Text("Catégorie (Personnalisable) :", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DEFAULT_JOURNAL_CATEGORIES.forEach { cat ->
                                    FilterChip(
                                        selected = category == cat,
                                        onClick = { category = cat },
                                        label = { Text(cat, fontSize = 11.sp) }
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Nom de catégorie personnalisée") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = scriptureAnchor,
                            onValueChange = { scriptureAnchor = it },
                            label = { Text("Verset d'ancrage (ex: Jean 14:27, Romains 8:28)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Audio memo recorder / player inside dialog
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("🎙️ Note Vocale de Prière", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                if (recordingState.isRecording) {
                                    AudioRecordingWidget(
                                        recordingState = recordingState,
                                        onPause = onPauseRecording,
                                        onResume = onResumeRecording,
                                        onStop = {
                                            val (filePath, durationSec) = onStopRecording()
                                            if (filePath != null) {
                                                audioPath = filePath
                                                audioDuration = durationSec
                                            }
                                        },
                                        onCancel = onCancelRecording
                                    )
                                } else if (!audioPath.isNullOrBlank()) {
                                    AudioPlayerCard(
                                        filePath = audioPath!!,
                                        title = "Prière audio (${AudioPlayerManager.formatTime(audioDuration)})",
                                        durationSeconds = audioDuration,
                                        playerState = playerState,
                                        onTogglePlay = { onTogglePlayAudio(audioPath!!) },
                                        onSeek = onSeekAudio,
                                        onSetSpeed = onSetAudioSpeed,
                                        onDelete = {
                                            audioPath = null
                                            audioDuration = 0
                                        }
                                    )
                                } else {
                                    OutlinedButton(
                                        onClick = { requestMicAndRecord() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Enregistrer une prière audio", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Two-Way Journal Part 1
                    item {
                        Column {
                            RichTextFormattingBar(text = myPrayer, onTextChange = { myPrayer = it })
                            OutlinedTextField(
                                value = myPrayer,
                                onValueChange = { myPrayer = it },
                                label = { Text("🙋‍♂️ Ma Prière / Ce que je dépose devant Dieu *") },
                                placeholder = { Text("Exprimez librement votre cœur, fardeau, question ou reconnaissance...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4
                            )
                        }
                    }

                    // Two-Way Journal Part 2
                    item {
                        Column {
                            RichTextFormattingBar(text = godsResponse, onTextChange = { godsResponse = it })
                            OutlinedTextField(
                                value = godsResponse,
                                onValueChange = { godsResponse = it },
                                label = { Text("🕊️ La Voix du Saint-Esprit / Ce que Dieu me dit *") },
                                placeholder = { Text("Notez les versets, impressions saintes, paix ou instructions reçues...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            label = { Text("Mots-clés (ex: Intimité, Paix, Direction)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && myPrayer.isNotBlank()) {
                            if (recordingState.isRecording) {
                                val (savedPath, durationSec) = onStopRecording()
                                if (savedPath != null) {
                                    audioPath = savedPath
                                    audioDuration = durationSec
                                }
                            }

                            val newEntry = TwoWayJournalEntry(
                                id = editingJournalEntry?.id ?: 0L,
                                title = title,
                                myPrayer = myPrayer,
                                godsResponse = godsResponse,
                                scriptureAnchor = scriptureAnchor,
                                mood = mood,
                                moodIntensity = moodIntensity,
                                category = category.ifBlank { "Intimité & Adoration" },
                                tags = tags,
                                audioFilePath = audioPath,
                                audioDurationSeconds = audioDuration,
                                isBreakthrough = editingJournalEntry?.isBreakthrough ?: false,
                                breakthroughNote = editingJournalEntry?.breakthroughNote ?: "",
                                dateMillis = editingJournalEntry?.dateMillis ?: System.currentTimeMillis()
                            )
                            onSaveJournalEntry(newEntry)
                            showAddJournalDialog = false
                        }
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (recordingState.isRecording) onCancelRecording()
                    showAddJournalDialog = false
                }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Breakthrough confirmation dialog
    if (breakthroughDialogEntry != null) {
        var breakthroughNote by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { breakthroughDialogEntry = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFF16A34A))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirmer la Percée Spirituelle ✨")
                }
            },
            text = {
                Column {
                    Text("Dialogue : ${breakthroughDialogEntry?.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = breakthroughNote,
                        onValueChange = { breakthroughNote = it },
                        label = { Text("Comment le Seigneur a-t-il opéré la percée ?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        breakthroughDialogEntry?.let { onToggleJournalBreakthrough(it, breakthroughNote) }
                        breakthroughDialogEntry = null
                        Toast.makeText(context, "Gloire à Dieu ! Percée enregistrée dans vos Témoignages !", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("Confirmer la Percée")
                }
            },
            dismissButton = {
                TextButton(onClick = { breakthroughDialogEntry = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Add Direct Testimony Dialog
    if (showAddTestimonyDialog) {
        var tTitle by remember { mutableStateOf("") }
        var tSourceType by remember { mutableStateOf("VERSET") }
        var tSourceRef by remember { mutableStateOf("") }
        var tStory by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddTestimonyDialog = false },
            title = { Text("Nouveau Témoignage") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tTitle, onValueChange = { tTitle = it }, label = { Text("Titre du témoignage *") }, modifier = Modifier.fillMaxWidth())
                    Text("Type d'inspiration :", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("VERSET", "DECLARATION", "PRIERE", "LIBRE").forEach { type ->
                            FilterChip(
                                selected = tSourceType == type,
                                onClick = { tSourceType = type },
                                label = { Text(type, fontSize = 11.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = tSourceRef,
                        onValueChange = { tSourceRef = it },
                        label = { Text("Référence (Verset, Déclaration ou Requête)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    RichTextFormattingBar(text = tStory, onTextChange = { tStory = it })
                    OutlinedTextField(
                        value = tStory,
                        onValueChange = { tStory = it },
                        label = { Text("Récit de l'intervention divine *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tTitle.isNotBlank() && tStory.isNotBlank()) {
                            onSaveTestimony(
                                TestimonyEntity(
                                    title = tTitle,
                                    sourceType = tSourceType,
                                    sourceReference = tSourceRef,
                                    story = tStory
                                )
                            )
                            showAddTestimonyDialog = false
                        }
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTestimonyDialog = false }) { Text("Annuler") }
            }
        )
    }

    // Add Prayer Dialog
    if (showAddPrayerDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Famille & Foyer") }
        var details by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPrayerDialog = false },
            title = { Text("Nouveau Sujet de Prière") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Sujet de la prière *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Catégorie de la prière :", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DEFAULT_PRAYER_CATEGORIES.forEach { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat, fontSize = 11.sp) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Nom de catégorie personnalisée") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        RichTextFormattingBar(text = details, onTextChange = { details = it })
                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text("Détails de la requête & promesses d'appui *") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSavePrayer(
                                PrayerItem(
                                    title = title,
                                    category = category,
                                    requestDetails = details
                                )
                            )
                            showAddPrayerDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_prayer_button")
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPrayerDialog = false }) { Text("Annuler") }
            }
        )
    }

    // Answered prayer testimony Dialog
    if (testimonyDialogPrayer != null) {
        var testimonyText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { testimonyDialogPrayer = null },
            title = { Text("Gloire à Dieu ! Enregistrer le Témoignage") },
            text = {
                Column {
                    Text(text = "Prière : ${testimonyDialogPrayer?.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = testimonyText,
                        onValueChange = { testimonyText = it },
                        label = { Text("Comment le Seigneur a-t-il répondu ?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        testimonyDialogPrayer?.let { onMarkAnswered(it, testimonyText) }
                        testimonyDialogPrayer = null
                    }
                ) {
                    Text("Confirmer l'Exaucement")
                }
            },
            dismissButton = {
                TextButton(onClick = { testimonyDialogPrayer = null }) { Text("Annuler") }
            }
        )
    }
}
