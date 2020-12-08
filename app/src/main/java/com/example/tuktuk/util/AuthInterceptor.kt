package com.example.tuktuk.util

import android.util.Log
import com.example.tuktuk.network.Api.Companion.useAuth
import com.example.tuktuk.registration.RegistrationFragment
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
            .newBuilder()
            .addHeader("Cache-Control","no-cache")
            .addHeader("Accept","application/json")
            .addHeader("Content-Type","application/json")

        return chain.proceed(request.build())
    }
}