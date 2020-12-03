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

    fun getUserByUsername(username: String?): LiveData<User?> {
        return dao.getUserByUsername(username)
    }

    fun getUserByMail(email: String?): LiveData<User?> {
        return dao.getUserByMail(email)
    }

    fun checkExistUserByUsername(username: String?): Boolean {
        return dao.checkExistUserByUsername(username)
    }

    fun checkExistUserByEmail(email: String?): Boolean {
        return dao.checkExistUserByEmail(email)
    }

}