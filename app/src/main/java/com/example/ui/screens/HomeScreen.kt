package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.bible.BibleRepositoryData
import com.example.data.bible.BibleVersion
import com.example.data.localization.AppLanguage
import com.example.data.localization.LanguageStrings
import com.example.data.model.SermonNote
import com.example.data.model.StudentProfile
import com.example.ui.AppScreen

data class QuickAction(
    val titleFr: String,
    val titleMg: String,
    val subtitleFr: String,
    val subtitleMg: String,
    val icon: ImageVector,
    val screen: AppScreen,
    val color: Color
)

@Composable
fun HomeScreen(
    profile: StudentProfile,
    currentLanguage: AppLanguage,
    recentNotes: List<SermonNote>,
    onNavigate: (AppScreen) -> Unit,
    onVerseClick: () -> Unit
) {
    val isMg = currentLanguage == AppLanguage.MALAGASY
    var selectedVerseVersion by remember { mutableStateOf(if (isMg) BibleVersion.MALAGASY else BibleVersion.SEMEUR) }
    val verseOfTheDay = remember {
        // Jean 14:26 - Mpananatra / Consolateur
        BibleRepositoryData.verses.find { it.book == "Jean" && it.chapter == 14 && it.verse == 26 }
            ?: BibleRepositoryData.verses.first()
    }

    val actions = listOf(
        QuickAction("Étude Biblique", "Fandalinana Baiboly", "3 Bibles comparées", "Baiboly 3 mampitaha", Icons.AutoMirrored.Filled.MenuBook, AppScreen.BIBLE, Color(0xFF1E3A8A)),
        QuickAction("Assistant Gemini IA", "Mpanolo-tsaina Gemini", "Exégèse & Homilétique", "Grika, Hebreo & Toriteny", Icons.Default.AutoAwesome, AppScreen.ASSISTANT_GEMINI, Color(0xFF0284C7)),
        QuickAction("Plans de Lecture", "Teti-pamakiana", "Chronologique & Annuel", "Ara-potoana & Isan-taona", Icons.AutoMirrored.Filled.MenuBook, AppScreen.READING_PLANS, Color(0xFF059669)),
        QuickAction("Notes de Sermon", "Fanamarihana Toriteny", "Prise de note fluide", "Fandraisana an-tsoratra", Icons.Default.EditNote, AppScreen.SERMON_NOTES, Color(0xFF0D9488)),
        QuickAction("Homilétique", "Fanomanana Toriteny", "Bâtisseur de sermon", "Drafitra toriteny masina", Icons.Default.RecordVoiceOver, AppScreen.PREACHING_PREP, Color(0xFFD97706)),
        QuickAction("Déclarations de Foi", "Fiekem-pinoana Velona", "Proclame la vie", "Fanambarana fandresena", Icons.Default.LocalFireDepartment, AppScreen.DECLARATIONS, Color(0xFFDC2626)),
        QuickAction("Prières & Gloire", "Vavaka & Voninahitra", "Sujets & Témoignages", "Fangatahana & Fisaorana", Icons.Default.VolunteerActivism, AppScreen.PRAYER, Color(0xFF7C3AED)),
        QuickAction("Bibliothèque", "Trano Fitehirizam-boky", "Livres théologiques", "Boky teolojika", Icons.Default.AutoStories, AppScreen.LIBRARY, Color(0xFF4338CA)),
        QuickAction("Dictionnaire & Lexique", "Rakibolana Baiboly", "Racines Grec & Hébreu", "Grika, Hebreo & Strong", Icons.Default.Translate, AppScreen.DICTIONARY, Color(0xFF2563EB)),
        QuickAction("Profil & Cloud", "Mombamomba & Rahona", "Sauvegarde Google", "Fampitahana Google", Icons.Default.CloudDone, AppScreen.SYNC_PROFILE, Color(0xFF10B981))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_holy_spirit),
                    contentDescription = "Bannière École du Saint Esprit",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0F172A), Color(0xF00F172A)),
                                startY = 40f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = if (isMg) "SEKOLY AMBONY" else "ÉCOLE SUPÉRIEURE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33FFFFFF)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Série",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${profile.studyStreakDays} ${LanguageStrings.get("study_streak", currentLanguage)}",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LanguageStrings.get("app_name", currentLanguage),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = if (isMg) "Ampianarina sy tarihin'ny Fanahy Masina ao anaty" else "Formé et enseigné par le Maître intérieur",
                        fontSize = 13.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }

        // 2. Verse of the Day Card with 3 translations
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { onVerseClick() }
                    .testTag("verse_of_the_day_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${LanguageStrings.get("verse_of_day", currentLanguage)} • ${verseOfTheDay.book} ${verseOfTheDay.chapter}:${verseOfTheDay.verse}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = selectedVerseVersion.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "« ${verseOfTheDay.getText(selectedVerseVersion)} »",
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Version selector chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedVerseVersion == BibleVersion.MALAGASY,
                            onClick = { selectedVerseVersion = BibleVersion.MALAGASY },
                            label = { Text("Baiboly Malagasy", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        FilterChip(
                            selected = selectedVerseVersion == BibleVersion.SEMEUR,
                            onClick = { selectedVerseVersion = BibleVersion.SEMEUR },
                            label = { Text("Semeur (BDS)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        FilterChip(
                            selected = selectedVerseVersion == BibleVersion.AMPLIFIED,
                            onClick = { selectedVerseVersion = BibleVersion.AMPLIFIED },
                            label = { Text("Amplified (AMP)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }

        // 3. Quick Action Grid Title
        item {
            Text(
                text = if (isMg) "Ireo Fitaovana Fandalinana & Fampiharana" else "Modules d'Étude & Pratique",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Action cards (2x2 grid)
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (rowItems in actions.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (action in rowItems) {
                            val title = if (isMg) action.titleMg else action.titleFr
                            val subtitle = if (isMg) action.subtitleMg else action.subtitleFr

                            ElevatedCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(108.dp)
                                    .clickable { onNavigate(action.screen) }
                                    .testTag("action_${action.screen.name}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(action.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = action.icon,
                                            contentDescription = title,
                                            tint = action.color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = subtitle,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Recent Sermon Notes Preview
        if (recentNotes.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isMg) "Fanamarihana Toriteny Farany" else "Dernières Notes de Sermon",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isMg) "Hijery rehetra" else "Voir tout",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigate(AppScreen.SERMON_NOTES) }
                    )
                }

                recentNotes.take(2).forEach { note ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { onNavigate(AppScreen.SERMON_NOTES) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = note.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = note.scriptureRef,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${if (isMg) "Mpitory" else "Prédicateur"} : ${note.preacher}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = note.keyTakeaways,
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
