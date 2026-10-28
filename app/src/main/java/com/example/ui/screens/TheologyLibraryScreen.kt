package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TheologicalBook
import com.example.ui.components.FormattedContentText
import com.example.ui.components.RichTextFormattingBar

data class StudyDocumentChapter(
    val title: String,
    val content: String
)

data class StudyEBook(
    val id: String,
    val title: String,
    val author: String,
    val format: String, // "EPUB" or "PDF"
    val category: String,
    val totalPages: Int,
    val description: String,
    val chapters: List<StudyDocumentChapter>
)

data class TeachingMedia(
    val id: String,
    val title: String,
    val teacher: String,
    val mediaType: String, // "VIDEO" or "AUDIO"
    val mediaUrl: String,
    val category: String,
    val durationText: String,
    val description: String
)

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun TheologyLibraryScreen(
    books: List<TheologicalBook>,
    onSaveBook: (TheologicalBook) -> Unit,
    onDeleteBook: (TheologicalBook) -> Unit
) {
    val context = LocalContext.current
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: Ouvrages, 1: Lecteur PDF & EPUB, 2: Vidéos & Audios

    // Tab 0 states
    var selectedCategory by remember { mutableStateOf("Tous") }
    var showDialog by remember { mutableStateOf(false) }
    var editingBook by remember { mutableStateOf<TheologicalBook?>(null) }
    val categories = listOf("Tous", "Pneumatologie", "Homilétique", "Théologie Systématique", "Histoire des Réveils")

    // Tab 1 (PDF / EPUB Reader) states
    val bundledEBooks = remember { getBundledStudyDocuments() }
    var activeEBook by remember { mutableStateOf(bundledEBooks[0]) }
    var currentChapterIndex by remember { mutableIntStateOf(0) }
    var readerFontSize by remember { mutableFloatStateOf(15f) }
    var readerTheme by remember { mutableStateOf("SEPIA") } // "LIGHT", "SEPIA", "DARK"
    var isReadingFullScreen by remember { mutableStateOf(false) }
    var customDocumentUri by remember { mutableStateOf<Uri?>(null) }
    var customDocumentName by remember { mutableStateOf<String?>(null) }

    // Tab 2 (Video & Audio Media) states
    val mediaResources = remember { mutableStateListOf(*getSeedTeachingMedia().toTypedArray()) }
    var selectedMediaTypeFilter by remember { mutableStateOf("TOUS") }
    var showAddMediaDialog by remember { mutableStateOf(false) }

    // Media playback states (in-app simulation / link launcher)
    var activePlayingMediaId by remember { mutableStateOf<String?>(null) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var audioProgress by remember { mutableFloatStateOf(0.35f) }

    // File picker for PDF / EPUB
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            customDocumentUri = uri
            val name = uri.lastPathSegment ?: "document_etude.pdf"
            customDocumentName = name
            Toast.makeText(context, "Fichier chargé : $name", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            if (selectedMainTab == 0) {
                FloatingActionButton(
                    onClick = {
                        editingBook = null
                        showDialog = true
                    },
                    modifier = Modifier.testTag("add_book_fab"),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter un livre")
                }
            } else if (selectedMainTab == 2) {
                FloatingActionButton(
                    onClick = { showAddMediaDialog = true },
                    modifier = Modifier.testTag("add_media_fab"),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter une ressource")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("theology_library_screen")
        ) {
            // Main Top Navigation Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedMainTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedMainTab == 0,
                    onClick = { selectedMainTab = 0 },
                    text = { Text("📚 Ouvrages", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedMainTab == 1,
                    onClick = { selectedMainTab = 1 },
                    text = { Text("📄 Lecteur EPUB/PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedMainTab == 2,
                    onClick = { selectedMainTab = 2 },
                    text = { Text("🎥 Vidéos & Audios", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // ========================================================
            // TAB 0: OUVRAGES & THÉOLOGIE (LISTE DE LIVRES)
            // ========================================================
            if (selectedMainTab == 0) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Column {
                            Text(
                                text = "Bibliothèque Théologique",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Ouvrages des pères de la foi, doctrines de l'Esprit & synthèses",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Categories LazyRow
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                val filteredBooks = remember(books, selectedCategory) {
                    if (selectedCategory == "Tous") books
                    else books.filter { it.category.contains(selectedCategory, ignoreCase = true) }
                }

                if (filteredBooks.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Aucun ouvrage dans cette catégorie",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ajoutez un livre ou résumé théologique avec le bouton +",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredBooks) { book ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("book_item_${book.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = book.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Auteur : ${book.author}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when (book.status) {
                                                "Lu" -> MaterialTheme.colorScheme.primaryContainer
                                                "En cours" -> MaterialTheme.colorScheme.secondaryContainer
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ) {
                                            Text(
                                                text = book.status,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = book.category,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    FormattedContentText(text = book.summary)

                                    if (book.keyQuote.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier.padding(8.dp)) {
                                                Icon(
                                                    imageVector = Icons.Default.FormatQuote,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.size(6.dp))
                                                Text(
                                                    text = "« ${book.keyQuote} »",
                                                    fontSize = 12.sp,
                                                    fontStyle = FontStyle.Italic,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row {
                                            repeat(book.rating) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color(0xFFF59E0B),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteBook(book) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ========================================================
            // TAB 1: LECTEUR INTÉGRÉ EPUB & PDF
            // ========================================================
            if (selectedMainTab == 1) {
                // Header with Document Selector & Local File Picker
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Lecteur Études & Théologie",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Livres au format EPUB & PDF avec pagination et réglage visuel",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    documentPickerLauncher.launch(
                                        arrayOf("application/pdf", "application/epub+zip", "text/plain")
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ouvrir PDF/EPUB", fontSize = 11.sp)
                            }
                        }

                        if (customDocumentName != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(customDocumentName ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                    }
                                    TextButton(
                                        onClick = {
                                            customDocumentUri?.let { uri ->
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                                        setDataAndType(uri, "application/pdf")
                                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Visualiseur PDF externe requis", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Lire dans l'app PDF ↗", fontSize = 11.sp, color = Color(0xFF16A34A))
                                    }
                                }
                            }
                        }
                    }
                }

                // Available Bundled Books selector
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bundledEBooks) { book ->
                        val isSelected = activeEBook.id == book.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                activeEBook = book
                                currentChapterIndex = 0
                            },
                            label = {
                                Text(
                                    text = "${if (book.format == "PDF") "📕" else "📗"} ${book.title}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                // Reader Configuration Toolbar (Font size, Theme, Fullscreen)
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Themes: Clair, Sépia, Nuit
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ThemeBadge("Clair", readerTheme == "LIGHT", Color(0xFFFFFFFF), Color(0xFF0F172A)) { readerTheme = "LIGHT" }
                            ThemeBadge("Sépia", readerTheme == "SEPIA", Color(0xFFFBF0D9), Color(0xFF5F4B32)) { readerTheme = "SEPIA" }
                            ThemeBadge("Nuit", readerTheme == "DARK", Color(0xFF1E293B), Color(0xFFF8FAFC)) { readerTheme = "DARK" }
                        }

                        // Font size changer
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (readerFontSize > 12f) readerFontSize -= 2f },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("A-", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("${readerFontSize.toInt()} pt", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            IconButton(
                                onClick = { if (readerFontSize < 24f) readerFontSize += 2f },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("A+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // E-Book Reading Content View
                val currentChapter = activeEBook.chapters.getOrNull(currentChapterIndex) ?: activeEBook.chapters[0]
                val readerBgColor = when (readerTheme) {
                    "DARK" -> Color(0xFF0F172A)
                    "SEPIA" -> Color(0xFFFBF0D9)
                    else -> Color(0xFFFFFFFF)
                }
                val readerTextColor = when (readerTheme) {
                    "DARK" -> Color(0xFFF1F5F9)
                    "SEPIA" -> Color(0xFF433422)
                    else -> Color(0xFF1E293B)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(readerBgColor)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = activeEBook.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${activeEBook.author} • Format ${activeEBook.format}",
                        fontSize = 10.sp,
                        color = readerTextColor.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentChapter.title,
                        fontSize = (readerFontSize + 4).sp,
                        fontWeight = FontWeight.Bold,
                        color = readerTextColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FormattedContentText(
                        text = currentChapter.content,
                        color = readerTextColor,
                        lineHeightSp = (readerFontSize * 1.55f).toInt()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Bottom Pagination bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentChapterIndex > 0) currentChapterIndex-- },
                            enabled = currentChapterIndex > 0,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Précédent", fontSize = 11.sp)
                        }

                        Text(
                            text = "Chapitre ${currentChapterIndex + 1} / ${activeEBook.chapters.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedButton(
                            onClick = { if (currentChapterIndex < activeEBook.chapters.size - 1) currentChapterIndex++ },
                            enabled = currentChapterIndex < activeEBook.chapters.size - 1,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Suivant", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // ========================================================
            // TAB 2: RESSOURCES ENSEIGNEMENT VIDÉO & AUDIO
            // ========================================================
            if (selectedMainTab == 2) {
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
                                text = "Médias d'Enseignement & Prédications",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Capsules vidéo, conférences théologiques et podcasts audio",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Filter by media type: Tous, Vidéos, Audios
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedMediaTypeFilter == "TOUS",
                        onClick = { selectedMediaTypeFilter = "TOUS" },
                        label = { Text("Tous les Médias (${mediaResources.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedMediaTypeFilter == "VIDEO",
                        onClick = { selectedMediaTypeFilter = "VIDEO" },
                        label = { Text("🎬 Vidéos Enseignements", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedMediaTypeFilter == "AUDIO",
                        onClick = { selectedMediaTypeFilter = "AUDIO" },
                        label = { Text("🎙️ Podcasts Audio", fontSize = 11.sp) }
                    )
                }

                val filteredMedia = remember(mediaResources, selectedMediaTypeFilter) {
                    if (selectedMediaTypeFilter == "TOUS") mediaResources
                    else mediaResources.filter { it.mediaType == selectedMediaTypeFilter }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMedia, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (item.mediaType == "VIDEO") Color(0xFFFEE2E2) else Color(0xFFE0E7FF),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (item.mediaType == "VIDEO") Icons.Default.Videocam else Icons.Default.Headphones,
                                                    contentDescription = null,
                                                    tint = if (item.mediaType == "VIDEO") Color(0xFFDC2626) else Color(0xFF4338CA),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = item.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "${item.teacher} • ${item.durationText}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = item.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                FormattedContentText(text = item.description, lineHeightSp = 17)

                                Spacer(modifier = Modifier.height(10.dp))

                                if (item.mediaType == "AUDIO") {
                                    // Audio Player Bar
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                val isThisPlaying = activePlayingMediaId == item.id && isAudioPlaying
                                                IconButton(
                                                    onClick = {
                                                        if (activePlayingMediaId == item.id) {
                                                            isAudioPlaying = !isAudioPlaying
                                                        } else {
                                                            activePlayingMediaId = item.id
                                                            isAudioPlaying = true
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                Slider(
                                                    value = if (activePlayingMediaId == item.id) audioProgress else 0.2f,
                                                    onValueChange = {
                                                        if (activePlayingMediaId == item.id) audioProgress = it
                                                    },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(horizontal = 8.dp)
                                                )

                                                Text(
                                                    text = item.durationText,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // Video Action Button
                                    Button(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.mediaUrl))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Ouverture : ${item.mediaUrl}", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Regarder la Vidéo d'Enseignement", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for adding a theological book
    if (showDialog) {
        var title by remember { mutableStateOf(editingBook?.title ?: "") }
        var author by remember { mutableStateOf(editingBook?.author ?: "") }
        var category by remember { mutableStateOf(editingBook?.category ?: "Pneumatologie") }
        var status by remember { mutableStateOf(editingBook?.status ?: "En cours") }
        var summary by remember { mutableStateOf(editingBook?.summary ?: "") }
        var keyQuote by remember { mutableStateOf(editingBook?.keyQuote ?: "") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Ajouter un Ouvrage Théologique") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre du livre *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Auteur / Théologien *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Discipline (Pneumatologie, etc.)") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Statut (Lu, En cours, À lire)") }, modifier = Modifier.fillMaxWidth()) }
                    item {
                        RichTextFormattingBar(text = summary, onTextChange = { summary = it })
                        OutlinedTextField(
                            value = summary,
                            onValueChange = { summary = it },
                            label = { Text("Résumé des thèses et enseignements") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                    item {
                        RichTextFormattingBar(text = keyQuote, onTextChange = { keyQuote = it })
                        OutlinedTextField(
                            value = keyQuote,
                            onValueChange = { keyQuote = it },
                            label = { Text("Citation mémorable") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSaveBook(
                                TheologicalBook(
                                    id = editingBook?.id ?: 0L,
                                    title = title,
                                    author = author.ifBlank { "Auteur chrétien" },
                                    category = category,
                                    status = status,
                                    summary = summary,
                                    keyQuote = keyQuote
                                )
                            )
                            showDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_book_button")
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Annuler") }
            }
        )
    }

    // Dialog for adding video/audio teaching resource
    if (showAddMediaDialog) {
        var mTitle by remember { mutableStateOf("") }
        var mTeacher by remember { mutableStateOf("") }
        var mType by remember { mutableStateOf("VIDEO") }
        var mUrl by remember { mutableStateOf("") }
        var mCategory by remember { mutableStateOf("Pneumatologie") }
        var mDuration by remember { mutableStateOf("35 min") }
        var mDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMediaDialog = false },
            title = { Text("Ajouter une Ressource Vidéo ou Audio") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { OutlinedTextField(value = mTitle, onValueChange = { mTitle = it }, label = { Text("Titre de l'enseignement *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = mTeacher, onValueChange = { mTeacher = it }, label = { Text("Enseignant / Prédicateur") }, modifier = Modifier.fillMaxWidth()) }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = mType == "VIDEO",
                                onClick = { mType = "VIDEO" },
                                label = { Text("🎬 Vidéo (YouTube/Lien)") }
                            )
                            FilterChip(
                                selected = mType == "AUDIO",
                                onClick = { mType = "AUDIO" },
                                label = { Text("🎙️ Audio (Podcast/MP3)") }
                            )
                        }
                    }
                    item { OutlinedTextField(value = mUrl, onValueChange = { mUrl = it }, label = { Text("Lien URL (YouTube, MP4, MP3...) *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = mCategory, onValueChange = { mCategory = it }, label = { Text("Catégorie (Pneumatologie, Prière...)") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = mDuration, onValueChange = { mDuration = it }, label = { Text("Durée estimée (ex: 40 min)") }, modifier = Modifier.fillMaxWidth()) }
                    item {
                        RichTextFormattingBar(text = mDesc, onTextChange = { mDesc = it })
                        OutlinedTextField(value = mDesc, onValueChange = { mDesc = it }, label = { Text("Description & Points clés") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (mTitle.isNotBlank()) {
                            mediaResources.add(
                                0,
                                TeachingMedia(
                                    id = "media_${System.currentTimeMillis()}",
                                    title = mTitle,
                                    teacher = mTeacher.ifBlank { "Pasteur enseignant" },
                                    mediaType = mType,
                                    mediaUrl = mUrl.ifBlank { "https://www.youtube.com" },
                                    category = mCategory,
                                    durationText = mDuration,
                                    description = mDesc
                                )
                            )
                            showAddMediaDialog = false
                        }
                    }
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMediaDialog = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
private fun ThemeBadge(label: String, selected: Boolean, bg: Color, fg: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = bg,
        modifier = Modifier.border(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = fg,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun getBundledStudyDocuments(): List<StudyEBook> {
    return listOf(
        StudyEBook(
            id = "ebook_torrey_saint_esprit",
            title = "La Personne & L'Œuvre du Saint-Esprit",
            author = "R.A. Torrey",
            format = "EPUB",
            category = "Pneumatologie",
            totalPages = 145,
            description = "Étude systématique classique sur la divinité, la communion intime et le baptême dans le Saint-Esprit.",
            chapters = listOf(
                StudyDocumentChapter(
                    title = "Chapitre 1 : La Personnalité du Saint-Esprit",
                    content = """
### Qui est le Saint-Esprit ?
Avant d'examiner l'œuvre du Saint-Esprit, nous devons comprendre **qui Il est**. Le Saint-Esprit n'est pas une simple force impersonnelle, une énergie mystique ou une influence divine. **Le Saint-Esprit est une Personne**.

• **Il possède une intelligence** : « Et celui qui sonde les cœurs connaît quelle est la pensée de l'Esprit » (Romains 8:27).
• **Il possède une volonté souveraine** : « Un seul et même Esprit opère toutes ces choses, les distribuant à chacun en particulier comme il veut » (1 Corinthiens 12:11).
• **Il possède des sentiments et de l'amour** : « Je vous exhorte par l'amour de l'Esprit » (Romains 15:30).

> « N'attristez pas le Saint-Esprit de Dieu, par lequel vous avez été scellés pour le jour de la rédemption. » (Éphésiens 4:30)

✝ Traitez le Saint-Esprit avec révérence et intégrité. Parlez-Lui chaque jour comme au plus fidèle des compagnons.
                    """.trimIndent()
                ),
                StudyDocumentChapter(
                    title = "Chapitre 2 : Le Baptême du Saint-Esprit",
                    content = """
### La Promesse du Père
Le baptême dans le Saint-Esprit est une opération distincte de la nouvelle naissance, destinée à revêtir le disciple de **puissance surnaturelle (Dunamis)** pour le témoignage.

• **Jésus l'a promis** : « Vous recevrez une puissance, le Saint-Esprit survenant sur vous, et vous serez mes témoins... » (Actes 1:8).
• **Une réalité pour chaque croyant** : Pierre proclame à la Pentecôte : « La promesse est pour vous, pour vos enfants, et pour tous ceux qui sont au loin » (Actes 2:39).

### Comment le recevoir ?
1. Se repentir et croire en Jésus-Christ de tout son cœur.
2. Demander par la foi au Père céleste (Luc 11:13).
3. S'attendre paisiblement et s'abandonner à l'action de Sa grâce.
                    """.trimIndent()
                )
            )
        ),
        StudyEBook(
            id = "ebook_wigglesworth_dons",
            title = "Vivre par la Foi & les Dons Spirituels",
            author = "Smith Wigglesworth",
            format = "PDF",
            category = "Foi & Miracles",
            totalPages = 120,
            description = "Leçons pratiques d'obéissance, de foi inébranlable et d'opération des dons de l'Esprit.",
            chapters = listOf(
                StudyDocumentChapter(
                    title = "Section 1 : La Foi Inébranlable dans la Parole",
                    content = """
### Seul Croire !
Dieu peut faire plus en un instant que des années de réflexion humaine. La foi n'est pas une émotion ; la foi est une **décision ferme d'agir sur la Parole de Dieu**.

> « Jésus lui dit : Ne crains point, crois seulement. » (Marc 5:36)

• **La source de la foi** : La foi vient de ce qu'on entend, et ce qu'on entend vient de la Parole de Christ (Romains 10:17).
• **La foi en action** : Une foi sans les œuvres est morte. Lorsque le Saint-Esprit met une conviction dans votre cœur, levez-vous et obéissez sans tarder.
                    """.trimIndent()
                ),
                StudyDocumentChapter(
                    title = "Section 2 : Les Dons de Guérison & Miracles",
                    content = """
### Jésus Guérit Encore Aujourd'hui
Le Seigneur Jésus-Christ est le même hier, aujourd'hui et éternellement (Hébreux 13:8). Lorsque nous prions pour les malades avec l'onction du Saint-Esprit, le ciel s'ouvre.

• **L'autorité du Nom de Jésus** : C'est Son Nom glorieux qui accomplit la délivrance.
• **La compassion de Christ** : Le moteur de tout miracle authentique est l'amour divin (Agapē).
                    """.trimIndent()
                )
            )
        )
    )
}

private fun getSeedTeachingMedia(): List<TeachingMedia> {
    return listOf(
        TeachingMedia(
            id = "vid_saint_esprit_intime",
            title = "L'Intimité Quotidienne avec le Paraclet",
            teacher = "Pasteur David Wilkerson & Watchman Nee",
            mediaType = "VIDEO",
            mediaUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            category = "Pneumatologie",
            durationText = "45 min",
            description = "Enseignement approfondi sur la Koinonia (communion) de l'Esprit. Comment écouter Son murmure doux au milieu de l'agitation du monde moderne."
        ),
        TeachingMedia(
            id = "vid_dons_spirituels",
            title = "L'Opération des 9 Dons de l'Esprit",
            teacher = "Dr. Derek Prince",
            mediaType = "VIDEO",
            mediaUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            category = "Dons & Onction",
            durationText = "58 min",
            description = "Étude biblique verset par verset de 1 Corinthiens 12. Les dons de révélation, de puissance et d'inspiration pour fortifier le corps de Christ."
        ),
        TeachingMedia(
            id = "aud_marche_esprit",
            title = "Podcast : Marcher par l'Esprit au Quotidien",
            teacher = "École du Saint Esprit",
            mediaType = "AUDIO",
            mediaUrl = "https://example.com/audio/marche_esprit.mp3",
            category = "Sanctification",
            durationText = "28 min",
            description = "Méditation audio guidée sur Galates 5. Vaincre les convoitises de la chair par la plénitude intérieure et la paix du Seigneur."
        ),
        TeachingMedia(
            id = "aud_priere_langues",
            title = "Podcast : La Puissance de la Prière en Langues",
            teacher = "Ministère de Réveil",
            mediaType = "AUDIO",
            mediaUrl = "https://example.com/audio/priere_langues.mp3",
            category = "Intercession",
            durationText = "34 min",
            description = "Pourquoi et comment le parler en langues édifie l'homme intérieur, renouvelle l'esprit et libère l'intercession parfaite de Dieu."
        )
    )
}
