package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.HarvestJob

@Composable
fun StatusBadge(
    status: String,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when {
        isActive -> Triple(
            Color(0xFF0066FF).copy(alpha = 0.15f),
            Color(0xFF0055D4),
            "IN PROGRESS"
        )
        status == HarvestJob.STATUS_PAID -> Triple(
            Color(0xFF2E7D32).copy(alpha = 0.15f),
            Color(0xFF1B5E20),
            "PAID"
        )
        else -> Triple(
            Color(0xFFD32F2F).copy(alpha = 0.15f),
            Color(0xFFC62828),
            "PENDING"
        )
    }

    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
