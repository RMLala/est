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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SearchResultItem
import com.example.ui.AppScreen

@Composable
fun GlobalSearchScreen(
    searchQuery: String,
    searchFilter: String,
    results: List<SearchResultItem>,
    onQueryChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val filters = listOf(
        "TOUS" to "Tous les contenus",
        "BIBLE" to "Bible (3 versions)",
        "NOTES" to "Notes de sermon",
        "JOURNAL" to "Two-Way Journal",
        "PREDICATION" to "Prédications",
        "LIVRES" to "Bibliothèque",
        "PRIERES" to "Prières",
        "DICT" to "Dictionnaire"
    )

    val filteredResults = remember(results, searchFilter) {
        when (searchFilter) {
            "BIBLE" -> results.filterIsInstance<SearchResultItem.VerseResult>()
            "NOTES" -> results.filterIsInstance<SearchResultItem.NoteResult>()
            "JOURNAL" -> results.filterIsInstance<SearchResultItem.JournalResult>()
            "PREDICATION" -> results.filterIsInstance<SearchResultItem.PreachingResult>()
            "LIVRES" -> results.filterIsInstance<SearchResultItem.BookResult>()
            "PRIERES" -> results.filterIsInstance<SearchResultItem.PrayerResult>()
            "DICT" -> results.filter { it is SearchResultItem.DictResult || it is SearchResultItem.ConcordanceResult }
            else -> results
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("global_search_screen")
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Recherche universelle (Esprit, Fanahy, Foi, Prière)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Effacer")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("universal_search_input"),
            singleLine = true
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { (key, label) ->
                FilterChip(
                    selected = searchFilter == key,
                    onClick = { onFilterChange(key) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        if (searchQuery.isBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Moteur de Recherche Intuitif",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tapez un mot-clé ou passage biblique pour chercher simultanément dans les Bibles Malagasy/Semeur/AMP, vos notes, prédications, prières et dictionnaire théologique.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (filteredResults.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Aucun résultat pour « $searchQuery »",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Essayez un autre mot-clé ou élargissez le filtre de recherche.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "${filteredResults.size} résultats trouvés",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(filteredResults) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            when (item) {
                                is SearchResultItem.VerseResult -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${item.verse.book} ${item.verse.chapter}:${item.verse.verse}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                            Text(text = item.version.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = item.matchedText, fontSize = 14.sp, lineHeight = 20.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Ouvrir dans la Bible →",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable { onNavigate(AppScreen.BIBLE) }
                                    )
                                }

                                is SearchResultItem.NoteResult -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.EditNote, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = item.note.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                                            Text(text = "Note Sermon", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Prédicateur: ${item.note.preacher} • ${item.note.scriptureRef}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.note.content, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }

                                is SearchResultItem.PreachingResult -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = item.prep.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF3C7)) {
                                            Text(text = "Homilétique", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Passage: ${item.prep.mainPassage} • Thème: ${item.prep.theme}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                is SearchResultItem.BookResult -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AutoStories, contentDescription = null, tint = Color(0xFF4338CA), modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = item.book.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE0E7FF)) {
                                            Text(text = item.book.category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3730A3), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Auteur: ${item.book.author}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.book.summary, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }

                                is SearchResultItem.PrayerResult -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = item.prayer.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEDE9FE)) {
                                            Text(text = "Prière", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5B21B6), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.prayer.requestDetails, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }

                                is SearchResultItem.TestimonyResult -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = item.testimony.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF3C7)) {
                                            Text(text = "Témoignage (${item.testimony.sourceType})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Réf : ${item.testimony.sourceReference}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.testimony.story, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }

                                is SearchResultItem.DictResult -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Translate, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "${item.entry.term} (${item.entry.originalWord})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE0F2FE)) {
                                            Text(text = item.entry.strongRef, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.entry.definition, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }

                                is SearchResultItem.ConcordanceResult -> {
                                    Text(text = "Concordance : ${item.topic.topic}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = item.topic.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.topic.references.take(3).joinToString(" • "), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }

                                is SearchResultItem.JournalResult -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = item.entry.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDDD6FE)) {
                                            Text(text = "Two-Way (${item.entry.mood})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5B21B6), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Catégorie : ${item.entry.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Prière: ${item.entry.myPrayer}", fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    if (item.entry.godsResponse.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "Réponse: ${item.entry.godsResponse}", fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = Color(0xFF92400E))
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Ouvrir dans le Journal →",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable { onNavigate(AppScreen.PRAYER) }
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
