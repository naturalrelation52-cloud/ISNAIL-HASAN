package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DayChartItem
import com.example.ui.theme.BentoPurpleContainer
import com.example.ui.theme.BentoPurpleDeep
import com.example.ui.theme.BentoPurplePrimary
import com.example.ui.theme.BentoTeal

@Composable
fun WeeklyBarChart(
    weeklyData: List<DayChartItem>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .testTag("weekly_bar_chart"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        weeklyData.forEach { item ->
            val animatedRatio by animateFloatAsState(
                targetValue = item.completionRatio,
                animationSpec = tween(durationMillis = 600),
                label = "barRatio"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.width(36.dp)
            ) {
                // Percentage or ratio text
                Text(
                    text = if (item.totalCount > 0) "${(item.completionRatio * 100).toInt()}%" else "-",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (item.isToday) BentoPurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Bar Container
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(BentoPurpleContainer.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val barHeightFraction = if (item.totalCount > 0) animatedRatio.coerceAtLeast(0.06f) else 0f
                    if (barHeightFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction = barHeightFraction)
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = if (item.completionRatio >= 1.0f) {
                                            listOf(BentoTeal, BentoTeal.copy(alpha = 0.8f))
                                        } else {
                                            listOf(BentoPurplePrimary, BentoPurpleDeep)
                                        }
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Day Label with indicator for today
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isToday) BentoPurplePrimary else Color.Transparent
                        )
                ) {
                    Text(
                        text = item.dayLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = if (item.isToday) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

