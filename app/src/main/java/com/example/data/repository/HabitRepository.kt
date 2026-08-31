package com.example.data.repository

import android.content.Context
import com.example.data.local.PreferencesManager
import com.example.data.local.ThemeMode
import com.example.data.local.dao.CompletionDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.model.UserProfile
import com.example.data.notification.HabitReminderManager
import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val context: Context,
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao,
    private val preferencesManager: PreferencesManager
) {
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allCompletions: Flow<List<HabitCompletionEntity>> = completionDao.getAllCompletions()
    val themeMode: Flow<ThemeMode> = preferencesManager.themeMode
    val userProfile: Flow<UserProfile> = preferencesManager.userProfile

    suspend fun saveUserProfile(name: String, photoPath: String?) {
        preferencesManager.saveUserProfile(name, photoPath)
    }

    suspend fun getHabitById(id: Long): HabitEntity? = habitDao.getHabitById(id)

    fun getHabitFlowById(id: Long): Flow<HabitEntity?> = habitDao.getHabitFlowById(id)

    fun getCompletionsByHabit(habitId: Long): Flow<List<HabitCompletionEntity>> =
        completionDao.getCompletionsByHabit(habitId)

    suspend fun insertHabit(habit: HabitEntity): Long {
        val id = habitDao.insertHabit(habit)
        val inserted = habit.copy(id = id)
        if (inserted.reminderEnabled) {
            HabitReminderManager.scheduleReminder(context, inserted)
        }
        return id
    }

    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
        if (habit.reminderEnabled) {
            HabitReminderManager.scheduleReminder(context, habit)
        } else {
            HabitReminderManager.cancelReminder(context, habit.id)
        }
    }

    suspend fun deleteHabit(habitId: Long) {
        HabitReminderManager.cancelReminder(context, habitId)
        habitDao.deleteHabitById(habitId)
        completionDao.deleteCompletionsByHabit(habitId)
    }

    suspend fun toggleHabitCompletion(habitId: Long, date: String): Boolean {
        val existing = completionDao.getCompletion(habitId, date)
        return if (existing != null && existing.completed) {
            completionDao.deleteCompletion(habitId, date)
            false
        } else {
            completionDao.insertOrUpdate(
                HabitCompletionEntity(
                    habitId = habitId,
                    date = date,
                    completed = true,
                    completedAt = System.currentTimeMillis()
                )
            )
            true
        }
    }

    suspend fun setHabitCompletion(habitId: Long, date: String, completed: Boolean) {
        if (completed) {
            completionDao.insertOrUpdate(
                HabitCompletionEntity(
                    habitId = habitId,
                    date = date,
                    completed = true,
                    completedAt = System.currentTimeMillis()
                )
            )
        } else {
            completionDao.deleteCompletion(habitId, date)
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        preferencesManager.setThemeMode(mode)
    }

    suspend fun resetAllData() {
        // Cancel all habit reminders
        val habits = habitDao.getAllHabits()
        habitDao.deleteAllHabits()
        completionDao.deleteAllCompletions()
        preferencesManager.clearPreferences()
    }
}

