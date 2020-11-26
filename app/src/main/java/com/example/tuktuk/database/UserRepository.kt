package com.example.tuktuk.database

import androidx.annotation.WorkerThread
import com.example.tuktuk.network.MarsApiService
import com.example.tuktuk.network.MarsUser
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val api: MarsApiService,
    private val userDatabaseDao: UserDatabaseDao) {

//    val allUsers: Flow<List<User>> = userDatabaseDao.getAllUsers()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun userRegister(
        apikey: String,
        email: String,
        username: String,
        password: String): List<MarsUser> {
        return api.userRegister(apikey, username, email, password)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) {
        userDatabaseDao.insert(user)
    }
}