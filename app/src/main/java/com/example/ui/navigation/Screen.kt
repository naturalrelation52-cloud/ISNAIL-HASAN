package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
    object AddHabit : Screen("add_habit")
    object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Long) = "edit_habit/$habitId"
    }
    object HabitDetails : Screen("habit_details/{habitId}") {
        fun createRoute(habitId: Long) = "habit_details/$habitId"
    }
    object Profile : Screen("profile")
}
