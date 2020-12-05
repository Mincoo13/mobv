package com.example.tuktuk.util

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.tuktuk.database.AppDatabase
import com.example.tuktuk.database.DataRepository
import com.example.tuktuk.database.LocalCache
import com.example.tuktuk.home.HomeViewModelFactory
import com.example.tuktuk.network.Api
import com.example.tuktuk.login.LoginViewModelFactory
import com.example.tuktuk.profile.ChangePasswordViewModelFactory
import com.example.tuktuk.profile.ProfileViewModelFactory
import com.example.tuktuk.registration.RegistrationViewModelFactory

object Injection {
    fun provideCache(context: Context): LocalCache {
        val database = AppDatabase.getInstance(context) // get instance of database. It is created if null
        return LocalCache(database.appDatabaseDao()) // use database to create LocalCache
    }

    // Create repository with database cache and api
    private fun provideDataRepository(context: Context): DataRepository {
        return DataRepository.getInstance(provideCache(context), Api.create())
    }

    fun provideRegistrationViewModelFactory(context: Context): ViewModelProvider.Factory {
        return RegistrationViewModelFactory(
            provideDataRepository(context)
        )
    }

    fun provideLoginViewModelFactory(context: Context): ViewModelProvider.Factory {
        return LoginViewModelFactory(
            provideDataRepository(context)
        )
    }

    fun provideProfileViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ProfileViewModelFactory(
            provideDataRepository(context)
        )
    }

    fun provideHomeViewModelFactory(context: Context): ViewModelProvider.Factory {
        return HomeViewModelFactory(
            provideDataRepository(context)
        )
    }

    fun provideChangePasswordViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ChangePasswordViewModelFactory(
            provideDataRepository(context)
        )
    }
}