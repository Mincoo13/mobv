package com.example.tuktuk.network.request

import com.google.gson.annotations.SerializedName

data class VideoRequest (
    @SerializedName("apikey") val apikey: String?,
    @SerializedName("token") val token: String?
)