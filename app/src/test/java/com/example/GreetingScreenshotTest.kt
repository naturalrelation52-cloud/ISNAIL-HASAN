package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.entity.HabitEntity
import com.example.data.model.HabitWithStats
import com.example.ui.components.HabitCard
import com.example.ui.theme.HabitTrackerTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun habit_card_screenshot() {
    val habit = HabitEntity(
      id = 1L,
      name = "Morning Reading",
      description = "Read for 20 minutes",
      icon = "📚",
      colorHex = "#4F46E5"
    )
    val habitStats = HabitWithStats(
      habit = habit,
      isCompletedToday = true,
      isScheduledToday = true,
      currentStreak = 5,
      bestStreak = 12,
      completionRate30Days = 85,
      completedDaysCount30Days = 25,
      totalDaysCount30Days = 30,
      totalCompletedAllTime = 42
    )

    composeTestRule.setContent {
      HabitTrackerTheme {
        HabitCard(
          habitWithStats = habitStats,
          onToggleCompletion = {},
          onClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
