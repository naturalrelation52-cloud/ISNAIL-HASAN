package com.example.data.util

import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.model.DayChartItem
import com.example.data.model.HabitCalendarDay
import com.example.data.model.HabitPerformanceItem
import com.example.data.model.HabitWithStats
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

object HabitCalculationUtils {

    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getTodayDate(): LocalDate = LocalDate.now()

    fun getTodayDateString(): String = getTodayDate().format(DATE_FORMATTER)

    fun parseDate(dateStr: String): LocalDate? {
        return try {
            LocalDate.parse(dateStr, DATE_FORMATTER)
        } catch (e: Exception) {
            null
        }
    }

    fun isHabitScheduledOn(habit: HabitEntity, date: LocalDate): Boolean {
        if (habit.frequencyType == "EVERY_DAY") return true
        val dayNumber = date.dayOfWeek.value.toString() // 1=Mon .. 7=Sun
        val selectedDays = habit.daysOfWeek.split(",").map { it.trim() }
        return selectedDays.contains(dayNumber)
    }

    fun calculateCurrentStreak(
        habit: HabitEntity,
        completions: Set<String>,
        today: LocalDate = getTodayDate()
    ): Int {
        var streak = 0
        var checkDate = today
        val isTodayScheduled = isHabitScheduledOn(habit, today)
        val isTodayCompleted = completions.contains(today.format(DATE_FORMATTER))

        if (isTodayScheduled) {
            if (isTodayCompleted) {
                streak++
                checkDate = today.minusDays(1)
            } else {
                // If today is scheduled but not completed yet, streak is intact if previous scheduled day was completed
                checkDate = today.minusDays(1)
            }
        } else {
            // Today is not scheduled, streak continues from last scheduled day
            checkDate = today.minusDays(1)
        }

        // Walk backwards up to 365 days or habit creation date
        val createdDate = try {
            LocalDate.ofEpochDay(habit.createdAt / (24 * 60 * 60 * 1000L))
        } catch (e: Exception) {
            today.minusYears(1)
        }

        var daysChecked = 0
        while (daysChecked < 365 && !checkDate.isBefore(createdDate.minusDays(1))) {
            if (isHabitScheduledOn(habit, checkDate)) {
                val dateStr = checkDate.format(DATE_FORMATTER)
                if (completions.contains(dateStr)) {
                    streak++
                } else {
                    // Missed a scheduled day - streak ends
                    break
                }
            }
            checkDate = checkDate.minusDays(1)
            daysChecked++
        }

        return streak
    }

    fun calculateBestStreak(
        habit: HabitEntity,
        completions: Set<String>,
        today: LocalDate = getTodayDate()
    ): Int {
        val createdDate = try {
            val d = LocalDate.ofEpochDay(habit.createdAt / (24 * 60 * 60 * 1000L))
            if (d.isAfter(today)) today else d
        } catch (e: Exception) {
            today.minusMonths(3)
        }

        var bestStreak = 0
        var currentStreak = 0
        var checkDate = createdDate

        while (!checkDate.isAfter(today)) {
            if (isHabitScheduledOn(habit, checkDate)) {
                val dateStr = checkDate.format(DATE_FORMATTER)
                val isCompleted = completions.contains(dateStr)

                if (isCompleted) {
                    currentStreak++
                    if (currentStreak > bestStreak) {
                        bestStreak = currentStreak
                    }
                } else if (!checkDate.isEqual(today)) {
                    // If missed a day in the past, streak resets
                    currentStreak = 0
                }
            }
            checkDate = checkDate.plusDays(1)
        }

        // Also compare with current streak calculation
        val curr = calculateCurrentStreak(habit, completions, today)
        return maxOf(bestStreak, curr)
    }

    fun calculateHabitStats(
        habit: HabitEntity,
        allHabitCompletions: List<HabitCompletionEntity>,
        today: LocalDate = getTodayDate()
    ): HabitWithStats {
        val completedDatesSet = allHabitCompletions
            .filter { it.completed }
            .map { it.date }
            .toSet()

        val todayStr = today.format(DATE_FORMATTER)
        val isCompletedToday = completedDatesSet.contains(todayStr)
        val isScheduledToday = isHabitScheduledOn(habit, today)

        val currentStreak = calculateCurrentStreak(habit, completedDatesSet, today)
        val bestStreak = calculateBestStreak(habit, completedDatesSet, today)

        // 30-day window stats
        var completed30Days = 0
        var scheduled30Days = 0

        for (i in 0 until 30) {
            val d = today.minusDays(i.toLong())
            if (isHabitScheduledOn(habit, d)) {
                scheduled30Days++
                if (completedDatesSet.contains(d.format(DATE_FORMATTER))) {
                    completed30Days++
                }
            }
        }

        val completionRate30Days = if (scheduled30Days > 0) {
            ((completed30Days.toDouble() / scheduled30Days) * 100).roundToInt()
        } else {
            0
        }

        return HabitWithStats(
            habit = habit,
            isCompletedToday = isCompletedToday,
            isScheduledToday = isScheduledToday,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            completionRate30Days = completionRate30Days,
            completedDaysCount30Days = completed30Days,
            totalDaysCount30Days = scheduled30Days,
            totalCompletedAllTime = completedDatesSet.size
        )
    }

