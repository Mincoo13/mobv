package com.example.tuktuk.network.request

import com.google.gson.annotations.SerializedName

data class AllVideosRequest (
    @SerializedName("action") val action: String?,
    @SerializedName("apikey") val apikey: String?,
    @SerializedName("token") val token: String?
)