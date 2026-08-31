package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val icon: String = "📚",
    val colorHex: String = "#7C3AED",
    val frequencyType: String = "EVERY_DAY", // EVERY_DAY or SPECIFIC_DAYS
    val daysOfWeek: String = "1,2,3,4,5,6,7", // 1=Mon, 2=Tue, ..., 7=Sun
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 8, // 0..23 24-hour format
    val reminderMinute: Int = 0, // 0..59
    val reminderTimeFormatted: String = "08:00 AM", // 12-hour display string
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun format12Hour(hour24: Int, minute: Int): String {
            val isPm = hour24 >= 12
            val hour12 = when {
                hour24 == 0 -> 12
                hour24 > 12 -> hour24 - 12
                else -> hour24
            }
            val amPm = if (isPm) "PM" else "AM"
            return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
        }

        fun to24Hour(hour12: Int, minute: Int, isPm: Boolean): Pair<Int, Int> {
            val clampedHour12 = hour12.coerceIn(1, 12)
            val clampedMinute = minute.coerceIn(0, 59)
            val hour24 = when {
                clampedHour12 == 12 && !isPm -> 0
                clampedHour12 == 12 && isPm -> 12
                isPm -> clampedHour12 + 12
                else -> clampedHour12
            }
            return Pair(hour24, clampedMinute)
        }
    }
}

