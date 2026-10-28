package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.model.CustomReadingPlanEntity
import com.example.data.readingplan.PlanCategory
import com.example.data.readingplan.PlanDay
import com.example.data.readingplan.ReadingPlan
import com.example.data.readingplan.ReadingPlanRepository

data class UnifiedDisplayPlan(
    val id: String,
    val isCustom: Boolean,
    val categoryName: String,
    val title: String,
    val description: String,
    val durationDays: Int,
    val days: List<PlanDay>,
    val customEntity: CustomReadingPlanEntity? = null
)

@Composable
fun ReadingPlansScreen(
    currentLanguage: AppLanguage,
    customPlans: List<CustomReadingPlanEntity>,
    completedDays: Set<String>,
    onToggleDayCompleted: (String, Int) -> Unit,
    onOpenPassage: (String, Int) -> Unit,
    onSaveCustomPlan: (CustomReadingPlanEntity) -> Unit,
    onDeleteCustomPlan: (CustomReadingPlanEntity) -> Unit
) {
    val isMg = currentLanguage == AppLanguage.MALAGASY
    var selectedCategoryFilter by remember { mutableStateOf<String?>("TOUS") }
    var expandedPlanId by remember { mutableStateOf<String?>("plan_saint_esprit") }
    var showCreatePlanDialog by remember { mutableStateOf(false) }

    // Map built-in plans to unified display model
    val builtInUnified = remember(isMg) {
        ReadingPlanRepository.allPlans.map { plan ->
            UnifiedDisplayPlan(
                id = plan.id,
                isCustom = false,
                categoryName = if (isMg) plan.category.displayNameMg else plan.category.displayNameFr,
                title = if (isMg) plan.titleMg else plan.titleFr,
                description = if (isMg) plan.descriptionMg else plan.descriptionFr,
                durationDays = plan.durationDays,
                days = plan.days
            )
        }
    }

    // Map custom plans from Room to unified display model
    val customUnified = remember(customPlans) {
        customPlans.map { entity ->
            val parsedDays = entity.daysJson.lines()
                .filter { it.isNotBlank() }
                .mapIndexed { idx, line ->
                    val parts = line.split("|")
                    val dayNum = parts.getOrNull(0)?.toIntOrNull() ?: (idx + 1)
                    val title = parts.getOrNull(1)?.ifBlank { "Jour $dayNum" } ?: "Jour $dayNum"
                    val passage = parts.getOrNull(2)?.ifBlank { "Jean 14" } ?: "Jean 14"
                    val focus = parts.getOrNull(3)?.ifBlank { "Méditation personnelle" } ?: "Méditation personnelle"

                    val targetBook = passage.split(" ").firstOrNull() ?: "Jean"
                    val chapterNum = passage.filter { it.isDigit() }.toIntOrNull() ?: 1

                    PlanDay(
                        day = dayNum,
                        titleFr = title,
                        titleMg = title,
                        scripturePassage = passage,
                        targetBook = targetBook,
                        targetChapter = chapterNum,
                        focusFr = focus,
                        focusMg = focus
                    )
                }

            UnifiedDisplayPlan(
                id = entity.planId,
                isCustom = true,
                categoryName = entity.categoryName.ifBlank { "Personnalisé" },
                title = entity.title,
                description = entity.description,
                durationDays = entity.durationDays,
                days = parsedDays,
                customEntity = entity
            )
        }
    }

    val allUnifiedPlans = remember(builtInUnified, customUnified) {
        customUnified + builtInUnified
    }

    // Dynamic list of categories for filter chips
    val allCategoryNames = remember(allUnifiedPlans) {
        val cats = allUnifiedPlans.map { it.categoryName }.distinct()
        listOf("TOUS", "MES PLANS") + cats
    }

    val filteredPlans = remember(allUnifiedPlans, selectedCategoryFilter) {
        when (selectedCategoryFilter) {
            "TOUS", null -> allUnifiedPlans
            "MES PLANS" -> allUnifiedPlans.filter { it.isCustom }
            else -> allUnifiedPlans.filter { it.categoryName.equals(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePlanDialog = true },
                modifier = Modifier.testTag("create_custom_reading_plan_fab"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Créer un plan personnalisé")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("reading_plans_screen")
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isMg) "Teti-pamakiana Baiboly Masina" else "Plans de Lecture Personnalisables",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primary) {
                                    Text(
                                        text = "PRO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isMg)
                                    "Mamorona sy manamboara teti-pamakiana manokana araka izay tianao"
                                else
                                    "Créez vos parcours sur mesure avec vos passages, jours et objectifs",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Quick Create Custom Plan Banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCreatePlanDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Créer un nouveau plan de lecture sur mesure",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Button(
                            onClick = { showCreatePlanDialog = true },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("+ Créer", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allCategoryNames) { cat ->
                        val isSelected = (selectedCategoryFilter == cat) || (cat == "TOUS" && selectedCategoryFilter == null)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = if (cat == "TOUS") null else cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }

            // Plans list
            items(filteredPlans, key = { it.id }) { plan ->
                val isExpanded = expandedPlanId == plan.id
                val totalDays = plan.days.size
                val completedCount = plan.days.count { day ->
                    completedDays.contains("${plan.id}_day_${day.day}")
                }
                val progressRatio = if (totalDays > 0) completedCount.toFloat() / totalDays else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("plan_card_${plan.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (plan.isCustom) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header of the plan
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedPlanId = if (isExpanded) null else plan.id
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (plan.isCustom) Color(0xFF059669).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Text(
                                            text = plan.categoryName.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (plan.isCustom) Color(0xFF059669) else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    if (plan.isCustom) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFDCFCE7),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Text(
                                                text = "PLAN PERSONNALISÉ",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF16A34A),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = plan.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = plan.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = if (isExpanded) 4 else 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (plan.isCustom && plan.customEntity != null) {
                                    IconButton(
                                        onClick = { onDeleteCustomPlan(plan.customEntity) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Supprimer le plan", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(17.dp))
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Dérouler",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isMg)
                                    "$completedCount amin'ny $totalDays andro vita (${(progressRatio * 100).toInt()}%)"
                                else
                                    "$completedCount sur $totalDays jours complétés (${(progressRatio * 100).toInt()}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (plan.isCustom) Color(0xFF059669) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        // Expanded Days list
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                plan.days.forEach { day ->
                                    val dayKey = "${plan.id}_day_${day.day}"
                                    val isDone = completedDays.contains(dayKey)

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDone)
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    modifier = Modifier.weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    IconButton(
                                                        onClick = { onToggleDayCompleted(plan.id, day.day) },
                                                        modifier = Modifier.size(36.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                            contentDescription = "Marquer comme fait",
                                                            tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.width(6.dp))

                                                    Column {
                                                        Text(
                                                            text = "${if (isMg) "Andro" else "Jour"} ${day.day} : ${if (isMg) day.titleMg else day.titleFr}",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Text(
                                                            text = day.scripturePassage,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }

                                                OutlinedButton(
                                                    onClick = { onOpenPassage(day.targetBook, day.targetChapter) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(34.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Book,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = if (isMg) "Hamaky" else "Lire",
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "🎯 ${if (isMg) day.focusMg else day.focusFr}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(start = 42.dp)
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
    }

    // Dialog for creating a custom reading plan
    if (showCreatePlanDialog) {
        var planTitle by remember { mutableStateOf("") }
        var planCategory by remember { mutableStateOf("Thématique Personnalisé") }
        var planDescription by remember { mutableStateOf("") }
        var planDaysCount by remember { mutableIntStateOf(7) }

        // Template list of days for easy customization
        val defaultDays = remember(planDaysCount) {
            mutableStateListOf<Triple<String, String, String>>().apply {
                for (i in 1..planDaysCount) {
                    add(Triple("Jour $i : Méditation", "Jean 14", "Écouter la voix du Saint-Esprit"))
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showCreatePlanDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Créer un Plan de Lecture")
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = planTitle,
                            onValueChange = { planTitle = it },
                            label = { Text("Titre du plan * (ex: 21 Jours de Foi)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = planCategory,
                            onValueChange = { planCategory = it },
                            label = { Text("Nom de la catégorie personnalisée *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = planDescription,
                            onValueChange = { planDescription = it },
                            label = { Text("Description & Objectif spirituel") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        Text("Nombre de jours : $planDaysCount jours", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(3, 5, 7, 14, 21, 30).forEach { days ->
                                FilterChip(
                                    selected = planDaysCount == days,
                                    onClick = { planDaysCount = days },
                                    label = { Text("$days j", fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    item {
                        Text("Configuration des jours de lecture :", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    items(defaultDays.indices.toList()) { idx ->
                        val dayItem = defaultDays.getOrNull(idx) ?: Triple("Jour ${idx + 1}", "Jean 14", "Méditation")
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Jour ${idx + 1} :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                OutlinedTextField(
                                    value = dayItem.first,
                                    onValueChange = { newT -> defaultDays[idx] = Triple(newT, dayItem.second, dayItem.third) },
                                    label = { Text("Titre du jour") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = dayItem.second,
                                    onValueChange = { newP -> defaultDays[idx] = Triple(dayItem.first, newP, dayItem.third) },
                                    label = { Text("Passage biblique (ex: Psaume 91, Jean 14)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = dayItem.third,
                                    onValueChange = { newF -> defaultDays[idx] = Triple(dayItem.first, dayItem.second, newF) },
                                    label = { Text("Objectif / Focus") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (planTitle.isNotBlank()) {
                            val serializedDays = defaultDays.mapIndexed { idx, item ->
                                "${idx + 1}|${item.first}|${item.second}|${item.third}"
                            }.joinToString("\n")

                            val customEntity = CustomReadingPlanEntity(
                                planId = "custom_plan_${System.currentTimeMillis()}",
                                title = planTitle,
                                categoryName = planCategory.ifBlank { "Personnalisé" },
                                description = planDescription.ifBlank { "Parcours de lecture personnalisé" },
                                durationDays = planDaysCount,
                                daysJson = serializedDays
                            )
                            onSaveCustomPlan(customEntity)
                            showCreatePlanDialog = false
                        }
                    }
                ) {
                    Text("Créer le Plan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePlanDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
