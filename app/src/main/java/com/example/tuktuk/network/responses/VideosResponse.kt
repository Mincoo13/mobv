package com.example.tuktuk.network.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class VideosResponse (
        val postid: String,
        val created: String,
//        val videourl: String,
        val username: String,
        val profile: String,
        @Json(name = "videourl") var videourl: String)