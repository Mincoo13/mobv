package com.example.tuktuk.util

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.tuktuk.database.AppDatabase
import com.example.tuktuk.database.DataRepository
import com.example.tuktuk.database.LocalCache
import com.example.tuktuk.network.Api
import com.example.tuktuk.registration.RegistrationViewModelFactory

object Injection {
    private fun provideCache(context: Context): LocalCache {
        val database = AppDatabase.getInstance(context) // get instance of database. It is created if null
        return LocalCache(database.appDatabaseDao()) // use database to create LocalCache
    }

    // Create repository with database cache and api
    private fun provideDataRepository(context: Context): DataRepository {
        return DataRepository.getInstance(provideCache(context), Api.create())
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return RegistrationViewModelFactory(
            provideDataRepository(context)
        )
    }
}