package com.example.tuktuk.network

import com.example.tuktuk.network.request.*
import com.example.tuktuk.network.responses.ExistsResponse
import com.example.tuktuk.network.responses.UserResponse
import com.example.tuktuk.util.AuthInterceptor
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Api {

    companion object {
        const val api_key: String = "uX9yA9jR8hZ6wE0mT5rZ3kA4kA6zC5"
        private const val BASE_URL = "http://api.mcomputing.eu/mobv/"

        var useAuth: Boolean = false

        fun setAuth(value: Boolean) {
            useAuth = value
        }

        fun create(): Api {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create()

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(AuthInterceptor())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
//                .addConverterFacory(MoshiConverterFactory.create(moshi))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(Api::class.java)
        }
    }

    @Headers(
        "Accept: application/json",
        "Cache-Control: no-cache",
        "Content-Type: application/json"
    )
    @POST("service.php")
    suspend fun userRegister(@Body body: UserRequest): Response<UserResponse>

    @Headers(
        "Accept: application/json",
        "Cache-Control: no-cache",
        "Content-Type: application/json"
    )
    @POST("service.php")
    suspend fun userExists(@Body body: UserExistsRequest): Response<ExistsResponse>

    @Headers("Accept: application/json", "Cache-Control: no-cache", "Content-Type: application/json")
    @POST("service.php")
    suspend fun userNameExists(@Body body: UserExistsRequest) : Response<ExistsResponse>

    @Headers("Accept: application/json", "Cache-Control: no-cache", "Content-Type: application/json")
    @POST("service.php")
    suspend fun userLogin(@Body body: LoginRequest): Response<UserResponse>

    @Headers(
        "Accept: application/json",
        "Cache-Control: no-cache",
        "Content-Type: application/json"
    )
    @POST("service.php")
    suspend fun userInfo(@Body body: InfoRequest): Response<UserResponse>

    @Headers(
        "Accept: application/json",
        "Cache-Control: no-cache",
        "Content-Type: application/json"
    )
    @POST("service.php")
    suspend fun tokenRefresh(@Body body: RefreshRequest): Response<UserResponse>

    @Headers("Content-Type: multipart/from-data; boundary=boundary")
    @Multipart
    @POST("upload.php")
    suspend fun uploadImage(
        @HeaderMap headers: Map<String, String>,
        @Part check: RequestBody,
        @Part imageFile: MultipartBody.Builder
    ): Response<UserResponse>

}