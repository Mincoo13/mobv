package com.example.tuktuk.network.request

import com.google.gson.annotations.SerializedName

data class PasswordChangeRequest (
    @SerializedName("action") val action: String?,
    @SerializedName("apikey") val apikey: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("oldpassword") val oldPassword: String?,
    @SerializedName("newpassword") val newPassword: String?
)