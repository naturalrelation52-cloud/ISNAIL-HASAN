package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber

@Composable
fun StreakBadge(
    streakCount: Int,
    modifier: Modifier = Modifier,
    isBest: Boolean = false
) {
    if (streakCount <= 0 && !isBest) return

    val bgColor = if (isBest) {
        Color(0xFFFEF3C7) // soft amber tint
    } else {
        Color(0xFFFFEDD5) // soft orange tint
    }

    val textColor = if (isBest) {
        Color(0xFF92400E)
    } else {
        Color(0xFFC2410C)
    }

    val iconText = if (isBest) "🏆" else "🔥"
    val labelText = if (isBest) "Best: $streakCount" else "$streakCount d"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = iconText,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 11.sp
            )
        )
    }
}
