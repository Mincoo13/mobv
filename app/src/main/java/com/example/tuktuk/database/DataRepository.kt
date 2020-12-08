package com.example.tuktuk.database

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.tuktuk.network.Api
import com.example.tuktuk.network.request.*
import com.example.tuktuk.network.responses.UserResponse
import com.example.tuktuk.network.responses.VideosResponse
import com.example.tuktuk.util.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.Headers.Companion.headersOf
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
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
            if (response.isSuccessful) {
                response.body()?.let {
                    cache.insertUser(gson.fromJson(response.body()!!))
                }
                return response.code()
            }

        } catch (ex: ConnectException){
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
            if (response.isSuccessful) {
                if (response.body()!!.exists) {
                    return 409
                }
                else {
                    return response.code()
                }
            }
            return responseCode
        } catch (ex:  ConnectException){
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
            if(response.isSuccessful) {
                return if(response.body() == null) {
                    return 401
                } else {
                    SharedPreferences.token = response.body()!!.token
                    SharedPreferences.email = response.body()!!.email
                    SharedPreferences.refresh = response.body()!!.refresh
                    SharedPreferences.profile = response.body()!!.token
                    SharedPreferences.username = response.body()!!.username
                    SharedPreferences.isLogin = true
                    response.code()
                }
            }
            return responseCode
        } catch (ex: Exception){
            return 401
        }

    }

    suspend fun passwordChange(
        action: String,
        token: String,
        oldPassword: String,
        newPassword: String): Int {
        try {
            val response = api.passwordChange(PasswordChangeRequest(action, Api.api_key, token, oldPassword, newPassword))
            if(response.isSuccessful) {
                return if(response.body() == null) {
                    return 401
                } else {
                    SharedPreferences.token = response.body()!!.token
                    SharedPreferences.refresh = response.body()!!.refresh
                    SharedPreferences.isLogin = true
                    response.code()
                }
            }
            return response.code()
        } catch (ex: Exception){
            return 401
        }

    }

    suspend fun userInfo(
        action: String,
        token: String): Int {
        val response = api.userInfo(InfoRequest(action, Api.api_key, token))
        if(response.isSuccessful) {
            SharedPreferences.token = response.body()!!.token
            SharedPreferences.email = response.body()!!.email
            SharedPreferences.refresh = response.body()!!.refresh
            SharedPreferences.profile = response.body()!!.token
            SharedPreferences.username = response.body()!!.username
            SharedPreferences.image = response.body()!!.profile
            SharedPreferences.isLogin = true
            return response.code()
        }
        else {
            SharedPreferences.token = ""
            SharedPreferences.email = ""
            SharedPreferences.refresh = ""
            SharedPreferences.profile = ""
            SharedPreferences.username = ""
            SharedPreferences.image = ""
            SharedPreferences.isLogin = false
            return response.code()
        }

    }

    suspend fun tokenRefresh(
        action: String,
        refresh: String,
        intent: String): Int {
        val response = api.tokenRefresh(RefreshRequest(action, Api.api_key, refresh))
        if (response.isSuccessful) {
            if (intent == "logout") {
                SharedPreferences.token = ""
                SharedPreferences.email = ""
                SharedPreferences.refresh = ""
                SharedPreferences.profile = ""
                SharedPreferences.username = ""
                SharedPreferences.isLogin = false
                return response.code()
            }
            else if (intent == "password") {
                SharedPreferences.token = response.body()!!.token
                SharedPreferences.refresh = response.body()!!.refresh
                SharedPreferences.isLogin = true
                return response.code()
            }

        }
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
                    return 409
                }
                else {
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
        fileUri: File,
        token: String,
        context: Context
    ): Int {
        val file = File(fileUri.getPath())
        val outputJson: String = Gson().toJson(ImageRequest(Api.api_key, token))
        val data = RequestBody.create("application/json".toMediaTypeOrNull(), outputJson)
        val dataPart = MultipartBody.Part.create(
            headersOf(
                "Content-Disposition",
                "form-data; name=\"data\""
            ),
            data
        )

        val image = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.create(
            headersOf(
                "Content-Disposition",
                "form-data; name=\"image\"; filename=\"" + file.name + "\""
            ),
            image
        )
        val response = api.uploadImage(imagePart, dataPart)
        if (response.isSuccessful) {
            return response.code()
        }

        return response.code()
    }

    suspend fun removeImage(
        action: String,
        token: String): Int {
        try {
            val response = api.removeImage(RemoveImageRequest(action, Api.api_key, token))
            if(response.isSuccessful) {
                when (response.code()) {
                    200 -> {
                        SharedPreferences.image = ""
                        return response.code()
                    }
                    else -> {
                        return response.code()
                    }
                }
            }
            return responseCode
        } catch (ex: Exception){
            Log.i("INFO", ex.toString())
            return responseCode
        }

    }

    suspend fun uploadVideo(
        fileUri: File,
        token: String,
        context: Context
    ): Int {

        val file = File(fileUri.path)

        val outputJson: String = Gson().toJson(VideoRequest(Api.api_key, token))
        val data = RequestBody.create("application/json".toMediaTypeOrNull(), outputJson)
        val dataPart = MultipartBody.Part.create(
            headersOf(
                "Content-Disposition",
                "form-data; name=\"data\""
            ),
            data
        )

        val video = RequestBody.create("video/mp4".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.create(
            headersOf(
                "Content-Disposition",
                "form-data; name=\"video\"; filename=\"" + file.name + "\""
            ),
            video
        )
        val response = api.uploadVideo(imagePart, dataPart)

        if (response.isSuccessful) {
            return response.code()
        }

        return response.code()
    }

    suspend fun removeVideo(
        video_id: Int): Int {
        try {
            val response = api.removeVideo(RemoveVideoRequest("deletePost", Api.api_key, SharedPreferences.token, video_id))
            if(response.isSuccessful) {
                when (response.code()) {
                    200 -> {
                        return response.code()
                    }
                    else -> {
                        return response.code()
                    }
                }
            }
            return responseCode
        } catch (ex: Exception){
            return responseCode
        }

    }

    suspend fun getVideos(): List<VideosResponse>? {
        val response = api.getVideos(AllVideosRequest("posts", Api.api_key, SharedPreferences.token))
        if(response.isSuccessful) {
            return response.body()
        }

        return ArrayList()
    }
}

private fun Gson.fromJson(body: UserResponse): User {
    return User(body.id, body.username, body.email, body.token, body.refresh, body.profile)
}

