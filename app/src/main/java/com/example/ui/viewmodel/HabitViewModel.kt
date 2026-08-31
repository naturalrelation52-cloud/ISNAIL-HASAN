package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ThemeMode
import com.example.data.local.entity.HabitEntity
import com.example.data.model.AnalyticsSummary
import com.example.data.model.HabitCalendarDay
import com.example.data.model.HabitDetailsData
import com.example.data.model.HabitPerformanceItem
import com.example.data.model.HabitWithStats
import com.example.data.model.UserProfile
import com.example.data.repository.HabitRepository
import com.example.data.util.HabitCalculationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

data class HomeUiState(
    val habits: List<HabitWithStats> = emptyList(),
    val todayCompletedCount: Int = 0,
    val todayTotalCount: Int = 0,
    val todayProgressPercentage: Int = 0,
    val isLoading: Boolean = false,
    val userGreeting: String = "Good Day 👋"
)

class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth

    // Combine habits & completions to produce home UI state
    val homeUiState: StateFlow<HomeUiState> = combine(
        repository.allHabits,
        repository.allCompletions
    ) { habits, completions ->
        val today = HabitCalculationUtils.getTodayDate()
        val todayStr = HabitCalculationUtils.getTodayDateString()

        val habitStatsList = habits.map { habit ->
            val habitCompletions = completions.filter { it.habitId == habit.id }
            HabitCalculationUtils.calculateHabitStats(habit, habitCompletions, today)
        }

        val todayScheduledHabits = habitStatsList.filter { it.isScheduledToday }
        val todayCompletedCount = todayScheduledHabits.count { it.isCompletedToday }
        val todayTotalCount = todayScheduledHabits.size

        val percentage = if (todayTotalCount > 0) {
            ((todayCompletedCount.toDouble() / todayTotalCount) * 100).roundToInt()
        } else if (habits.isNotEmpty()) {
            0
        } else {
            0
        }

        // Determine greeting based on current local hour
        val hour = java.time.LocalTime.now().hour
        val greeting = when (hour) {
            in 5..11 -> "Good Morning 👋"
            in 12..16 -> "Good Afternoon ☀️"
            in 17..21 -> "Good Evening 🌙"
            else -> "Good Night 🌙"
        }

        HomeUiState(
            habits = habitStatsList,
            todayCompletedCount = todayCompletedCount,
            todayTotalCount = todayTotalCount,
            todayProgressPercentage = percentage,
            isLoading = false,
            userGreeting = greeting
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    // Analytics summary
    val analyticsSummary: StateFlow<AnalyticsSummary> = combine(
        repository.allHabits,
        repository.allCompletions
    ) { habits, completions ->
        if (habits.isEmpty()) {
            return@combine AnalyticsSummary()
        }

        val today = HabitCalculationUtils.getTodayDate()
        val todayStr = HabitCalculationUtils.getTodayDateString()

        val habitStatsList = habits.map { habit ->
            val habitCompletions = completions.filter { it.habitId == habit.id }
            HabitCalculationUtils.calculateHabitStats(habit, habitCompletions, today)
        }

        // 1. Today completion
        val todayScheduled = habitStatsList.filter { it.isScheduledToday }
        val todayCompleted = todayScheduled.count { it.isCompletedToday }
        val todayTotal = todayScheduled.size
        val todayPct = if (todayTotal > 0) {
            ((todayCompleted.toDouble() / todayTotal) * 100).roundToInt()
        } else 0

        // 2. This Week completion (Monday to today)
        val monday = today.with(java.time.DayOfWeek.MONDAY)
        var weekScheduledCount = 0
        var weekCompletedCount = 0
        val completedDatesByHabit = completions.filter { it.completed }.groupBy { it.habitId }

        var d = monday
        while (!d.isAfter(today)) {
            val dStr = d.format(HabitCalculationUtils.DATE_FORMATTER)
            for (habit in habits) {
                if (HabitCalculationUtils.isHabitScheduledOn(habit, d)) {
                    weekScheduledCount++
                    val habitCompletions = completedDatesByHabit[habit.id] ?: emptyList()
                    if (habitCompletions.any { it.date == dStr }) {
                        weekCompletedCount++
                    }
                }
            }
            d = d.plusDays(1)
        }
        val thisWeekPct = if (weekScheduledCount > 0) {
            ((weekCompletedCount.toDouble() / weekScheduledCount) * 100).roundToInt()
        } else 0

        // 3. This Month completion (1st to today)
        val firstOfMonth = today.withDayOfMonth(1)
        var monthScheduledCount = 0
        var monthCompletedCount = 0

        d = firstOfMonth
        while (!d.isAfter(today)) {
            val dStr = d.format(HabitCalculationUtils.DATE_FORMATTER)
            for (habit in habits) {
                if (HabitCalculationUtils.isHabitScheduledOn(habit, d)) {
                    monthScheduledCount++
                    val habitCompletions = completedDatesByHabit[habit.id] ?: emptyList()
                    if (habitCompletions.any { it.date == dStr }) {
                        monthCompletedCount++
                    }
                }
            }
            d = d.plusDays(1)
        }
        val thisMonthPct = if (monthScheduledCount > 0) {
            ((monthCompletedCount.toDouble() / monthScheduledCount) * 100).roundToInt()
        } else 0

        // 4. Overall completion rate (across past 30 days)
        val total30DaysScheduled = habitStatsList.sumOf { it.totalDaysCount30Days }
        val total30DaysCompleted = habitStatsList.sumOf { it.completedDaysCount30Days }
        val overallPct = if (total30DaysScheduled > 0) {
            ((total30DaysCompleted.toDouble() / total30DaysScheduled) * 100).roundToInt()
        } else if (todayTotal > 0) {
            todayPct
        } else 0

        // 5. Weekly 7-day Bar Chart
        val weeklyChart = HabitCalculationUtils.calculateWeeklyChart(habits, completions, today)

        // 6. Habit performance items
        val habitPerformances = habitStatsList.map { stats ->
            HabitPerformanceItem(
                habit = stats.habit,
                currentStreak = stats.currentStreak,
                bestStreak = stats.bestStreak,
                completionPercentage = stats.completionRate30Days,
                completedDaysCount = stats.completedDaysCount30Days,
                totalDaysCount = stats.totalDaysCount30Days
            )
        }

        AnalyticsSummary(
            overallPercentage = overallPct,
            todayPercentage = todayPct,
            thisWeekPercentage = thisWeekPct,
            thisMonthPercentage = thisMonthPct,
            todayCompletedCount = todayCompleted,
            todayTotalCount = todayTotal,
            weeklyChart = weeklyChart,
            habitPerformances = habitPerformances
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsSummary()
    )

    fun toggleHabitCompletion(habitId: Long, date: String = HabitCalculationUtils.getTodayDateString()) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, date)
        }
    }

    fun setHabitCompletion(habitId: Long, date: String, completed: Boolean) {
        viewModelScope.launch {
            repository.setHabitCompletion(habitId, date, completed)
        }
    }

    fun createHabit(
        name: String,
        description: String = "",
        icon: String = "📚",
        colorHex: String = "#7C3AED",
        frequencyType: String = "EVERY_DAY",
        daysOfWeek: String = "1,2,3,4,5,6,7",
        reminderEnabled: Boolean = false,
        reminderHour: Int = 8,
        reminderMinute: Int = 0,
        reminderTimeFormatted: String = "08:00 AM",
        onCreated: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val newHabit = HabitEntity(
                name = name.trim(),
                description = description.trim(),
                icon = icon,
                colorHex = colorHex,
                frequencyType = frequencyType,
                daysOfWeek = daysOfWeek,
                reminderEnabled = reminderEnabled,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute,
                reminderTimeFormatted = reminderTimeFormatted,
                createdAt = System.currentTimeMillis()
            )
            val id = repository.insertHabit(newHabit)
            onCreated(id)
        }
    }

    fun updateHabit(
        habit: HabitEntity,
        onUpdated: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.updateHabit(habit)
            onUpdated()
        }
    }

    fun deleteHabit(habitId: Long, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            onDeleted()
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun saveUserProfile(name: String, photoPath: String?, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveUserProfile(name.trim().ifEmpty { "Jane Doe" }, photoPath)
            onSaved()
        }
    }

    fun resetAllData(onReset: () -> Unit = {}) {
        viewModelScope.launch {
            repository.resetAllData()
            onReset()
        }
    }

    fun changeSelectedMonth(delta: Int) {
        _selectedMonth.value = _selectedMonth.value.plusMonths(delta.toLong())
    }

    fun resetSelectedMonthToCurrent() {
        _selectedMonth.value = YearMonth.now()
    }
}

class HabitViewModelFactory(
    private val repository: HabitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
