package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.ai.ChatMessage
import com.example.data.ai.MessageSender
import com.example.data.localization.AppLanguage
import com.example.data.localization.LanguageStrings
import kotlinx.coroutines.launch

@Composable
fun GeminiAssistantScreen(
    currentLanguage: AppLanguage,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onCreateSermonFromAi: (String) -> Unit,
    onCreateDeclarationFromAi: (String) -> Unit
) {
    val isMg = currentLanguage == AppLanguage.MALAGASY
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }

    val quickChips = if (isMg) listOf(
        "🎙️ Drafitra toriteny momba ny Fanahy Masina",
        "🔍 Fandinihana teny grika Parakletos sy Dunamis",
        "🕊️ Fitararihana vavaka feno hery",
        "🔥 Fanambarana finoana amin'ny fandresena",
        "✨ Fampianarana momba ny fanomezam-pahasoavana"
    ) else listOf(
        "🎙️ Plan de prédication sur l'onction du Saint-Esprit",
        "🔍 Exégèse grecque de Paraklētos & Dunamis",
        "🕊️ Conducteur de prière inspirée par l'Esprit",
        "🔥 Décrets de foi et d'autorité biblique",
        "✨ Étude sur les 9 dons spirituels (1 Cor 12)"
    )

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("gemini_assistant_screen")
    ) {
        // Top Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isMg) "Mpanolo-tsaina Gemini IA" else "Conseiller Spirituel Gemini IA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = LanguageStrings.get("assistant_subtitle", currentLanguage),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Quick Suggestions Horizontal Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickChips) { chipText ->
                FilterChip(
                    selected = false,
                    onClick = {
                        onSendMessage(chipText)
                    },
                    label = { Text(chipText, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                )
            }
        }

        // Chat Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = LanguageStrings.get("gemini_welcome", currentLanguage),
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { message ->
                val isUser = message.sender == MessageSender.USER

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(top = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.92f),
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (isUser) 14.dp else 2.dp,
                                bottomEnd = if (isUser) 2.dp else 14.dp
                            ),
                            color = if (isUser)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            SelectionContainer {
                                Text(
                                    text = message.text,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // Assistant Action Buttons (Copy, Save to Sermon, Save to Declaration)
                        if (!isUser) {
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Gemini Spirituel", message.text)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, if (isMg) "Voalika ny soratra" else "Copié dans le presse-papier", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copier",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        onCreateSermonFromAi(message.text)
                                        Toast.makeText(context, if (isMg) "Nalefa ho toriteny" else "Transféré aux notes de sermon", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = "Ajouter aux notes",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        onCreateDeclarationFromAi(message.text)
                                        Toast.makeText(context, if (isMg) "Nalefa ho fiekem-pinoana" else "Ajouté aux déclarations de foi", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Déclaration de foi",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isMg) "Eo am-pandinihana sy fanoratana..." else "Gemini médite et rédige la réponse...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Bottom Input Field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(LanguageStrings.get("ask_gemini", currentLanguage), fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("gemini_prompt_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isLoading) {
                            val textToSend = inputText
                            inputText = ""
                            onSendMessage(textToSend)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("send_gemini_prompt_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Envoyer",
                        tint = if (inputText.isNotBlank() && !isLoading) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
