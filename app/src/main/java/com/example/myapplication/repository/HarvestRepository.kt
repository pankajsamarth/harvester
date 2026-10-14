package com.example.myapplication.repository

import com.example.myapplication.data.HarvestJob
import com.example.myapplication.data.HarvestJobDao
import com.example.myapplication.util.TimeUtils
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

class HarvestRepository(private val dao: HarvestJobDao) {

    val allJobs: Flow<List<HarvestJob>> = dao.getAllJobs()
    val activeJob: Flow<HarvestJob?> = dao.getActiveJob()

    fun getJobById(id: Int): Flow<HarvestJob?> = dao.getJobById(id)

    suspend fun startNewJob(farmerName: String, cropType: String, hourlyRate: Double): Long {
        val now = System.currentTimeMillis()
        val formattedDate = TimeUtils.formatDateOnly(now)
        val newJob = HarvestJob(
            farmerName = farmerName.trim(),
            cropType = cropType.ifBlank { "Rice" }.trim(),
            date = formattedDate,
            startTime = now,
            endTime = null,
            hourlyRate = hourlyRate,
            totalHours = 0.0,
            totalAmount = 0.0,
            paymentStatus = HarvestJob.STATUS_PENDING,
            isActive = true
        )
        return dao.insertJob(newJob)
    }

    suspend fun stopActiveJob(jobId: Int, stopTime: Long = System.currentTimeMillis()) {
        val currentJob = dao.getJobByIdDirect(jobId) ?: dao.getActiveJobDirect() ?: return
        val calculatedHours = TimeUtils.calculateHours(currentJob.startTime, stopTime)
        val calculatedAmount = (calculatedHours * currentJob.hourlyRate * 100.0).roundToInt() / 100.0

        val updatedJob = currentJob.copy(
            endTime = stopTime,
            totalHours = calculatedHours,
            totalAmount = calculatedAmount,
            isActive = false
        )
        dao.updateJob(updatedJob)
    }

    suspend fun togglePaymentStatus(jobId: Int) {
        val job = dao.getJobByIdDirect(jobId) ?: return
        val newStatus = if (job.paymentStatus == HarvestJob.STATUS_PAID) {
            HarvestJob.STATUS_PENDING
        } else {
            HarvestJob.STATUS_PAID
        }
        dao.updateJob(job.copy(paymentStatus = newStatus))
    }

    suspend fun deleteJob(job: HarvestJob) {
        dao.deleteJob(job)
    }
}
