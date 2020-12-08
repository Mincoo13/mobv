package com.example.tuktuk.login

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.tuktuk.database.*


class LoginViewModel(
    private val repository: DataRepository
) : ViewModel() {

    val name = ObservableField<String>("")
    val password = ObservableField<String>("")

    private val _inputResponse: MutableLiveData<String> = MutableLiveData()
    val inputResponse: LiveData<String>
        get() = _inputResponse

    init {
    }


    suspend fun userLogin(action: String, name: String, password: String): Int{
        val response = repository.userLogin(action, name, password)
        if (response == 401) {
            _inputResponse.postValue("Zadané nesprávne údaje.")
        }
        return response
    }

    suspend fun userExists(action: String, name: String): Int {
        return repository.userExists(action, name)
    }

}