package com.example.tuktuk.database

import androidx.lifecycle.LiveData

class LocalCache(private val dao: AppDatabaseDao){
    suspend fun insertUser(user: User) {
        dao.insertUser(user)
    }

    fun getUser(id: String): User? {
        return dao.getUser(id)
    }

    fun getAllUsers(): LiveData<List<User>> {
        return dao.getAllUsers()
    }

    suspend fun updateUser(user: User) {
        dao.updateUser(user)
    }
}