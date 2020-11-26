package com.example.tuktuk.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User constructor(
    @PrimaryKey(autoGenerate = false)
    val id: String,

    val username: String,
    val email: String,
    val token: String,
    val refresh: String,
    val profile: String?
)