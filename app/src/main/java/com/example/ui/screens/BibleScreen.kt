package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.bible.BibleRepositoryData
import com.example.data.bible.BibleVerse
import com.example.data.bible.BibleVersion
import com.example.data.model.TestimonyEntity
import com.example.data.model.VerseBookmark

val highlightColors = listOf(
    "GOLD" to Color(0xFFF59E0B),
    "BLUE" to Color(0xFF3B82F6),
    "GREEN" to Color(0xFF10B981),
    "ROSE" to Color(0xFFEC4899),
    "PURPLE" to Color(0xFF8B5CF6)
)

val verseCategories = listOf(
    "Saint-Esprit",
    "Foi & Victoire",
    "Guérison Divine",
    "Paix & Réconfort",
    "Combat Spirituel",
    "Prière & Intercession",
    "Ministère"
)

@Composable
fun BibleScreen(
    selectedBook: String,
    selectedChapter: Int,
    selectedVersion: BibleVersion,
    parallelMode: Boolean,
    fontSizeSp: Int,
    useSerifFont: Boolean,
    verses: List<BibleVerse>,
    bookmarks: List<VerseBookmark>,
    onSelectBook: (String) -> Unit,
    onSelectChapter: (Int) -> Unit,
    onSetVersion: (BibleVersion) -> Unit,
    onToggleParallel: () -> Unit,
    onChangeFontSize: (Int) -> Unit,
    onToggleFontFamily: () -> Unit,
    onSaveVerseHighlight: (BibleVerse, String, String, String) -> Unit,
    onRemoveBookmarkByCoordinates: (String, Int, Int) -> Unit,
    onCreateSermonFromVerse: (BibleVerse) -> Unit,
    onSaveTestimony: (TestimonyEntity) -> Unit
) {
    val context = LocalContext.current
    var showBookDropdown by remember { mutableStateOf(false) }
    var showFormattingBar by remember { mutableStateOf(false) }
    var showBookmarksDialog by remember { mutableStateOf(false) }

    // Interactivity dialog for a selected verse
    var activeVerseForAction by remember { mutableStateOf<BibleVerse?>(null) }
    var testimonyDialogVerse by remember { mutableStateOf<BibleVerse?>(null) }

    val chaptersForCurrentBook = remember(selectedBook) {
        val chs = BibleRepositoryData.verses
            .filter { it.book == selectedBook }
            .map { it.chapter }
            .distinct()
            .sorted()
        if (chs.isNotEmpty()) chs else listOf(1, 2, 3)
    }

    val currentFontFamily = if (useSerifFont) FontFamily.Serif else FontFamily.SansSerif

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bible_screen")
    ) {
        // 1. Header Toolbar
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Book selector dropdown
                    Box {
                        OutlinedButton(
                            onClick = { showBookDropdown = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("book_selector_button")
                        ) {
                            Text(
                                text = "$selectedBook $selectedChapter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        DropdownMenu(
                            expanded = showBookDropdown,
                            onDismissRequest = { showBookDropdown = false }
                        ) {
                            BibleRepositoryData.booksList.forEach { book ->
                                DropdownMenuItem(
                                    text = { Text(book) },
                                    onClick = {
                                        onSelectBook(book)
                                        showBookDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Format, Parallel & Bookmarks actions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showFormattingBar = !showFormattingBar },
                            modifier = Modifier.testTag("format_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Formater le texte",
                                tint = if (showFormattingBar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        FilterChip(
                            selected = parallelMode,
                            onClick = onToggleParallel,
                            label = { Text("Parallèle (3)", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CompareArrows,
                                    contentDescription = "Mode Parallèle",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("parallel_mode_chip")
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { showBookmarksDialog = true },
                            modifier = Modifier.testTag("bookmarks_list_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Mes Versets Marqués",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Formatting expand bar
                AnimatedVisibility(visible = showFormattingBar) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Taille du texte :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(14 to "Petit", 16 to "Standard", 18 to "Grand", 21 to "Très grand").forEach { (sz, lbl) ->
                                        FilterChip(
                                            selected = fontSizeSp == sz,
                                            onClick = { onChangeFontSize(sz) },
                                            label = { Text(lbl, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Police d'écriture :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                OutlinedButton(
                                    onClick = onToggleFontFamily,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(if (useSerifFont) "📜 Sérif Biblique" else "📱 Sans-Sérif Moderne", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chapter selector row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(chaptersForCurrentBook) { chapter ->
                        FilterChip(
                            selected = selectedChapter == chapter,
                            onClick = { onSelectChapter(chapter) },
                            label = { Text("Ch. $chapter") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // Bible version pills
                if (!parallelMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BibleVersion.values().forEach { version ->
                            FilterChip(
                                selected = selectedVersion == version,
                                onClick = { onSetVersion(version) },
                                label = { Text(version.displayName, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. Verses List with Formatting, Highlighting, and Categorization
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(verses) { verse ->
                val bookmark = bookmarks.find {
                    it.book == verse.book && it.chapter == verse.chapter && it.verse == verse.verse
                }
                val isHighlighted = bookmark != null && bookmark.highlightColor != "NONE"
                val highlightColorVal = highlightColors.find { it.first == bookmark?.highlightColor }?.second

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verse_item_${verse.chapter}_${verse.verse}")
                        .clickable { activeVerseForAction = verse },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = highlightColorVal?.copy(alpha = 0.18f) ?: MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Left colored stripe if highlighted
                        if (highlightColorVal != null) {
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(110.dp)
                                    .background(highlightColorVal)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(highlightColorVal ?: MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${verse.verse}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${verse.book} ${verse.chapter}:${verse.verse}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (bookmark?.categoryTag?.isNotBlank() == true) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "#${bookmark.categoryTag}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (parallelMode) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TranslationBlock("🇲🇬 Baiboly Malagasy", verse.textMalagasy, Color(0xFF1E3A8A), fontSizeSp, currentFontFamily)
                                    TranslationBlock("🇫🇷 Bible du Semeur (BDS)", verse.textSemeur, Color(0xFF0D9488), fontSizeSp, currentFontFamily)
                                    TranslationBlock("🇬🇧 Amplified Bible (AMP)", verse.textAmplified, Color(0xFFB45309), fontSizeSp, currentFontFamily)
                                }
                            } else {
                                Text(
                                    text = verse.getText(selectedVersion),
                                    fontSize = fontSizeSp.sp,
                                    lineHeight = (fontSizeSp + 8).sp,
                                    fontFamily = currentFontFamily,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (bookmark?.note?.isNotBlank() == true) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "Note : ${bookmark.note}",
                                        fontSize = 12.sp,
                                        fontStyle = FontStyle.Italic,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick Action Toolbar on Verse
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val formatted = "« ${verse.getText(selectedVersion)} »\n— ${verse.book} ${verse.chapter}:${verse.verse} (${selectedVersion.displayName})"
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Verset", formatted))
                                        Toast.makeText(context, "Verset formaté et copié !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier avec référence", modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { activeVerseForAction = verse },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ColorLens, contentDescription = "Surligner & Catégoriser", tint = highlightColorVal ?: MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { testimonyDialogVerse = verse },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Celebration, contentDescription = "Lier un témoignage", tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { onCreateSermonFromVerse(verse) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.EditNote, contentDescription = "Note de sermon", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Action Sheet for Verse: Surlignage, Catégorisation, Notes
    if (activeVerseForAction != null) {
        val verse = activeVerseForAction!!
        val existingBookmark = bookmarks.find {
            it.book == verse.book && it.chapter == verse.chapter && it.verse == verse.verse
        }

        var selectedColorName by remember { mutableStateOf(existingBookmark?.highlightColor ?: "GOLD") }
        var selectedCategory by remember { mutableStateOf(existingBookmark?.categoryTag ?: "Saint-Esprit") }
        var customNote by remember { mutableStateOf(existingBookmark?.note ?: "") }

        AlertDialog(
            onDismissRequest = { activeVerseForAction = null },
            title = {
                Text(
                    text = "${verse.book} ${verse.chapter}:${verse.verse}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Text("1. Choisir une couleur de surlignage :", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            highlightColors.forEach { (colorName, colorVal) ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(colorVal)
                                        .border(
                                            width = if (selectedColorName == colorName) 3.dp else 1.dp,
                                            color = if (selectedColorName == colorName) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColorName = colorName }
                                )
                            }
                            // None option
                            TextButton(onClick = { selectedColorName = "NONE" }) {
                                Text("Aucun", fontSize = 12.sp)
                            }
                        }
                    }

                    item {
                        Text("2. Catégoriser le verset :", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(verseCategories) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                                )
                            }
                        }
                    }

                    item {
                        Text("3. Note personnelle sur la révélation :", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = customNote,
                            onValueChange = { customNote = it },
                            placeholder = { Text("Ce que l'Esprit me révèle...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedColorName == "NONE" && customNote.isBlank() && existingBookmark != null) {
                            onRemoveBookmarkByCoordinates(verse.book, verse.chapter, verse.verse)
                        } else {
                            onSaveVerseHighlight(verse, selectedColorName, selectedCategory, customNote)
                        }
                        activeVerseForAction = null
                        Toast.makeText(context, "Verset surligné et catégorisé !", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Appliquer")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeVerseForAction = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Testimony linked to Verse Dialog
    if (testimonyDialogVerse != null) {
        val v = testimonyDialogVerse!!
        var testimonyTitle by remember { mutableStateOf("Témoignage sur ${v.book} ${v.chapter}:${v.verse}") }
        var testimonyStory by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { testimonyDialogVerse = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Témoignage lié à ce verset")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Passage : ${v.book} ${v.chapter}:${v.verse}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = testimonyTitle,
                        onValueChange = { testimonyTitle = it },
                        label = { Text("Titre du témoignage *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = testimonyStory,
                        onValueChange = { testimonyStory = it },
                        label = { Text("Comment Dieu a-t-il agi à travers ce verset ? *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (testimonyStory.isNotBlank()) {
                            onSaveTestimony(
                                TestimonyEntity(
                                    title = testimonyTitle,
                                    sourceType = "VERSET",
                                    sourceReference = "${v.book} ${v.chapter}:${v.verse}",
                                    story = testimonyStory
                                )
                            )
                            testimonyDialogVerse = null
                            Toast.makeText(context, "Témoignage lié au verset enregistré !", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Enregistrer le Témoignage")
                }
            },
            dismissButton = {
                TextButton(onClick = { testimonyDialogVerse = null }) { Text("Annuler") }
            }
        )
    }

    // Bookmarks & Highlights Category Filter Dialog
    if (showBookmarksDialog) {
        var selectedFilterCategory by remember { mutableStateOf("Tous") }
        val filteredBookmarks = remember(bookmarks, selectedFilterCategory) {
            if (selectedFilterCategory == "Tous") bookmarks
            else bookmarks.filter { it.categoryTag == selectedFilterCategory }
        }

        AlertDialog(
            onDismissRequest = { showBookmarksDialog = false },
            title = { Text("Versets Marqués & Surlignés (${bookmarks.size})", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = selectedFilterCategory == "Tous",
                                onClick = { selectedFilterCategory = "Tous" },
                                label = { Text("Tous") }
                            )
                        }
                        items(verseCategories) { cat ->
                            FilterChip(
                                selected = selectedFilterCategory == cat,
                                onClick = { selectedFilterCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredBookmarks.isEmpty()) {
                        Text("Aucun verset dans cette catégorie.", fontSize = 13.sp)
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredBookmarks) { b ->
                                val colorVal = highlightColors.find { it.first == b.highlightColor }?.second
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorVal?.copy(alpha = 0.2f) ?: MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${b.book} ${b.chapter}:${b.verse} (${b.version})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            if (b.categoryTag.isNotBlank()) {
                                                Text(text = "#${b.categoryTag}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(text = b.text, fontSize = 13.sp, maxLines = 3)
                                        if (b.note.isNotBlank()) {
                                            Text(text = "Note : ${b.note}", fontSize = 11.sp, fontStyle = FontStyle.Italic)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBookmarksDialog = false }) { Text("Fermer") }
            }
        )
    }
}

@Composable
fun TranslationBlock(
    label: String,
    text: String,
    accentColor: Color,
    fontSizeSp: Int,
    fontFamily: FontFamily
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = accentColor.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp + 6).sp,
                fontFamily = fontFamily,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
