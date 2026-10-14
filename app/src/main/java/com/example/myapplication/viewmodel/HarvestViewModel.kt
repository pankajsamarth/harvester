package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.HarvestJob
import com.example.myapplication.repository.HarvestRepository
import com.example.myapplication.util.TimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardSummary(
    val totalEarnings: Double = 0.0,
    val pendingPayments: Double = 0.0,
    val totalHoursWorked: Double = 0.0
)

class HarvestViewModel(private val repository: HarvestRepository) : ViewModel() {

    val allJobs: StateFlow<List<HarvestJob>> = repository.allJobs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeJob: StateFlow<HarvestJob?> = repository.activeJob.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val summaryState: StateFlow<DashboardSummary> = allJobs.combine(activeJob) { jobs, _ ->
        val completedJobs = jobs.filter { !it.isActive }
        val totalEarnings = completedJobs.sumOf { it.totalAmount }
        val pendingPayments = completedJobs
            .filter { it.paymentStatus == HarvestJob.STATUS_PENDING }
            .sumOf { it.totalAmount }
        val totalHours = completedJobs.sumOf { it.totalHours }

        DashboardSummary(
            totalEarnings = totalEarnings,
            pendingPayments = pendingPayments,
            totalHoursWorked = totalHours
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardSummary()
    )

    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis: StateFlow<Long> = _elapsedMillis.asStateFlow()

    private val _estimatedCost = MutableStateFlow(0.0)
    val estimatedCost: StateFlow<Double> = _estimatedCost.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Observe active job and maintain live timer ticker
        viewModelScope.launch {
            activeJob.collect { active ->
                timerJob?.cancel()
                if (active != null) {
                    timerJob = viewModelScope.launch {
                        while (true) {
                            val now = System.currentTimeMillis()
                            val elapsed = maxOf(0L, now - active.startTime)
                            _elapsedMillis.value = elapsed
                            val hours = TimeUtils.calculateHours(active.startTime, now)
                            _estimatedCost.value = hours * active.hourlyRate
                            delay(1000)
                        }
                    }
                } else {
                    _elapsedMillis.value = 0L
                    _estimatedCost.value = 0.0
                }
            }
        }
    }

    fun startNewJob(
        farmerName: String,
        cropType: String,
        hourlyRate: Double,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val newId = repository.startNewJob(farmerName, cropType, hourlyRate)
            onSuccess(newId)
        }
    }

    fun stopActiveJob(jobId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.stopActiveJob(jobId)
            onSuccess()
        }
    }

    fun togglePaymentStatus(jobId: Int) {
        viewModelScope.launch {
            repository.togglePaymentStatus(jobId)
        }
    }

    fun deleteJob(job: HarvestJob) {
        viewModelScope.launch {
            repository.deleteJob(job)
        }
    }

    fun getJobById(id: Int): Flow<HarvestJob?> {
        return repository.getJobById(id)
    }
}
