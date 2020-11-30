package com.example.tuktuk.database

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.tuktuk.network.Api
import com.example.tuktuk.network.request.UserExistsRequest
import com.example.tuktuk.network.request.UserRequest
import com.example.tuktuk.network.responses.UserResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import java.net.ConnectException

class DataRepository(
    private val cache: LocalCache,
    private val api: Api
) {

    lateinit var uid: String
    private val responseCode = 500

    companion object {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create()
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
        Api.setAuthentication(false)
        try {
            val response = api.userRegister(UserRequest(action, Api.api_key, username, email, password))
            Log.i("INFO", response.toString())
//            Log.i("INFO", response.body()!!.toString())
//            Log.i("INFO", response.body()!!.email)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("INFO", "INSERT TO DATABASE")
                    cache.insertUser(gson.fromJson(response.body()!!))
                    Log.i("INFO", "# USER")
                    cache.getUser(response.body()!!.id)
                    Log.i("INFO", cache.getUser("41").toString())
                }
                return response.code()
            }

        } catch (ex: ConnectException){
            Log.i("ERROR", "Problem s pripojenim k internetu.")
            ex.printStackTrace()
        }
        return responseCode
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun userExists(
        action: String,
        username: String): Int {
        try {
            val response = api.userExists(UserExistsRequest(action, Api.api_key, username))

            Log.i("INFO", response.body()!!.exists.toString())
            if (response.isSuccessful) {
                if (response.body()!!.exists) {
                    Log.i("INFO", "Pouzivatel existuje")
                    return 409
                }
                else {
                    Log.i("INFO", "Pouzivatel neexistuje")
                    return response.code()
                }
            }
            return responseCode
//            if (response.isSuccessful) {
//
//            }
        } catch (ex:  ConnectException){
            Log.i("ERROR", "Problem s pripojenim k internetu.")
            ex.printStackTrace()
        }
        return responseCode
    }
}

private fun Gson.fromJson(body: UserResponse): User {
    return User(body.id, body.username, body.email, body.token, body.refresh, body.profile)
}

