package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.repository.HarvestRepository

class HarvesterApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { HarvestRepository(database.harvestJobDao()) }
}
