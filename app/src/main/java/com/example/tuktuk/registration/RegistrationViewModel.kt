package com.example.tuktuk.registration

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.tuktuk.database.AppDatabaseDao
import com.example.tuktuk.database.User
import com.example.tuktuk.database.UserDatabase
import com.example.tuktuk.database.UserDatabaseDao
import com.example.tuktuk.network.MarsUser
import kotlinx.coroutines.launch


class RegistrationViewModel(
    val database: AppDatabaseDao,
    application: Application) : AndroidViewModel(application) {

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
        database.insertUser(user)
    }

    fun registerUser(

    )

}