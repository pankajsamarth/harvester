package com.example.myapplication.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object TimeUtils {

    /**
     * Calculates duration in hours rounded to 2 decimal places.
     * Example: 90 mins -> 1.50 hours, 45 mins -> 0.75 hours.
     */
    fun calculateHours(startTime: Long, endTime: Long): Double {
        if (endTime <= startTime) return 0.0
        val diffMillis = endTime - startTime
        val hours = diffMillis / (1000.0 * 60.0 * 60.0)
        return (hours * 100.0).roundToInt() / 100.0
    }

    /**
     * Formats milliseconds into HH:MM:SS for live timer display.
     */
    fun formatElapsedTime(elapsedMillis: Long): String {
        val totalSeconds = maxOf(0L, elapsedMillis / 1000)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * Formats duration into human readable text, e.g., "1h 30m"
     */
    fun formatDurationText(startTime: Long, endTime: Long?): String {
        if (endTime == null || endTime <= startTime) return "0h 0m"
        val totalMinutes = (endTime - startTime) / (1000 * 60)
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }

    /**
     * Formats epoch timestamp into date string, e.g., "May 20, 2025"
     */
    fun formatDateOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats epoch timestamp into time string, e.g., "09:30 AM"
     */
    fun formatTimeOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats epoch timestamp into date and time string, e.g., "May 20, 2025, 09:30 AM"
     */
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Formats currency amounts cleanly.
     */
    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        return format.format(amount)
    }
}
