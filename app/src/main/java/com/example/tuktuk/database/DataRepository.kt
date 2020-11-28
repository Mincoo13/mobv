package com.example.tuktuk.database

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.tuktuk.network.Api
import com.example.tuktuk.network.request.UserRequest
import java.net.ConnectException

class DataRepository(
    private val cache: LocalCache,
    private val api: Api
) {

    lateinit var uid: String

    companion object {
        @Volatile
        private var INSTANCE: DataRepository ?= null

        fun getInstance(cache: LocalCache, api: Api): DataRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataRepository(cache, api).also { INSTANCE = it }
            }
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun userRegister(
        action: String,
        email: String,
        username: String,
        password: String): Int {
        val responseCode = 500
        Api.setAuthentication(false)
        try {
            val response = api.userRegister(action, Api.api_key, username, email, password)
            Log.i("INFO", response.toString())
            if (response.isSuccessful) {
//                response.body()?.let {
//                    return cache.insertUser(gson.fromJson(response.toString()))
//                }
                Log.i("INFO", response.toString())
                return response.code()
            }

        } catch (ex: ConnectException){
            Log.i("ERROR", "Problem s pripojenim k internetu.")
            ex.printStackTrace()
        }
        return responseCode
    }
}