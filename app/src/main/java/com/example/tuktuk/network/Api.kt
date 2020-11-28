package com.example.tuktuk.network

import android.content.Context
import com.example.tuktuk.network.responses.UserResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Api {

    companion object {
        private const val BASE_URL =
            "http://api.mcomputing.eu/mobv/"

        fun create(context: Context): Api {

            val client = OkHttpClient.Builder()
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(Api::class.java)
        }
    }

    @Headers("Accept: application/json", "Cache-Control: no-cache", "Content-Type: application/json")
    @FormUrlEncoded
    @POST("service.php")
    suspend fun userRegister(
        @Field("action") action: String,
        @Field("apikey") apikey: String,
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String
    ) : Response<UserResponse>

    @Headers("Accept: application/json", "Cache-Control: no-cache", "Content-Type: application/json")
    @FormUrlEncoded
    @POST("service.php")
    suspend fun userExists(
        @Field("action") action: String,
        @Field("apikey") apikey: String,
        @Field("username") username: String
    ) : Response<UserResponse>
}