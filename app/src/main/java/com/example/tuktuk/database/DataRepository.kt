package com.example.tuktuk.database

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.example.tuktuk.network.Api
import com.example.tuktuk.network.request.*
import com.example.tuktuk.network.responses.UserResponse
import com.example.tuktuk.util.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.Headers.Builder
import okhttp3.Headers.Companion.headersOf
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.http.Header
import java.io.File
import java.net.ConnectException
import java.net.URI

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

    fun checkExistUserByUsername(username: String?): Boolean = cache.checkExistUserByUsername(username)
    fun checkExistUserByEmail(email: String?): Boolean = cache.checkExistUserByEmail(email)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun userRegister(
        action: String,
        email: String,
        username: String,
        password: String): Int {
        Api.setAuth(false)
        try {
            val response = api.userRegister(UserRequest(action, Api.api_key, username, email, password))
            Log.i("INFO", response.toString())
            Log.i("INFO", "----------------------------")
            Log.i("INFO", username)
            Log.i("INFO", password)
//            Log.i("INFO", response.body()!!.toString())
//            Log.i("INFO", response.body()!!.email)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("INFO", "INSERT TO DATABASE")
                    cache.insertUser(gson.fromJson(response.body()!!))
                    Log.i("INFO", "# USER")
                    Log.i("INFO", cache.getUser(response.body()!!.id).toString())
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
            Log.i("ERROR", "Problem.")
            ex.printStackTrace()
        }
        return responseCode
    }

    suspend fun userLogin(
        action: String,
        username: String,
        password: String): Int {
        Api.setAuth(false)
        try {
            val response = api.userLogin(LoginRequest(action, Api.api_key, username, password))
            Log.i("INFO", "LOGIN")
            Log.i("INFO", response.toString())
            Log.i("INFO", username)
            Log.i("INFO", password)
            if(response.isSuccessful) {
                return if(response.body() == null) {
                    Log.i("INFO", "BAD CREDENTIALS")
                    401
                } else {
                    SharedPreferences.token = response.body()!!.token
                    SharedPreferences.email = response.body()!!.email
                    SharedPreferences.refresh = response.body()!!.refresh
                    SharedPreferences.profile = response.body()!!.token
                    SharedPreferences.username = response.body()!!.username
                    SharedPreferences.isLogin = true
//                    Log.i("INFO", cache.getUser(response.body()!!.id).toString())
//                    cache.updateUser(gson.fromJson(response.body()!!))
                    Log.i("INFO", cache.getUser(response.body()!!.id).toString())
                    response.code()
                }
            }
            Log.i("INFO", "Stala sa velmi skareda vec")
            return responseCode
        } catch (ex: Exception){
            Log.i("INFO", ex.toString())
            return 401
        }

    }

    suspend fun userInfo(
        action: String,
        token: String): Int {
        val response = api.userInfo(InfoRequest(action, Api.api_key, token))
        Log.i("INFO", "INFO REFRESH")
        Log.i("INFO", response.body()!!.refresh)
//        val response2 = api.tokenRefresh(RefreshRequest(action, Api.api_key, response.body()!!.refresh))
//        Log.i("INFO", response2.code().toString())
//        Log.i("INFO", api.tokenRefresh(RefreshRequest("refreshToken", Api.api_key, response.body()!!.refresh)).body().toString())
        if(response.isSuccessful) {
            Log.i("INFO", response.body().toString())
            SharedPreferences.token = response.body()!!.token
            SharedPreferences.email = response.body()!!.email
            SharedPreferences.refresh = response.body()!!.refresh
            SharedPreferences.profile = response.body()!!.token
            SharedPreferences.username = response.body()!!.username

            return response.code()
        }
        else {
            Log.i("INFO", "USER INFO ZLY TOKEN")
            Log.i("INFO", response.code().toString())
            return response.code()
        }
//        Log.i("INFO", "Nieco nepravdepoodbne")
//        return responseCode

    }

    suspend fun tokenRefresh(
        action: String,
        refresh: String): Int {
        Log.i("INFO", "-----")
        Log.i("INFO", refresh)
        Log.i("INFO", "-----")
        val response = api.tokenRefresh(RefreshRequest(action, Api.api_key, refresh))
        if (response.isSuccessful) {
            Log.i("INFO", "Odhlasenie sa podarilo")
            SharedPreferences.token = ""
            SharedPreferences.email = ""
            SharedPreferences.refresh = ""
            SharedPreferences.profile = ""
            SharedPreferences.username = ""
            SharedPreferences.isLogin = false
            return response.code()
        }

        Log.i("INFO", "Odhlasenie sa nepodarilo")
        Log.i("INFO", response.code().toString())
        return responseCode
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun userNameExists(
        action: String,
        username: String): Int {
        try {
            val response = api.userNameExists(UserExistsRequest(action, Api.api_key, username))
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

        } catch (ex:  ConnectException){
            ex.printStackTrace()
        }
        return responseCode
    }


    suspend fun uploadImage(
        fileUri: URI,
        token: String,
        context: Context
    ): Int {

            val file = File(fileUri.getPath())
            Log.i("INFO", fileUri.toString())

        // val inputStream = context.getContentResolver().openInputStream(Uri.fromFile(file))
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file.readBytes())
            //body = MultipartBody.Part.createFormData("image", file.name, requestFile)


        var myHeaders = Headers

        myHeaders.headersOf("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"")
        myHeaders.headersOf("Content-Type: image/jpeg")

            val mpart = MultipartBody.Builder().addPart(null, requestFile)

        val map = mapOf("Content-Disposition" to "form-data; name=\"image\"; filename=\"image.jpg\"",
            "Content-Type" to "image/jpeg")
        println(map) // {1=x, 2=y, -1=zz}


            val outputJson: String = Gson().toJson(ImageRequest(Api.api_key, token))
            val body2 = RequestBody.create("application/json".toMediaTypeOrNull(), outputJson)


            val response = api.uploadImage(map, body2, mpart)
             if (response.isSuccessful) {
                 Log.i("INFO", "Odhlasenie sa podarilo")
                 SharedPreferences.token = ""
                 SharedPreferences.email = ""
                 SharedPreferences.refresh = ""
                 SharedPreferences.profile = ""
                 SharedPreferences.username = ""
                 SharedPreferences.isLogin = false
                 return response.code()
             }

             Log.i("INFO", "Odhlasenie sa nepodarilo")
             Log.i("INFO", response.code().toString())
        return responseCode
    }
}

private fun Gson.fromJson(body: UserResponse): User {
    return User(body.id, body.username, body.email, body.token, body.refresh, body.profile)
}

