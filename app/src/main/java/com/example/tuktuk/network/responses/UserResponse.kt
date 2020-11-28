package com.example.tuktuk.network.responses

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val token: String,
    val refresh: String,
    val profile: String
)