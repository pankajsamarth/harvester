package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "harvest_jobs")
data class HarvestJob(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val farmerName: String,
    val cropType: String,
    val date: String,
    val startTime: Long,
    val endTime: Long? = null,
    val hourlyRate: Double,
    val totalHours: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentStatus: String = STATUS_PENDING,
    val isActive: Boolean = true
) {
    companion object {
        const val STATUS_PENDING = "Pending"
        const val STATUS_PAID = "Paid"
    }
}
