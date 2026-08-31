package com.example.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.entity.HabitEntity
import java.time.LocalDate

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(HabitReminderManager.EXTRA_HABIT_ID, -1L)
        if (habitId == -1L) return

        val habitName = intent.getStringExtra(HabitReminderManager.EXTRA_HABIT_NAME) ?: "Habit"
        val habitIcon = intent.getStringExtra(HabitReminderManager.EXTRA_HABIT_ICON) ?: "✨"
        val frequencyType = intent.getStringExtra(HabitReminderManager.EXTRA_FREQUENCY_TYPE) ?: "EVERY_DAY"
        val daysOfWeek = intent.getStringExtra(HabitReminderManager.EXTRA_DAYS_OF_WEEK) ?: "1,2,3,4,5,6,7"
        val reminderHour = intent.getIntExtra(HabitReminderManager.EXTRA_REMINDER_HOUR, 8)
        val reminderMinute = intent.getIntExtra(HabitReminderManager.EXTRA_REMINDER_MINUTE, 0)

        // Check if today matches the scheduled frequency
        val today = LocalDate.now()
        val dayNumber = today.dayOfWeek.value.toString() // 1=Mon .. 7=Sun
        val isScheduledToday = if (frequencyType == "EVERY_DAY") {
            true
        } else {
            val selectedDays = daysOfWeek.split(",").map { it.trim() }
            selectedDays.contains(dayNumber)
        }

        if (isScheduledToday) {
            HabitReminderManager.showHabitNotification(
                context = context,
                habitId = habitId,
                habitName = habitName,
                habitIcon = habitIcon
            )
        }

        // Schedule the next alarm for tomorrow/subsequent days
        val mockHabit = HabitEntity(
            id = habitId,
            name = habitName,
            icon = habitIcon,
            frequencyType = frequencyType,
            daysOfWeek = daysOfWeek,
            reminderEnabled = true,
            reminderHour = reminderHour,
            reminderMinute = reminderMinute
        )
        HabitReminderManager.scheduleReminder(context, mockHabit)
    }
}
