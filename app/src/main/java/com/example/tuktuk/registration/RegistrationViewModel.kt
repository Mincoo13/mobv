package com.example.tuktuk.registration

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.tuktuk.database.*
import com.example.tuktuk.network.MarsUser
import kotlinx.coroutines.launch


class RegistrationViewModel(
    val repository: DataRepository
) : ViewModel() {

    val name = ObservableField<String>("")
    val email = ObservableField<String>("")
    val password = ObservableField<String>("")
    val passwordCheck = ObservableField<String>("")
    val age = ObservableField<String>("")
    val messageRegister = ObservableField<String>("")

    init {
    }

    private val _navigateToProfile = MutableLiveData<MarsUser>()

    val navigateToProfile: LiveData<MarsUser>
        get() = _navigateToProfile

    fun doneNavigating() {
        _navigateToProfile.value = null
    }

    private suspend fun insert(user: User) {
//        database.insertUser(user)
    }

//    fun registerUser(
//
//    )
    suspend fun api(action: String, name: String, email: String, password: String): Int{
        return repository.userRegister(action, name, email, password)
    }

}