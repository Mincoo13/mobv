package com.example.tuktuk.database

class LocalCache(private val dao: AppDatabaseDao){
    suspend fun insertUser(user: User) {
        dao.insertUser(user)
    }
}