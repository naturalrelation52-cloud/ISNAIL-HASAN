package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.HabitEntity
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.DaySelector
import com.example.ui.components.HabitColorSelector
import com.example.ui.components.HabitIconSelector
import com.example.ui.components.ReminderTimeSelector
import com.example.ui.theme.BentoPurplePrimary
import com.example.ui.theme.BentoRose
import com.example.ui.theme.parseHexColor
import com.example.ui.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    habitId: Long?,
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditMode = habitId != null && habitId > 0
    val homeState by viewModel.homeUiState.collectAsStateWithLifecycle()

    var existingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("📚") }
    var selectedColorHex by remember { mutableStateOf("#7C3AED") }
    var frequencyType by remember { mutableStateOf("EVERY_DAY") }
    var selectedDays by remember { mutableStateOf(setOf("1", "2", "3", "4", "5", "6", "7")) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderHour by remember { mutableIntStateOf(8) }
    var reminderMinute by remember { mutableIntStateOf(0) }
    var reminderTimeFormatted by remember { mutableStateOf("08:00 AM") }
    var nameError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(habitId, homeState.habits) {
        if (isEditMode) {
            val found = homeState.habits.find { it.habit.id == habitId }?.habit
            if (found != null) {
                existingHabit = found
                name = found.name
                description = found.description
                selectedIcon = found.icon
                selectedColorHex = found.colorHex
                frequencyType = found.frequencyType
                reminderEnabled = found.reminderEnabled
                reminderHour = found.reminderHour
                reminderMinute = found.reminderMinute
                reminderTimeFormatted = found.reminderTimeFormatted
                val days = found.daysOfWeek.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                selectedDays = if (days.isNotEmpty()) days else setOf("1", "2", "3", "4", "5", "6", "7")
            }
        }
    }

    if (showDeleteDialog && habitId != null) {
        ConfirmDialog(
            title = "Delete this habit?",
            message = "Your habit history may also be removed. This action cannot be undone.",
            confirmButtonText = "Delete",
            cancelButtonText = "Cancel",
            isDestructive = true,
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteHabit(habitId) {
                    onNavigateBack()
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Habit" else "New Habit",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.25).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.testTag("delete_habit_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Habit",
                                tint = BentoRose
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("add_edit_habit_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Bento Live Preview Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .testTag("habit_preview_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    ) {
                        Text(text = selectedIcon, fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = if (name.isNotBlank()) name else "Habit Name",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (name.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (description.isNotBlank()) description else (if (frequencyType == "EVERY_DAY") "Every day" else "${selectedDays.size} days a week"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (reminderEnabled) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🔔 Reminding at $reminderTimeFormatted",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = BentoPurplePrimary
                                )
                            )
                        }
                    }
                }
            }

            // Habit Name Input
            Column {
                Text(
                    text = "Habit Name",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError && it.isNotBlank()) nameError = false
                    },
                    placeholder = { Text("e.g. Reading, Drink Water, Workout") },
                    singleLine = true,
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text("Please enter a habit name", color = BentoRose)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPurplePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_name_input")
                )
            }

            // Description Input
            Column {
                Text(
                    text = "Description (Optional)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("e.g. 20 minutes before bedtime, 8 glasses") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPurplePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_description_input")
                )
            }

            // Choose Icon
            Column {
                Text(
                    text = "Choose Icon",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                HabitIconSelector(
                    selectedIcon = selectedIcon,
                    onSelectIcon = { selectedIcon = it }
                )
            }

            // Choose Color Theme
            Column {
                Text(
                    text = "Theme Color",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                HabitColorSelector(
                    selectedColorHex = selectedColorHex,
                    onSelectColorHex = { selectedColorHex = it }
                )
            }

            // Frequency Selection
            Column {
                Text(
                    text = "Frequency",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { frequencyType = "EVERY_DAY" }
                                .padding(vertical = 6.dp)
                                .testTag("frequency_every_day_row")
                        ) {
                            RadioButton(
                                selected = frequencyType == "EVERY_DAY",
                                onClick = { frequencyType = "EVERY_DAY" },
                                colors = RadioButtonDefaults.colors(selectedColor = BentoPurplePrimary),
                                modifier = Modifier.testTag("radio_every_day")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Every day",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { frequencyType = "SPECIFIC_DAYS" }
                                .padding(vertical = 6.dp)
                                .testTag("frequency_specific_days_row")
                        ) {
                            RadioButton(
                                selected = frequencyType == "SPECIFIC_DAYS",
                                onClick = { frequencyType = "SPECIFIC_DAYS" },
                                colors = RadioButtonDefaults.colors(selectedColor = BentoPurplePrimary),
                                modifier = Modifier.testTag("radio_specific_days")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Specific days of week",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (frequencyType == "SPECIFIC_DAYS") {
                            Spacer(modifier = Modifier.height(10.dp))
                            DaySelector(
                                selectedDays = selectedDays,
                                onToggleDay = { day ->
                                    val current = selectedDays.toMutableSet()
                                    if (current.contains(day)) {
                                        if (current.size > 1) {
                                            current.remove(day)
                                        }
                                    } else {
                                        current.add(day)
                                    }
                                    selectedDays = current
                                }
                            )
                        }
                    }
                }
            }

            // Reminder Time Section
            Column {
                Text(
                    text = "Reminder Time",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                ReminderTimeSelector(
                    reminderEnabled = reminderEnabled,
                    onReminderEnabledChange = { reminderEnabled = it },
                    reminderHour24 = reminderHour,
                    reminderMinute = reminderMinute,
                    onTimeChange = { h24, m, formatted ->
                        reminderHour = h24
                        reminderMinute = m
                        reminderTimeFormatted = formatted
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val daysStr = if (frequencyType == "EVERY_DAY") {
                        "1,2,3,4,5,6,7"
                    } else {
                        selectedDays.sorted().joinToString(",")
                    }

                    if (isEditMode && habitId != null) {
                        val updated = existingHabit?.copy(
                            name = name.trim(),
                            description = description.trim(),
                            icon = selectedIcon,
                            colorHex = selectedColorHex,
                            frequencyType = frequencyType,
                            daysOfWeek = daysStr,
                            reminderEnabled = reminderEnabled,
                            reminderHour = reminderHour,
                            reminderMinute = reminderMinute,
                            reminderTimeFormatted = reminderTimeFormatted
                        ) ?: HabitEntity(
                            id = habitId,
                            name = name.trim(),
                            description = description.trim(),
                            icon = selectedIcon,
                            colorHex = selectedColorHex,
                            frequencyType = frequencyType,
                            daysOfWeek = daysStr,
                            reminderEnabled = reminderEnabled,
                            reminderHour = reminderHour,
                            reminderMinute = reminderMinute,
                            reminderTimeFormatted = reminderTimeFormatted
                        )
                        viewModel.updateHabit(updated) {
                            onNavigateBack()
                        }
                    } else {
                        viewModel.createHabit(
                            name = name.trim(),
                            description = description.trim(),
                            icon = selectedIcon,
                            colorHex = selectedColorHex,
                            frequencyType = frequencyType,
                            daysOfWeek = daysStr,
                            reminderEnabled = reminderEnabled,
                            reminderHour = reminderHour,
                            reminderMinute = reminderMinute,
                            reminderTimeFormatted = reminderTimeFormatted
                        ) {
                            onNavigateBack()
                        }
                    }
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPurplePrimary),
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_habit_button")
            ) {
                Text(
                    text = if (isEditMode) "Save Changes" else "Create Habit",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
            }

            if (isEditMode) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoRose),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoRose.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delete_habit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete Habit",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

