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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.SermonNote
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

val DEFAULT_SERMON_CATEGORIES = listOf(
    "Culte Dominical",
    "Étude Biblique",
    "Conférence",
    "Réveil Spirituel",
    "Pneumatologie",
    "Jeûne & Prière",
    "Évangélisation",
    "Autre"
)

@Composable
fun SermonNotesScreen(
    notes: List<SermonNote>,
    initialPassage: String = "",
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
    onSaveNote: (SermonNote) -> Unit,
    onDeleteNote: (SermonNote) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("TOUTES") }
    var filterWithAudioOnly by remember { mutableStateOf(false) }

    var editingNote by remember { mutableStateOf<SermonNote?>(null) }
    var showDialog by remember { mutableStateOf(initialPassage.isNotEmpty()) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    // Audio permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartRecording("sermon")
        } else {
            showPermissionRationale = true
        }
    }

    val requestMicAndRecord = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            onStartRecording("sermon")
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Dynamic categories from notes + defaults
    val availableCategories = remember(notes) {
        val fromNotes = notes.map { it.category }.filter { it.isNotBlank() }
        (listOf("TOUTES") + (DEFAULT_SERMON_CATEGORIES + fromNotes).distinct())
    }

    val filteredNotes = remember(notes, searchQuery, selectedCategoryFilter, filterWithAudioOnly) {
        notes.filter { note ->
            val matchesSearch = if (searchQuery.isBlank()) true else {
                note.title.contains(searchQuery, ignoreCase = true) ||
                        note.preacher.contains(searchQuery, ignoreCase = true) ||
                        note.scriptureRef.contains(searchQuery, ignoreCase = true) ||
                        note.tags.contains(searchQuery, ignoreCase = true) ||
                        note.category.contains(searchQuery, ignoreCase = true) ||
                        note.content.contains(searchQuery, ignoreCase = true)
            }
            val matchesCategory = if (selectedCategoryFilter == "TOUTES") true else {
                note.category.equals(selectedCategoryFilter, ignoreCase = true)
            }
            val matchesAudio = if (!filterWithAudioOnly) true else {
                !note.audioFilePath.isNullOrBlank()
            }
            matchesSearch && matchesCategory && matchesAudio
        }
    }

    val totalAudioCount = remember(notes) { notes.count { !it.audioFilePath.isNullOrBlank() } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingNote = null
                    showDialog = true
                },
                modifier = Modifier.testTag("add_sermon_note_fab"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle note de sermon")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("sermon_notes_screen")
        ) {
            // Live active recording bar (floating or top banner)
            AnimatedVisibility(visible = recordingState.isRecording) {
                AudioRecordingWidget(
                    recordingState = recordingState,
                    onPause = onPauseRecording,
                    onResume = onResumeRecording,
                    onStop = {
                        val (filePath, durationSec) = onStopRecording()
                        if (filePath != null) {
                            // If editing a note, attach to it or open dialog to save
                            if (editingNote != null) {
                                editingNote = editingNote?.copy(
                                    audioFilePath = filePath,
                                    audioDurationSeconds = durationSec
                                )
                            } else {
                                // Create new draft note with audio
                                editingNote = SermonNote(
                                    title = "Sermon du " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                                    preacher = "",
                                    churchOrEvent = "",
                                    scriptureRef = "",
                                    content = "",
                                    keyTakeaways = "",
                                    tags = "Audio, Enregistrement",
                                    audioFilePath = filePath,
                                    audioDurationSeconds = durationSec
                                )
                                showDialog = true
                            }
                        }
                    },
                    onCancel = onCancelRecording,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Rechercher dans les notes de sermon...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("search_sermon_notes_field"),
                singleLine = true
            )

            // Category & Audio Filters Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio only filter toggle
                item {
                    FilterChip(
                        selected = filterWithAudioOnly,
                        onClick = { filterWithAudioOnly = !filterWithAudioOnly },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Audio ($totalAudioCount)", fontSize = 12.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFDC2626),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                items(availableCategories) { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            // Quick live recording trigger banner if not currently recording
            if (!recordingState.isRecording) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { requestMicAndRecord() },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Enregistrer le culte en direct",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Microphone actif • Enregistrement lié à la note",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { requestMicAndRecord() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Démarrer", fontSize = 11.sp)
                        }
                    }
                }
            }

            if (filteredNotes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Aucune note trouvée",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enregistrez le message en direct ou rédigez vos notes en cliquant sur +",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sermon_note_card_${note.id}")
                                .clickable {
                                    editingNote = note
                                    showDialog = true
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
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
                                        text = note.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = note.scriptureRef.ifBlank { "Passage" },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Preacher, Category badge, Date
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(
                                                text = note.category.ifBlank { "Culte" },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = "Prédicateur : ${note.preacher}",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Text(
                                        text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(note.dateMillis)),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (note.churchOrEvent.isNotBlank()) {
                                    Text(
                                        text = "Lieu : ${note.churchOrEvent}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                // Audio player embedded directly if note has audio
                                if (!note.audioFilePath.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    AudioPlayerCard(
                                        filePath = note.audioFilePath,
                                        title = "Audio du sermon (${AudioPlayerManager.formatTime(note.audioDurationSeconds)})",
                                        durationSeconds = note.audioDurationSeconds,
                                        playerState = playerState,
                                        onTogglePlay = { onTogglePlayAudio(note.audioFilePath) },
                                        onSeek = onSeekAudio,
                                        onSetSpeed = onSetAudioSpeed,
                                        onDelete = {
                                            // Remove audio link from note
                                            onSaveNote(note.copy(audioFilePath = null, audioDurationSeconds = 0))
                                            Toast.makeText(context, "Audio supprimé de la note", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }

                                if (note.content.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FormattedContentText(text = note.content)
                                }

                                if (note.keyTakeaways.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = "Application :",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            FormattedContentText(text = note.keyTakeaways)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#${note.tags.replace(",", " #")}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Row {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val audioInfo = if (!note.audioFilePath.isNullOrBlank()) "\n[Enregistrement audio inclus: ${AudioPlayerManager.formatTime(note.audioDurationSeconds)}]" else ""
                                                val shareText = "Sermon: ${note.title}\nPrédicateur: ${note.preacher}\nPassage: ${note.scriptureRef}\nCatégorie: ${note.category}$audioInfo\n\n${note.content}\n\nApplication:\n${note.keyTakeaways}"
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Sermon", shareText))
                                                Toast.makeText(context, "Note copiée dans le presse-papiers !", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Partager", modifier = Modifier.size(18.dp))
                                        }

                                        IconButton(
                                            onClick = { onDeleteNote(note) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
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

    // Permission explanation dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Autorisation du Microphone") },
            text = {
                Text("Pour enregistrer l'audio des prédications directement dans vos notes de sermon, l'accès au microphone est requis. Veuillez l'activer dans les paramètres de l'appareil.")
            },
            confirmButton = {
                Button(onClick = { showPermissionRationale = false }) {
                    Text("Compris")
                }
            }
        )
    }

    // Add / Edit Dialog
    if (showDialog) {
        var title by remember { mutableStateOf(editingNote?.title ?: "") }
        var preacher by remember { mutableStateOf(editingNote?.preacher ?: "") }
        var category by remember { mutableStateOf(editingNote?.category ?: "Culte Dominical") }
        var churchOrEvent by remember { mutableStateOf(editingNote?.churchOrEvent ?: "") }
        var scriptureRef by remember { mutableStateOf(editingNote?.scriptureRef ?: initialPassage) }
        var content by remember { mutableStateOf(editingNote?.content ?: "") }
        var keyTakeaways by remember { mutableStateOf(editingNote?.keyTakeaways ?: "") }
        var tags by remember { mutableStateOf(editingNote?.tags ?: "SaintEsprit, Foi") }
        var currentAudioPath by remember { mutableStateOf(editingNote?.audioFilePath) }
        var currentAudioDuration by remember { mutableStateOf(editingNote?.audioDurationSeconds ?: 0) }

        var isRecordingInsideDialog by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                if (recordingState.isRecording) onCancelRecording()
                showDialog = false
            },
            title = {
                Text(
                    text = if (editingNote == null) "Nouvelle Note de Sermon" else "Modifier la Note",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Titre du message *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Audio Recording / Player inside Dialog
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🎙️ Enregistrement Audio du Sermon",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                if (recordingState.isRecording) {
                                    AudioRecordingWidget(
                                        recordingState = recordingState,
                                        onPause = onPauseRecording,
                                        onResume = onResumeRecording,
                                        onStop = {
                                            val (filePath, durationSec) = onStopRecording()
                                            if (filePath != null) {
                                                currentAudioPath = filePath
                                                currentAudioDuration = durationSec
                                                isRecordingInsideDialog = false
                                            }
                                        },
                                        onCancel = {
                                            onCancelRecording()
                                            isRecordingInsideDialog = false
                                        }
                                    )
                                } else if (!currentAudioPath.isNullOrBlank()) {
                                    AudioPlayerCard(
                                        filePath = currentAudioPath!!,
                                        title = "Audio attaché (${AudioPlayerManager.formatTime(currentAudioDuration)})",
                                        durationSeconds = currentAudioDuration,
                                        playerState = playerState,
                                        onTogglePlay = { onTogglePlayAudio(currentAudioPath!!) },
                                        onSeek = onSeekAudio,
                                        onSetSpeed = onSetAudioSpeed,
                                        onDelete = {
                                            currentAudioPath = null
                                            currentAudioDuration = 0
                                        }
                                    )
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            isRecordingInsideDialog = true
                                            requestMicAndRecord()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                                    ) {
                                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Démarrer l'enregistrement audio", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = preacher,
                            onValueChange = { preacher = it },
                            label = { Text("Prédicateur *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Category selection and customization
                    item {
                        Column {
                            Text(
                                text = "Catégorie du sermon (Personnalisable) :",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DEFAULT_SERMON_CATEGORIES.forEach { cat ->
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
                            value = scriptureRef,
                            onValueChange = { scriptureRef = it },
                            label = { Text("Passage biblique de base") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = churchOrEvent,
                            onValueChange = { churchOrEvent = it },
                            label = { Text("Église / Événement / Conférence") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Column {
                            RichTextFormattingBar(text = content, onTextChange = { content = it })
                            OutlinedTextField(
                                value = content,
                                onValueChange = { content = it },
                                label = { Text("Notes détaillées du message *") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4
                            )
                        }
                    }

                    item {
                        Column {
                            RichTextFormattingBar(text = keyTakeaways, onTextChange = { keyTakeaways = it })
                            OutlinedTextField(
                                value = keyTakeaways,
                                onValueChange = { keyTakeaways = it },
                                label = { Text("Ce que le Saint-Esprit m'a dit / Application") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            label = { Text("Mots-clés (ex: Prière, Onction, Amour)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            // If still recording, stop and attach
                            if (recordingState.isRecording) {
                                val (savedPath, durationSec) = onStopRecording()
                                if (savedPath != null) {
                                    currentAudioPath = savedPath
                                    currentAudioDuration = durationSec
                                }
                            }

                            val newNote = SermonNote(
                                id = editingNote?.id ?: 0L,
                                title = title,
                                preacher = preacher.ifBlank { "Pasteur" },
                                category = category.ifBlank { "Culte Dominical" },
                                churchOrEvent = churchOrEvent,
                                scriptureRef = scriptureRef.ifBlank { "Jean 14" },
                                content = content,
                                keyTakeaways = keyTakeaways,
                                tags = tags,
                                audioFilePath = currentAudioPath,
                                audioDurationSeconds = currentAudioDuration,
                                dateMillis = editingNote?.dateMillis ?: System.currentTimeMillis()
                            )
                            onSaveNote(newNote)
                            showDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_sermon_note_button")
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (recordingState.isRecording) onCancelRecording()
                    showDialog = false
                }) {
                    Text("Annuler")
                }
            }
        )
    }
}
