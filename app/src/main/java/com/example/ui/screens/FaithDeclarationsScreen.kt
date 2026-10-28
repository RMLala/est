package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RecordVoiceOver
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FaithDeclaration
import com.example.data.model.TestimonyEntity

@Composable
fun FaithDeclarationsScreen(
    declarations: List<FaithDeclaration>,
    onIncrementRecitation: (FaithDeclaration) -> Unit,
    onToggleFavorite: (FaithDeclaration) -> Unit,
    onSaveDeclaration: (FaithDeclaration) -> Unit,
    onSaveTestimony: (TestimonyEntity) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTheme by remember { mutableStateOf("Tous") }
    var showAddDialog by remember { mutableStateOf(false) }
    var testimonyDialogDecl by remember { mutableStateOf<FaithDeclaration?>(null) }

    val themes = listOf("Tous", "Identité en Christ", "Puissance & Onction", "Victoire & Autorité", "Direction Divine")

    val filteredDeclarations = remember(declarations, selectedTheme) {
        if (selectedTheme == "Tous") declarations
        else declarations.filter { it.theme.contains(selectedTheme, ignoreCase = true) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_declaration_fab"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle déclaration")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("faith_declarations_screen")
        ) {
            Surface(
                color = Color(0xFFFEF3C7),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Column {
                        Text(
                            text = "Déclarations & Décrets de Foi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "« La mort et la vie sont au pouvoir de la langue » (Proverbes 18:21)",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(themes) { theme ->
                    FilterChip(
                        selected = selectedTheme == theme,
                        onClick = { selectedTheme = theme },
                        label = { Text(theme, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredDeclarations) { decl ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("declaration_card_${decl.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = decl.theme,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { testimonyDialogDecl = decl },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Celebration,
                                            contentDescription = "Lier un témoignage",
                                            tint = Color(0xFFD97706)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onToggleFavorite(decl) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (decl.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favori",
                                            tint = if (decl.isFavorite) Color(0xFFDC2626) else MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Déclaration", "${decl.title}\n\n${decl.declarationText}\n\nAppui: ${decl.scriptureRef}"))
                                            Toast.makeText(context, "Déclaration copiée !", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Partager")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = decl.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = decl.declarationText,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Versets d'ancrage : ${decl.scriptureRef}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${decl.recitationCount} proclamations",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Button(
                                    onClick = { onIncrementRecitation(decl) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text("J'ai Proclamé ! (+1)", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Testimony linked to Faith Declaration Dialog
    if (testimonyDialogDecl != null) {
        val d = testimonyDialogDecl!!
        var testimonyTitle by remember { mutableStateOf("Témoignage suite à la proclamation : ${d.title}") }
        var testimonyStory by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { testimonyDialogDecl = null },
            title = {
                Text("Témoignage lié à cette Déclaration", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Déclaration : ${d.title}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = testimonyTitle,
                        onValueChange = { testimonyTitle = it },
                        label = { Text("Titre du témoignage *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = testimonyStory,
                        onValueChange = { testimonyStory = it },
                        label = { Text("Racontez comment Dieu s'est manifesté suite à votre proclamation...") },
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
                                    sourceType = "DECLARATION",
                                    sourceReference = d.title,
                                    story = testimonyStory
                                )
                            )
                            testimonyDialogDecl = null
                            Toast.makeText(context, "Témoignage lié à la déclaration enregistré !", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { testimonyDialogDecl = null }) { Text("Annuler") }
            }
        )
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var theme by remember { mutableStateOf("Identité en Christ") }
        var text by remember { mutableStateOf("") }
        var scriptureRef by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nouvelle Déclaration de Foi") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre de la déclaration *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = theme, onValueChange = { theme = it }, label = { Text("Thème (Identité, Guérison, etc.)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Texte de la proclamation à voix haute *") }, modifier = Modifier.fillMaxWidth(), minLines = 4)
                    OutlinedTextField(value = scriptureRef, onValueChange = { scriptureRef = it }, label = { Text("Versets d'ancrage biblique") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && text.isNotBlank()) {
                            onSaveDeclaration(
                                FaithDeclaration(
                                    title = title,
                                    theme = theme,
                                    declarationText = text,
                                    scriptureRef = scriptureRef.ifBlank { "Romains 8" }
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_declaration_button")
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Annuler") }
            }
        )
    }
}
