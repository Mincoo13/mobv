//package com.example.tuktuk.database
//
//import android.content.Context
//import androidx.lifecycle.ViewModelProvider
//import com.example.tuktuk.network.Api
//import com.example.tuktuk.database.UserRepository
//
///**
// * Class that handles object creation.
// * Like this, objects can be passed as parameters in the constructors and then replaced for
// * testing, where needed.
// */
//object Injection {
//
//    private fun provideCache(context: Context): LocalCache {
//        val database = AppDatabase.getInstance(context)
//        return LocalCache(database.appDatabaseDao)
//    }
//
//    fun provideDataRepository(context: Context): UserRepository {
//        return UserRepository().getInstance(Api.create(context), provideCache(context))
//    }
//
//    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
//        return ViewModelFactory(
//            provideDataRepository(
//                context
//            )
//        )
//    }
//}