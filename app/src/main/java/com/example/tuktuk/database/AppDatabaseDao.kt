package com.example.tuktuk.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * from users WHERE id = :id")
    fun getUser(id: String): User?

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): LiveData<List<User>>

}