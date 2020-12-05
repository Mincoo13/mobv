package com.example.tuktuk.login

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.tuktuk.database.*
import com.example.tuktuk.network.MarsUser
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repository: DataRepository
) : ViewModel() {

    val name = ObservableField<String>("")
    val password = ObservableField<String>("")

    init {
    }


    suspend fun userLogin(action: String, name: String, password: String): Int{
        return repository.userLogin(action, name, password)
    }

    suspend fun userExists(action: String, name: String): Int {
        return repository.userExists(action, name)
    }

}