    fun calculateWeeklyChart(
        habits: List<HabitEntity>,
        allCompletions: List<HabitCompletionEntity>,
        today: LocalDate = getTodayDate()
    ): List<DayChartItem> {
        val completionsByDate = allCompletions
            .filter { it.completed }
            .groupBy { it.date }

        // Start from Monday of current week
        val monday = today.with(DayOfWeek.MONDAY)
        val chartItems = mutableListOf<DayChartItem>()

        for (i in 0..6) {
            val date = monday.plusDays(i.toLong())
            val dateStr = date.format(DATE_FORMATTER)
            val dayLabel = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())

            // Scheduled habits for this day
            val scheduledHabits = habits.filter { isHabitScheduledOn(it, date) }
            val completedList = completionsByDate[dateStr]?.map { it.habitId }?.toSet() ?: emptySet()
            val completedCount = scheduledHabits.count { completedList.contains(it.id) }
            val totalCount = scheduledHabits.size

            val ratio = if (totalCount > 0) {
                (completedCount.toFloat() / totalCount).coerceIn(0f, 1f)
            } else {
                0f
            }

            chartItems.add(
                DayChartItem(
                    dateString = dateStr,
                    dayLabel = dayLabel,
                    isToday = date.isEqual(today),
                    completedCount = completedCount,
                    totalCount = totalCount,
                    completionRatio = ratio
                )
            )
        }

        return chartItems
    }

    fun calculateMonthCalendarDays(
        habit: HabitEntity,
        completionsMap: Map<String, Boolean>,
        yearMonth: YearMonth,
        today: LocalDate = getTodayDate()
    ): List<HabitCalendarDay> {
        val firstOfMonth = yearMonth.atDay(1)
        val daysInMonth = yearMonth.lengthOfMonth()

        // Find which day of week the month starts on (1=Mon, 7=Sun)
        val startDayOfWeek = firstOfMonth.dayOfWeek.value // 1 to 7

        val calendarDays = mutableListOf<HabitCalendarDay>()

        // Previous month padding days
        val prevMonth = yearMonth.minusMonths(1)
        val daysInPrevMonth = prevMonth.lengthOfMonth()
        val prevMonthDaysCount = startDayOfWeek - 1

        for (i in (daysInPrevMonth - prevMonthDaysCount + 1)..daysInPrevMonth) {
            val date = prevMonth.atDay(i)
            val dateStr = date.format(DATE_FORMATTER)
            val isScheduled = isHabitScheduledOn(habit, date)
            val isCompleted = completionsMap[dateStr] == true

            calendarDays.add(
                HabitCalendarDay(
                    dateString = dateStr,
                    dayOfMonth = i,
                    isCurrentMonth = false,
                    isToday = date.isEqual(today),
                    isFuture = date.isAfter(today),
                    isScheduled = isScheduled,
                    isCompleted = isCompleted
                )
            )
        }

        // Current month days
        for (day in 1..daysInMonth) {
            val date = yearMonth.atDay(day)
            val dateStr = date.format(DATE_FORMATTER)
            val isScheduled = isHabitScheduledOn(habit, date)
            val isCompleted = completionsMap[dateStr] == true

            calendarDays.add(
                HabitCalendarDay(
                    dateString = dateStr,
                    dayOfMonth = day,
                    isCurrentMonth = true,
                    isToday = date.isEqual(today),
                    isFuture = date.isAfter(today),
                    isScheduled = isScheduled,
                    isCompleted = isCompleted
                )
            )
        }

        // Next month trailing days to complete 35 or 42 grid slots
        val totalCells = if (calendarDays.size > 35) 42 else 35
        val remaining = totalCells - calendarDays.size
        val nextMonth = yearMonth.plusMonths(1)

        for (day in 1..remaining) {
            val date = nextMonth.atDay(day)
            val dateStr = date.format(DATE_FORMATTER)
            val isScheduled = isHabitScheduledOn(habit, date)
            val isCompleted = completionsMap[dateStr] == true

            calendarDays.add(
                HabitCalendarDay(
                    dateString = dateStr,
                    dayOfMonth = day,
                    isCurrentMonth = false,
                    isToday = date.isEqual(today),
                    isFuture = date.isAfter(today),
                    isScheduled = isScheduled,
                    isCompleted = isCompleted
                )
            )
        }

        return calendarDays
    }
}
