package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SyncProfileScreen(
    profile: StudentProfile,
    totalNotes: Int,
    totalPreachings: Int,
    totalBooks: Int,
    answeredPrayers: Int,
    onToggleSync: (Boolean) -> Unit,
    onTriggerSync: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSyncingNow by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sync_profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Google Account & Cloud Sync Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.studentName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Vérifié",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = profile.email,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (profile.isGoogleSynced) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (profile.isGoogleSynced) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = if (profile.isGoogleSynced) Color(0xFF16A34A) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (profile.isGoogleSynced) "Compte Google Synchronisé" else "Synchronisation Google en pause",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (profile.isGoogleSynced) Color(0xFF166534) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Dernière sync : ${profile.lastSyncTime}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = profile.isGoogleSynced,
                                onCheckedChange = onToggleSync,
                                modifier = Modifier.testTag("google_sync_switch")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isSyncingNow = true
                                delay(1200)
                                isSyncingNow = false
                                onTriggerSync()
                                Toast.makeText(context, "Données synchronisées avec succès avec Google Cloud !", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_sync_button"),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isSyncingNow
                    ) {
                        if (isSyncingNow) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synchronisation en cours...")
                        } else {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synchroniser maintenant")
                        }
                    }
                }
            }
        }

        // 2. Student Statistics Overview
        item {
            Text(
                text = "Statistiques de l'Étudiant",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Série d'Étude",
                    value = "${profile.studyStreakDays} jours",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFF59E0B)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Notes Sermons",
                    value = "$totalNotes",
                    icon = Icons.Default.EditNote,
                    color = Color(0xFF0D9488)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Prédications",
                    value = "$totalPreachings",
                    icon = Icons.Default.RecordVoiceOver,
                    color = Color(0xFFD97706)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Exaucements",
                    value = "$answeredPrayers",
                    icon = Icons.Default.Celebration,
                    color = Color(0xFF16A34A)
                )
            }
        }

        // 3. Backup & Export Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sauvegarde Locale & Export des Notes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exportez l'ensemble de vos sermons, canevas de prédications et prières pour archivage ou partage.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val exportPayload = """
                                ÉCOLE DU SAINT ESPRIT - EXPORT DE L'ÉTUDIANT
                                ===========================================
                                Étudiant : ${profile.studentName} (${profile.email})
                                Série : ${profile.studyStreakDays} jours
                                Notes de sermon : $totalNotes
                                Prédications préparées : $totalPreachings
                                Livres théologiques : $totalBooks
                                Prières exaucées : $answeredPrayers
                                Statut Google Sync : ${if (profile.isGoogleSynced) "Actif" else "Inactif"}
                            """.trimIndent()
                            clipboard.setPrimaryClip(ClipData.newPlainText("Export École du Saint Esprit", exportPayload))
                            Toast.makeText(context, "Export copié dans le presse-papiers !", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copier le récapitulatif complet")
                    }
                }
            }
        }

        // 4. About App
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "École du Saint Esprit v1.0",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dédié à la formation des disciples, l'onction ministérielle, l'étude approfondie des Écritures et la vie guidée par l'Esprit Saint.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
