package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.HabitEntity
import com.example.ui.theme.BentoPurpleContainer
import com.example.ui.theme.BentoPurplePrimary
import java.util.Locale

@Composable
fun ReminderTimeSelector(
    reminderEnabled: Boolean,
    onReminderEnabledChange: (Boolean) -> Unit,
    reminderHour24: Int,
    reminderMinute: Int,
    onTimeChange: (hour24: Int, minute: Int, formatted12H: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }

    // Convert 24-hour hour to 12-hour values for display and manipulation
    val isPm = reminderHour24 >= 12
    val hour12 = when {
        reminderHour24 == 0 -> 12
        reminderHour24 > 12 -> reminderHour24 - 12
        else -> reminderHour24
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("reminder_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with Toggle Switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onReminderEnabledChange(!reminderEnabled) }
                    .testTag("reminder_toggle_row")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (reminderEnabled) BentoPurpleContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                    ) {
                        Icon(
                            imageVector = if (reminderEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (reminderEnabled) BentoPurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Daily Reminder",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (reminderEnabled) {
                                HabitEntity.format12Hour(reminderHour24, reminderMinute)
                            } else {
                                "Get notified on scheduled days"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (reminderEnabled) BentoPurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = onReminderEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BentoPurplePrimary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("reminder_switch")
                )
            }

            // Expanded Time Selector Section
            AnimatedVisibility(
                visible = reminderEnabled,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    // Bento 12-Hour Time Display Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                            .testTag("reminder_time_display_box")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = BentoPurplePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Reminder Time (12-Hour)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TextButton(
                                    onClick = { showTimePickerDialog = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("open_time_picker_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = BentoPurplePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Pick Time",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = BentoPurplePrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Large 12-Hour Display & Steppers
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Hour Block
                                TimeDigitStepper(
                                    label = "HOUR",
                                    value = String.format(Locale.getDefault(), "%02d", hour12),
                                    onIncrement = {
                                        val newHour12 = if (hour12 == 12) 1 else hour12 + 1
                                        val (newHour24, m) = HabitEntity.to24Hour(newHour12, reminderMinute, isPm)
                                        onTimeChange(newHour24, m, HabitEntity.format12Hour(newHour24, m))
                                    },
                                    onDecrement = {
                                        val newHour12 = if (hour12 == 1) 12 else hour12 - 1
                                        val (newHour24, m) = HabitEntity.to24Hour(newHour12, reminderMinute, isPm)
                                        onTimeChange(newHour24, m, HabitEntity.format12Hour(newHour24, m))
                                    },
                                    testTagPrefix = "hour"
                                )

                                Text(
                                    text = ":",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 14.dp)
                                )

                                // Minute Block
                                TimeDigitStepper(
                                    label = "MINUTE",
                                    value = String.format(Locale.getDefault(), "%02d", reminderMinute),
                                    onIncrement = {
                                        val newMin = (reminderMinute + 5) % 60
                                        val (newHour24, _) = HabitEntity.to24Hour(hour12, newMin, isPm)
                                        onTimeChange(newHour24, newMin, HabitEntity.format12Hour(newHour24, newMin))
                                    },
                                    onDecrement = {
                                        val newMin = if (reminderMinute - 5 < 0) 55 else reminderMinute - 5
                                        val (newHour24, _) = HabitEntity.to24Hour(hour12, newMin, isPm)
                                        onTimeChange(newHour24, newMin, HabitEntity.format12Hour(newHour24, newMin))
                                    },
                                    testTagPrefix = "minute"
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // AM / PM Clearly Visible Segmented Selector
                                AmPmSelector(
                                    isPm = isPm,
                                    onSelectAm = {
                                        if (isPm) {
                                            val (newHour24, m) = HabitEntity.to24Hour(hour12, reminderMinute, false)
                                            onTimeChange(newHour24, m, HabitEntity.format12Hour(newHour24, m))
                                        }
                                    },
                                    onSelectPm = {
                                        if (!isPm) {
                                            val (newHour24, m) = HabitEntity.to24Hour(hour12, reminderMinute, true)
                                            onTimeChange(newHour24, m, HabitEntity.format12Hour(newHour24, m))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal 12-Hour Time Picker Dialog
    if (showTimePickerDialog) {
        TimePicker12HourDialog(
            initialHour12 = hour12,
            initialMinute = reminderMinute,
            initialIsPm = isPm,
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { h12, min, pm ->
                showTimePickerDialog = false
                val (h24, m) = HabitEntity.to24Hour(h12, min, pm)
                onTimeChange(h24, m, HabitEntity.format12Hour(h24, m))
            }
        )
    }
}

@Composable
private fun TimeDigitStepper(
    label: String,
    value: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Increment Button
        IconButton(
            onClick = onIncrement,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .testTag("${testTagPrefix}_increment")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase $label",
                tint = BentoPurplePrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Digit Display Box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 58.dp, height = 48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .testTag("${testTagPrefix}_display")
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Decrement Button
        IconButton(
            onClick = onDecrement,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .testTag("${testTagPrefix}_decrement")
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease $label",
                tint = BentoPurplePrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AmPmSelector(
    isPm: Boolean,
    onSelectAm: () -> Unit,
    onSelectPm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 12.dp)
    ) {
        // AM Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 54.dp, height = 36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (!isPm) BentoPurplePrimary else MaterialTheme.colorScheme.surface
                )
                .border(
                    width = 1.dp,
                    color = if (!isPm) BentoPurplePrimary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onSelectAm
                )
                .testTag("selector_am_button")
        ) {
            Text(
                text = "AM",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = if (!isPm) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // PM Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 54.dp, height = 36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isPm) BentoPurplePrimary else MaterialTheme.colorScheme.surface
                )
                .border(
                    width = 1.dp,
                    color = if (isPm) BentoPurplePrimary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onSelectPm
                )
                .testTag("selector_pm_button")
        ) {
            Text(
                text = "PM",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = if (isPm) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TimePicker12HourDialog(
    initialHour12: Int,
    initialMinute: Int,
    initialIsPm: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (hour12: Int, minute: Int, isPm: Boolean) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour12) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    var selectedIsPm by remember { mutableStateOf(initialIsPm) }

    val hours = (1..12).toList()
    val minutes = listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Reminder Time",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Live Preview in Dialog
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BentoPurpleContainer)
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = BentoPurplePrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BentoPurplePrimary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (selectedIsPm) "PM" else "AM",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AM / PM Toggle Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!selectedIsPm) BentoPurplePrimary else Color.Transparent)
                            .clickable { selectedIsPm = false }
                            .padding(vertical = 8.dp)
                            .testTag("dialog_am_button")
                    ) {
                        Text(
                            text = "AM (Morning)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (!selectedIsPm) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedIsPm) BentoPurplePrimary else Color.Transparent)
                            .clickable { selectedIsPm = true }
                            .padding(vertical = 8.dp)
                            .testTag("dialog_pm_button")
                    ) {
                        Text(
                            text = "PM (Evening)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedIsPm) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hour Grid Selector
                Text(
                    text = "Select Hour",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(84.dp)
                ) {
                    items(hours) { h ->
                        val isSelected = h == selectedHour
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BentoPurplePrimary else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    if (isSelected) BentoPurplePrimary else MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedHour = h }
                                .testTag("dialog_hour_$h")
                        ) {
                            Text(
                                text = "$h",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Minute Grid Selector
                Text(
                    text = "Select Minute",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(84.dp)
                ) {
                    items(minutes) { m ->
                        val isSelected = m == selectedMinute
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BentoPurplePrimary else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    if (isSelected) BentoPurplePrimary else MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedMinute = m }
                                .testTag("dialog_minute_$m")
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%02d", m),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedHour, selectedMinute, selectedIsPm) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPurplePrimary),
                modifier = Modifier.testTag("dialog_confirm_button")
            ) {
                Text("Confirm", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("dialog_cancel_button")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
