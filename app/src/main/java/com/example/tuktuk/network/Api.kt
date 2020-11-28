package com.example.tuktuk.network

import com.example.tuktuk.network.responses.UserResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface Api {

    companion object {
        const val api_key : String = "uX9yA9jR8hZ6wE0mT5rZ3kA4kA6zC5"
        private const val BASE_URL = "http://api.mcomputing.eu/mobv/"

        fun setAuthentication(value: Boolean){
            Api.useAuthentication = value
        }

        var useAuthentication : Boolean = false

        fun create(): Api {

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
//
//            val retrofit = Retrofit.Builder()
//                .addConverterFactory(MoshiConverterFactory.create(moshi))
//                .baseUrl(BASE_URL)
//                .build()
//
//            retrofit.create(Api::class.java)
//
//            val interceptor = HttpLoggingInterceptor()
//            interceptor.level = HttpLoggingInterceptor.Level.BODY
//
//
//            val httpBuilder = OkHttpClient.Builder()
//            httpBuilder
//                .connectTimeout(15, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS)
//                .addInterceptor(interceptor)  /// show all JSON in logCat
//            val client = httpBuilder.build()


//            val client = OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .addInterceptor(AuthInterceptor())
//                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
//                .addConverterFactory(GsonConverterFactory.create())
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