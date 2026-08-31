package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.entity.HabitEntity
import com.example.data.util.HabitCalculationUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Habit Tracker", appName)
  }

  @Test
  fun `calculate current streak correctly for completed days`() {
    val today = LocalDate.of(2026, 8, 31)
    val habit = HabitEntity(
      id = 1L,
      name = "Reading",
      createdAt = today.minusDays(10).toEpochDay() * 24 * 60 * 60 * 1000L
    )
    val completions = setOf(
      "2026-08-31",
      "2026-08-30",
      "2026-08-29"
    )

    val streak = HabitCalculationUtils.calculateCurrentStreak(habit, completions, today)
    assertEquals(3, streak)
  }

  @Test
  fun `calculate best streak accurately across gaps`() {
    val today = LocalDate.of(2026, 8, 31)
    val habit = HabitEntity(
      id = 1L,
      name = "Exercise",
      createdAt = today.minusDays(20).toEpochDay() * 24 * 60 * 60 * 1000L
    )
    val completions = setOf(
      "2026-08-15",
      "2026-08-16",
      "2026-08-17",
      "2026-08-18", // 4 days streak
      "2026-08-30",
      "2026-08-31"  // 2 days streak
    )

    val bestStreak = HabitCalculationUtils.calculateBestStreak(habit, completions, today)
    assertEquals(4, bestStreak)
  }
}
