package com.example.tuktuk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tuktuk.database.DataRepository
import com.example.tuktuk.registration.LoginViewModel

class LoginViewModelFactory(
    private val repository: DataRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
