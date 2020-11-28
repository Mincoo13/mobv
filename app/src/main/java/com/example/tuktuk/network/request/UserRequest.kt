package com.example.tuktuk.network.request

data class UserRequest(
    val action: String,
    val apikey: String,
    val email: String,
    val username: String,
    val password: String
)