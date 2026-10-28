package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.localization.LanguageStrings
import com.example.ui.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    isGoogleSynced: Boolean,
    onSearchClick: () -> Unit,
    onOpenThemeCustomizer: () -> Unit,
    onProfileClick: () -> Unit
) {
    val screenTitleKey = when (currentScreen) {
        AppScreen.HOME -> "home"
        AppScreen.BIBLE -> "bible"
        AppScreen.SERMON_NOTES -> "sermons"
        AppScreen.PREACHING_PREP -> "preaching"
        AppScreen.LIBRARY -> "library"
        AppScreen.PRAYER -> "prayers"
        AppScreen.DECLARATIONS -> "declarations"
        AppScreen.DICTIONARY -> "dictionary"
        AppScreen.READING_PLANS -> "reading_plans"
        AppScreen.ASSISTANT_GEMINI -> "assistant_gemini"
        AppScreen.SEARCH -> "search"
        AppScreen.SYNC_PROFILE -> "profile"
    }
    val titleText = LanguageStrings.get(screenTitleKey, currentLanguage)

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Saint Esprit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = titleText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        },
        actions = {
            // Theme & Language Palette Customizer Button
            IconButton(
                onClick = onOpenThemeCustomizer,
                modifier = Modifier.testTag("top_theme_customizer_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = currentLanguage.flag, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Thèmes & Polices",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Rechercher",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.testTag("top_profile_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isGoogleSynced) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Synchronisé Google",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Non synchronisé",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profil Google",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
