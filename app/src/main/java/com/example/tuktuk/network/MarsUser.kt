package com.example.tuktuk.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MarsUser(
    val id: String,
    val email: String,
    val refresh: String,
    val token: String,
    val profile: String) : Parcelable {

}
