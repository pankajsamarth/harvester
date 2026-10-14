package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface HarvestJobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: HarvestJob): Long

    @Update
    suspend fun updateJob(job: HarvestJob): Int

    @Delete
    suspend fun deleteJob(job: HarvestJob): Int

    @Query("SELECT * FROM harvest_jobs ORDER BY startTime DESC")
    fun getAllJobs(): Flow<List<HarvestJob>>

    @Query("SELECT * FROM harvest_jobs WHERE isActive = 1 LIMIT 1")
    fun getActiveJob(): Flow<HarvestJob?>

    @Query("SELECT * FROM harvest_jobs WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveJobDirect(): HarvestJob?

    @Query("SELECT * FROM harvest_jobs WHERE id = :id")
    fun getJobById(id: Int): Flow<HarvestJob?>

    @Query("SELECT * FROM harvest_jobs WHERE id = :id")
    suspend fun getJobByIdDirect(id: Int): HarvestJob?
}
