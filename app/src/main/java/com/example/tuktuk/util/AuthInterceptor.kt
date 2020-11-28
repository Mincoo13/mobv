package com.example.tuktuk.util

import android.util.Log
import com.example.tuktuk.registration.RegistrationFragment
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        Log.i("TAG_API", "sending request")

        val request = chain.request()
            .newBuilder()
            .addHeader("Cache-Control","no-cache")
            .addHeader("Accept","application/json")
            .addHeader("Content-Type","application/json")

        return chain.proceed(request.build())
    }
}