package com.example.tuktuk.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * from users WHERE id = :id")
    fun getUser(id: String): User?

    @Query("SELECT * from users WHERE username = :username")
    fun getUserByUsername(username: String?): LiveData<User?>

    @Query("SELECT * from users WHERE email = :email")
    fun getUserByMail(email: String?): LiveData<User?>

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT EXISTS (SELECT 1 FROM users WHERE username = :username)")
    fun checkExistUserByUsername(username: String?): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM users WHERE email = :email)")
    fun checkExistUserByEmail(email: String?): Boolean
}