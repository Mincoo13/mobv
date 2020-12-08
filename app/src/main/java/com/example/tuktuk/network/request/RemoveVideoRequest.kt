package com.example.tuktuk.network.request

import com.google.gson.annotations.SerializedName

data class RemoveVideoRequest (
    @SerializedName("action") val action: String,
    @SerializedName("apikey") val apikey: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("id") val id: Int
)