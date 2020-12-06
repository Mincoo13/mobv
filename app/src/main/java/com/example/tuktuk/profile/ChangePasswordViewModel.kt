package com.example.tuktuk.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tuktuk.database.DataRepository

class ChangePasswordViewModel(
    private val repository: DataRepository
) : ViewModel() {

    val oldPassword: MutableLiveData<String> = MutableLiveData()
    val newPassword: MutableLiveData<String> = MutableLiveData()
    val newPasswordCheck: MutableLiveData<String> = MutableLiveData()

    private val _passwordResponse: MutableLiveData<String> = MutableLiveData()
    val passwordResponse: LiveData<String>
        get() = _passwordResponse

    private val _newPasswordCheckResponse: MutableLiveData<String> = MutableLiveData()
    val newPasswordCheckResponse: LiveData<String>
        get() = _newPasswordCheckResponse

    private val isNewPasswordValid = MutableLiveData<Boolean>()
    private val _isValid: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        fun update() {
            val pswd = isNewPasswordValid.value ?: return

            value = pswd
        }

        addSource(isNewPasswordValid) {update()}

        update()
    }

    val isValid: LiveData<Boolean>
        get() = _isValid

    init {
        setValidationIncomplete()
        setAllInputAsIncomplete()
    }

    fun setValidationIncomplete() {
        _isValid.postValue(false)
    }

    fun setAllInputAsIncomplete() {
        isNewPasswordValid.postValue(false)
    }

    fun validatePassword() {
        isNewPasswordValid.postValue(false)
        newPassword.value?.let {
            if (it.isNotEmpty()) {
                if (newPasswordCheck.value.toString() != it) {
                    _newPasswordCheckResponse.postValue("Hesla sa nezhoduju")
                    Log.i("INFO", "Hesla sa nezhoduju")
                }
                else {
                    isNewPasswordValid.postValue(true)
                    _newPasswordCheckResponse.postValue(" ")
                }
            }
            else {
                _newPasswordCheckResponse.postValue("Musite vyplnit heslo")
                Log.i("INFO", "Heslo nie je vyplnene")
            }
        }
    }

    suspend fun passwordChange(action: String, token: String, oldPassword: String, newPassword: String): Int {
        val response = repository.passwordChange(action, token, oldPassword, newPassword)
        if (response == 401) {
            _newPasswordCheckResponse.postValue("Aktuálne heslo je nesprávne.")
        }
        return response
    }

    suspend fun userInfo(action: String, token: String): Int {
        return repository.userInfo(action, token)
    }

}