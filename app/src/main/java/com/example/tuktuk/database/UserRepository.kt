package com.example.tuktuk.database

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.tuktuk.network.Api
import com.example.tuktuk.network.MarsUser
import com.example.tuktuk.network.responses.UserResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.net.ConnectException

class UserRepository(
    private val cache: LocalCache,
    private val api: Api,
    private val appDatabaseDao: AppDatabaseDao) {

    val gson = Gson()

//    val allUsers: Flow<List<User>> = userDatabaseDao.getAllUsers()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun userRegister(
        action: String,
        apikey: String,
        email: String,
        username: String,
        password: String) {
        try {
            val response = api.userRegister(action, apikey, username, email, password)
            if (response.isSuccessful) {
//                response.body()?.let {
//                    return cache.insertUser(gson.fromJson(response.toString()))
//                }
                Log.i("INFO", response.toString())
            }

        } catch (ex: ConnectException){
            Log.i("ERROR", "Problem s pripojenim k internetu.")
            return
        }

    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) {
        appDatabaseDao.insertUser(user)
    }
}