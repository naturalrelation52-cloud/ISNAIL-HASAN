package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HabitCalendarDay
import com.example.data.util.HabitCalculationUtils
import com.example.ui.theme.BentoPurpleContainer
import com.example.ui.theme.BentoPurpleDark
import com.example.ui.theme.BentoPurpleDeep
import com.example.ui.theme.BentoPurpleLight
import com.example.ui.theme.BentoPurplePrimary
import com.example.ui.theme.BentoRose
import com.example.ui.theme.BentoTeal
import com.example.ui.theme.parseHexColor
import com.example.ui.viewmodel.HabitViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailsScreen(
    habitId: Long,
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    val habitWithStats = homeState.habits.find { it.habit.id == habitId }

    if (habitWithStats == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Habit Details") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Habit not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    val habit = habitWithStats.habit
    val habitColor = parseHexColor(habit.colorHex)

    // Calculate calendar days for selected month
    val today = HabitCalculationUtils.getTodayDate()
    val todayStr = HabitCalculationUtils.getTodayDateString()

    val completionsMap = remember(habitWithStats, selectedMonth) {
        val map = mutableMapOf<String, Boolean>()
        if (habitWithStats.isCompletedToday) {
            map[todayStr] = true
        }
        map
    }

    val calendarDays = remember(habit, selectedMonth, habitWithStats) {
        HabitCalculationUtils.calculateMonthCalendarDays(
            habit = habit,
            completionsMap = completionsMap,
            yearMonth = selectedMonth,
            today = today
        )
    }

    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    val monthTitle = selectedMonth.format(monthFormatter)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Habit Details",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.25).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("details_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onNavigateToEdit(habitId) },
                        modifier = Modifier.testTag("details_edit_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Habit", tint = BentoPurplePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("habit_details_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bento Hero Habit Header Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                        .testTag("habit_details_hero_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        ) {
                            Text(text = habit.icon, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = habit.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (habit.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = habit.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (habit.frequencyType == "EVERY_DAY") "Every day" else "Scheduled specific days",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BentoPurplePrimary
                                    )
                                )

                                if (habit.reminderEnabled) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "🔔 ${habit.reminderTimeFormatted}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = BentoPurplePrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4 Bento Key Metric Tiles in 2x2 Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailMetricCard(
                            label = "Current Streak",
                            value = "${habitWithStats.currentStreak} Days",
                            icon = Icons.Default.LocalFireDepartment,
                            accentColor = BentoRose,
                            modifier = Modifier.weight(1f)
                        )
                        DetailMetricCard(
                            label = "Best Streak",
                            value = "${habitWithStats.bestStreak} Days",
                            icon = Icons.Default.EmojiEvents,
                            accentColor = BentoPurplePrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailMetricCard(
                            label = "30-Day Rate",
                            value = "${habitWithStats.completionRate30Days}%",
                            icon = Icons.Default.Percent,
                            accentColor = BentoPurplePrimary,
                            modifier = Modifier.weight(1f)
                        )
                        DetailMetricCard(
                            label = "Total Done",
                            value = "${habitWithStats.totalCompletedAllTime} Days",
                            icon = Icons.Default.TaskAlt,
                            accentColor = BentoTeal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Calendar Section Bento Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                        .testTag("habit_calendar_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Month Navigation Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = monthTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.changeSelectedMonth(-1) },
                                    modifier = Modifier.size(36.dp).testTag("prev_month_button")
                                ) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                                }
                                IconButton(
                                    onClick = { viewModel.changeSelectedMonth(1) },
                                    modifier = Modifier.size(36.dp).testTag("next_month_button")
                                ) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Day of week labels (M T W T F S S)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.width(36.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar Days Grid (7 columns)
                        val chunks = calendarDays.chunked(7)
                        chunks.forEach { week ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                week.forEach { day ->
                                    CalendarDayCell(
                                        day = day,
                                        habitColor = BentoPurplePrimary,
                                        onToggleDay = {
                                            if (!day.isFuture) {
                                                viewModel.toggleHabitCompletion(habit.id, day.dateString)
                                            }
                                        },
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Calendar Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LegendItem(color = BentoPurplePrimary, label = "Completed")
                            Spacer(modifier = Modifier.width(16.dp))
                            LegendItem(color = MaterialTheme.colorScheme.outlineVariant, label = "Missed")
                            Spacer(modifier = Modifier.width(16.dp))
                            LegendItem(color = MaterialTheme.colorScheme.surfaceVariant, label = "Future")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    day: HabitCalendarDay,
    habitColor: Color,
    onToggleDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInteractive = !day.isFuture && day.isCurrentMonth

    val cellBg = when {
        day.isCompleted -> BentoPurpleContainer
        day.isToday -> BentoPurpleContainer.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    val textColor = when {
        day.isCompleted -> BentoPurplePrimary
        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        day.isFuture -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        day.isToday -> BentoPurplePrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(cellBg)
            .border(
                width = if (day.isToday && !day.isCompleted) 1.5.dp else 0.dp,
                color = if (day.isToday && !day.isCompleted) BentoPurplePrimary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(
                enabled = isInteractive,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 18.dp),
                onClick = onToggleDay
            )
            .testTag("cal_day_${day.dateString}")
    ) {
        if (day.isCompleted) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(BentoPurplePrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else {
            Text(
                text = "${day.dayOfMonth}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (day.isToday || day.isCompleted) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DetailMetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BentoPurpleContainer)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

