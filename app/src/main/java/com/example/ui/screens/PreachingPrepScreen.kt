package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PreachingPrep
import com.example.ui.components.FormattedContentText
import com.example.ui.components.RichTextFormattingBar

@Composable
fun PreachingPrepScreen(
    preachings: List<PreachingPrep>,
    onSavePreaching: (PreachingPrep) -> Unit,
    onDeletePreaching: (PreachingPrep) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingPrep by remember { mutableStateOf<PreachingPrep?>(null) }
    var pulpitPrep by remember { mutableStateOf<PreachingPrep?>(null) } // pulpit presentation mode

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingPrep = null
                    showDialog = true
                },
                modifier = Modifier.testTag("add_preaching_fab"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle prédication")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("preaching_prep_screen")
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Column {
                        Text(
                            text = "Atelier d'Homilétique Biblique",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Structure en 3 points, illustrations et appel inspiré par le Saint-Esprit",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (preachings.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Aucune prédication en préparation",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Concevez votre plan de prédication christocentrique avec le bouton +",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(preachings) { prep ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("preaching_item_${prep.id}")
                                .clickable {
                                    editingPrep = prep
                                    showDialog = true
                                },
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
                                        text = prep.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = prep.mainPassage,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Thème : ${prep.theme} • Pour : ${prep.targetAudience}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Intro : ${prep.introduction}",
                                    fontSize = 13.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• ${prep.pointOne}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "• ${prep.pointTwo}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (prep.pointThree.isNotBlank()) {
                                    Text(
                                        text = "• ${prep.pointThree}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.size(4.dp))
                                        Text(text = "~${prep.durationMinutes} min", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    }

                                    Row {
                                        Button(
                                            onClick = { pulpitPrep = prep },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.size(4.dp))
                                            Text("Mode Chaire", fontSize = 12.sp)
                                        }

                                        IconButton(onClick = { onDeletePreaching(prep) }) {
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

    // Pulpit Mode Full Screen Reading Dialog
    if (pulpitPrep != null) {
        val p = pulpitPrep!!
        AlertDialog(
            onDismissRequest = { pulpitPrep = null },
            title = {
                Column {
                    Text(text = p.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = "${p.mainPassage} • ${p.durationMinutes} min", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "1. Introduction / Accroche", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                Text(text = p.introduction, fontSize = 14.sp)
                            }
                        }
                    }
                    item {
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "2. Corps du Message (3 Piliers)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = p.pointOne, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = p.pointTwo, fontSize = 14.sp)
                                if (p.pointThree.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = p.pointThree, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                    item {
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "3. Illustrations & Témoignages", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = p.illustrations.ifBlank { "Témoignage biblique ou personnel." }, fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "4. Application Pratique", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = p.application.ifBlank { "Comment le chrétien applique ceci dès demain." }, fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "5. Appel à l'Autel & Prière d'Onction", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                Text(text = p.altarCall.ifBlank { "Invitation à s'avancer et prière de consécration." }, fontSize = 14.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { pulpitPrep = null }) {
                    Text("Quitter le Mode Chaire")
                }
            }
        )
    }

    // Add / Edit Preaching Dialog
    if (showDialog) {
        var title by remember { mutableStateOf(editingPrep?.title ?: "") }
        var mainPassage by remember { mutableStateOf(editingPrep?.mainPassage ?: "") }
        var theme by remember { mutableStateOf(editingPrep?.theme ?: "") }
        var targetAudience by remember { mutableStateOf(editingPrep?.targetAudience ?: "Assemblée générale") }
        var introduction by remember { mutableStateOf(editingPrep?.introduction ?: "") }
        var pointOne by remember { mutableStateOf(editingPrep?.pointOne ?: "") }
        var pointTwo by remember { mutableStateOf(editingPrep?.pointTwo ?: "") }
        var pointThree by remember { mutableStateOf(editingPrep?.pointThree ?: "") }
        var illustrations by remember { mutableStateOf(editingPrep?.illustrations ?: "") }
        var application by remember { mutableStateOf(editingPrep?.application ?: "") }
        var altarCall by remember { mutableStateOf(editingPrep?.altarCall ?: "") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingPrep == null) "Nouvelle Prédication" else "Modifier le Plan") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre de la prédication *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = mainPassage, onValueChange = { mainPassage = it }, label = { Text("Passage biblique d'ancrage *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = theme, onValueChange = { theme = it }, label = { Text("Thème central") }, modifier = Modifier.fillMaxWidth()) }
                    item {
                        Column {
                            RichTextFormattingBar(text = introduction, onTextChange = { introduction = it })
                            OutlinedTextField(value = introduction, onValueChange = { introduction = it }, label = { Text("Accroche & Introduction") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                        }
                    }
                    item { OutlinedTextField(value = pointOne, onValueChange = { pointOne = it }, label = { Text("Point 1 (Vérité biblique)") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = pointTwo, onValueChange = { pointTwo = it }, label = { Text("Point 2 (Développement théologique)") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = pointThree, onValueChange = { pointThree = it }, label = { Text("Point 3 (Action prophétique)") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = illustrations, onValueChange = { illustrations = it }, label = { Text("Illustrations / Témoignages") }, modifier = Modifier.fillMaxWidth()) }
                    item {
                        Column {
                            RichTextFormattingBar(text = application, onTextChange = { application = it })
                            OutlinedTextField(value = application, onValueChange = { application = it }, label = { Text("Application pour la congrégation") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                        }
                    }
                    item { OutlinedTextField(value = altarCall, onValueChange = { altarCall = it }, label = { Text("Appel à l'autel & Prière finale") }, modifier = Modifier.fillMaxWidth()) }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSavePreaching(
                                PreachingPrep(
                                    id = editingPrep?.id ?: 0L,
                                    title = title,
                                    mainPassage = mainPassage.ifBlank { "Actes 2" },
                                    theme = theme.ifBlank { "Vie Spirituelle" },
                                    targetAudience = targetAudience,
                                    introduction = introduction,
                                    pointOne = pointOne,
                                    pointTwo = pointTwo,
                                    pointThree = pointThree,
                                    illustrations = illustrations,
                                    application = application,
                                    altarCall = altarCall
                                )
                            )
                            showDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_preaching_button")
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Annuler") }
            }
        )
    }
}
