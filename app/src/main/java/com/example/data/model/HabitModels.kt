package com.example.data.model

import com.example.data.local.entity.HabitEntity

data class HabitWithStats(
    val habit: HabitEntity,
    val isCompletedToday: Boolean,
    val isScheduledToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionRate30Days: Int,
    val completedDaysCount30Days: Int,
    val totalDaysCount30Days: Int,
    val totalCompletedAllTime: Int
)

data class DayChartItem(
    val dateString: String,
    val dayLabel: String, // "M", "T", "W", etc.
    val isToday: Boolean,
    val completedCount: Int,
    val totalCount: Int,
    val completionRatio: Float // 0.0f to 1.0f
)

data class HabitPerformanceItem(
    val habit: HabitEntity,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionPercentage: Int,
    val completedDaysCount: Int,
    val totalDaysCount: Int
)

data class AnalyticsSummary(
    val overallPercentage: Int = 0,
    val todayPercentage: Int = 0,
    val thisWeekPercentage: Int = 0,
    val thisMonthPercentage: Int = 0,
    val todayCompletedCount: Int = 0,
    val todayTotalCount: Int = 0,
    val weeklyChart: List<DayChartItem> = emptyList(),
    val habitPerformances: List<HabitPerformanceItem> = emptyList()
)

data class HabitCalendarDay(
    val dateString: String,
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean,
    val isScheduled: Boolean,
    val isCompleted: Boolean
)

data class HabitDetailsData(
    val habit: HabitEntity,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionRate: Int,
    val totalCompleted: Int,
    val completionsMap: Map<String, Boolean>
)

data class UserProfile(
    val name: String = "Jane Doe",
    val photoPath: String? = null
) {
    val initials: String
        get() {
            val trimmed = name.trim()
            if (trimmed.isEmpty()) return "JD"
            val parts = trimmed.split("\\s+".toRegex()).filter { it.isNotEmpty() }
            return when {
                parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
                parts.size == 1 && parts[0].length >= 2 -> "${parts[0][0].uppercaseChar()}${parts[0][1].uppercaseChar()}"
                parts.size == 1 -> "${parts[0][0].uppercaseChar()}"
                else -> "JD"
            }
        }
}

