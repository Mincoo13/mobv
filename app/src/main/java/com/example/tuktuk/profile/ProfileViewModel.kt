package com.example.tuktuk.profile

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.tuktuk.database.DataRepository
import com.example.tuktuk.util.SharedPreferences

class ProfileViewModel(
    private val repository: DataRepository
) : ViewModel() {
    val name = ObservableField<String>("")
    val email = ObservableField<String>("")

    init {

    }

    suspend fun userInfo(action: String, token: String): Int {
        return repository.userInfo(action, token)
    }

    suspend fun userLogout(action: String, refresh: String): Int {
        Log.i("INFO", refresh)
        return repository.tokenRefresh(action, refresh)
    }
}