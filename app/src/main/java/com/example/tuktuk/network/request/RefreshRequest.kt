package com.example.tuktuk.network.request

import com.google.gson.annotations.SerializedName

data class RefreshRequest (
    @SerializedName("action") val action: String?,
    @SerializedName("apikey") val apikey: String?,
    @SerializedName("refreshToken") val refreshToken: String?
)