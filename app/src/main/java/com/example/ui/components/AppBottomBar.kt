package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.localization.AppLanguage
import com.example.data.localization.LanguageStrings
import com.example.ui.AppScreen

@Composable
fun AppBottomBar(
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    onNavigate: (AppScreen) -> Unit,
    onOpenMoreMenu: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
            label = { Text(LanguageStrings.get("home", currentLanguage)) },
            modifier = Modifier.testTag("nav_home"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.BIBLE,
            onClick = { onNavigate(AppScreen.BIBLE) },
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Bible") },
            label = { Text(LanguageStrings.get("bible", currentLanguage)) },
            modifier = Modifier.testTag("nav_bible"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.SERMON_NOTES,
            onClick = { onNavigate(AppScreen.SERMON_NOTES) },
            icon = { Icon(Icons.Default.EditNote, contentDescription = "Sermons") },
            label = { Text(LanguageStrings.get("sermons", currentLanguage)) },
            modifier = Modifier.testTag("nav_sermons"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.PREACHING_PREP,
            onClick = { onNavigate(AppScreen.PREACHING_PREP) },
            icon = { Icon(Icons.Default.RecordVoiceOver, contentDescription = "Homilétique") },
            label = { Text(LanguageStrings.get("preaching", currentLanguage)) },
            modifier = Modifier.testTag("nav_preachings"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        val isMoreSelected = currentScreen in listOf(
            AppScreen.READING_PLANS,
            AppScreen.ASSISTANT_GEMINI,
            AppScreen.LIBRARY,
            AppScreen.PRAYER,
            AppScreen.DECLARATIONS,
            AppScreen.DICTIONARY,
            AppScreen.SYNC_PROFILE
        )

        NavigationBarItem(
            selected = isMoreSelected,
            onClick = onOpenMoreMenu,
            icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Plus d'outils") },
            label = { Text(LanguageStrings.get("more", currentLanguage)) },
            modifier = Modifier.testTag("nav_more"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
