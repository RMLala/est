package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.bible.BibleRepositoryData
import com.example.data.model.DictionaryTermEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryConcordanceScreen(
    roomTerms: List<DictionaryTermEntity>,
    onSaveTerm: (DictionaryTermEntity) -> Unit,
    onDeleteTerm: (DictionaryTermEntity) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dictionnaire Room, 1: Concordance
    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguageFilter by remember { mutableStateOf("Tous") } // Tous, Grec, Hébreu
    var showAddDialog by remember { mutableStateOf(false) }

    // Search in Room dictionary
    val filteredDictionary = remember(roomTerms, searchQuery, selectedLanguageFilter) {
        var list = roomTerms
        if (selectedLanguageFilter != "Tous") {
            list = list.filter { it.language.equals(selectedLanguageFilter, ignoreCase = true) }
        }
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.term.contains(searchQuery, ignoreCase = true) ||
                        it.originalWord.contains(searchQuery, ignoreCase = true) ||
                        it.transliteration.contains(searchQuery, ignoreCase = true) ||
                        it.definition.contains(searchQuery, ignoreCase = true) ||
                        it.strongRef.contains(searchQuery, ignoreCase = true) ||
                        it.theologicalSignificance.contains(searchQuery, ignoreCase = true)
            }
        }
        list
    }

    val filteredConcordance = remember(searchQuery) {
        if (searchQuery.isBlank()) BibleRepositoryData.concordanceTopics
        else BibleRepositoryData.concordanceTopics.filter {
            it.topic.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.references.any { ref -> ref.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_dictionary_term_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter un terme biblique")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("dictionary_concordance_screen")
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Dictionnaire Biblique Room (${roomTerms.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Concordance Thématique", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Dedicated Search Component for Dictionary
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                if (selectedTab == 0) "Rechercher une définition (ex: Paraclet, Ruach, Dunamis)..."
                                else "Rechercher un sujet ou référence (ex: Esprit, Foi)..."
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Effacer")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dictionary_search_input"),
                        singleLine = true
                    )

                    if (selectedTab == 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Langue d'origine :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            listOf("Tous", "Grec", "Hébreu").forEach { lang ->
                                FilterChip(
                                    selected = selectedLanguageFilter == lang,
                                    onClick = { selectedLanguageFilter = lang },
                                    label = { Text(lang, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                // Dictionary List from Room Database
                if (filteredDictionary.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Aucun terme trouvé dans le dictionnaire", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Vous pouvez ajouter un terme en appuyant sur le bouton +", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredDictionary) { dict ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dict_entry_${dict.term}"),
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
                                        Column {
                                            Text(
                                                text = dict.term,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "${dict.originalWord} (${dict.transliteration})",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "${dict.language} • ${dict.strongRef}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }

                                            if (dict.isCustom) {
                                                IconButton(
                                                    onClick = { onDeleteTerm(dict) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Définition :",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = dict.definition,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (dict.theologicalSignificance.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = "Révélation & Signification Théologique :",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = dict.theologicalSignificance,
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    if (dict.keyVerses.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Passages Clés : ${dict.keyVerses}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Concordance List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredConcordance) { conc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("concordance_entry_${conc.topic}"),
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
                                        text = conc.topic,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = conc.category,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = conc.description,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Références Bibliques :",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                conc.references.forEach { ref ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "•", color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(14.dp))
                                        Text(
                                            text = ref,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add New Dictionary Term Dialog
    if (showAddDialog) {
        var term by remember { mutableStateOf("") }
        var originalWord by remember { mutableStateOf("") }
        var transliteration by remember { mutableStateOf("") }
        var language by remember { mutableStateOf("Grec") }
        var strongRef by remember { mutableStateOf("") }
        var definition by remember { mutableStateOf("") }
        var significance by remember { mutableStateOf("") }
        var keyVerses by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Ajouter un terme au dictionnaire Room") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { OutlinedTextField(value = term, onValueChange = { term = it }, label = { Text("Terme en Français (ex: Paraclet) *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = originalWord, onValueChange = { originalWord = it }, label = { Text("Mot original (Grec ou Hébreu)") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = transliteration, onValueChange = { transliteration = it }, label = { Text("Translittération phonétique") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = strongRef, onValueChange = { strongRef = it }, label = { Text("Numéro Strong (ex: G3875)") }, modifier = Modifier.fillMaxWidth()) }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Grec", "Hébreu").forEach { l ->
                                FilterChip(
                                    selected = language == l,
                                    onClick = { language = l },
                                    label = { Text(l) }
                                )
                            }
                        }
                    }
                    item { OutlinedTextField(value = definition, onValueChange = { definition = it }, label = { Text("Définition biblique *") }, modifier = Modifier.fillMaxWidth(), minLines = 3) }
                    item { OutlinedTextField(value = significance, onValueChange = { significance = it }, label = { Text("Portée théologique dans l'Esprit") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
                    item { OutlinedTextField(value = keyVerses, onValueChange = { keyVerses = it }, label = { Text("Passages clés (séparés par virgules)") }, modifier = Modifier.fillMaxWidth()) }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (term.isNotBlank() && definition.isNotBlank()) {
                            onSaveTerm(
                                DictionaryTermEntity(
                                    term = term,
                                    originalWord = originalWord,
                                    transliteration = transliteration,
                                    language = language,
                                    strongRef = strongRef.ifBlank { "Strong" },
                                    definition = definition,
                                    theologicalSignificance = significance,
                                    keyVerses = keyVerses,
                                    isCustom = true
                                )
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Enregistrer dans Room")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Annuler") }
            }
        )
    }
}
