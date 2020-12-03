package com.example.tuktuk.network.request

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("action") val action: String?,
    @SerializedName("apikey") val apikey: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
)