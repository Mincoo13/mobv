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

    private val _oldPasswordResponse: MutableLiveData<String> = MutableLiveData()
    val oldPasswordResponse: LiveData<String>
        get() = _oldPasswordResponse

    private val _newPasswordCheckResponse: MutableLiveData<String> = MutableLiveData()
    val newPasswordCheckResponse: LiveData<String>
        get() = _newPasswordCheckResponse

    private val isNewPasswordValid = MutableLiveData<Boolean>()
    private val isOldPasswordValid = MutableLiveData<Boolean>()
    private val _isValid: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        fun update() {
            val pswd = isNewPasswordValid.value ?: return
            val oldPswd = isOldPasswordValid.value ?: return

            value = pswd && oldPswd
        }

        addSource(isNewPasswordValid) {update()}
        addSource(isOldPasswordValid) {update()}

        update()
    }

    val isValid: LiveData<Boolean>
        get() = _isValid

    init {
        setValidationIncomplete()
        setAllInputAsIncomplete()
    }

    private fun setValidationIncomplete() {
        _isValid.postValue(false)
    }

    private fun setAllInputAsIncomplete() {
        isNewPasswordValid.postValue(false)
        isOldPasswordValid.postValue(false)
    }

    fun validateNewPassword() {
        isNewPasswordValid.postValue(false)

        newPassword.value?.let {
            if (it.isNotEmpty()) {
                if (newPasswordCheck.value.toString() != it) {
                    _newPasswordCheckResponse.postValue("Heslá sa nezhodujú")
                    Log.i("INFO", "Hesla sa nezhoduju")
                }
                else if(it == oldPassword.value.toString()) {
                    _newPasswordCheckResponse.postValue("Nové heslo nesmie byť rovnaké ako staré heslo")
                    Log.i("INFO", "Nové heslo nesmie byť rovnaké ako staré heslo")
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

    fun validateOldPassword() {
        isOldPasswordValid.postValue(false)
        oldPassword.value?.let {
            if (it.isNotEmpty()) {
                if (it == newPassword.value.toString()) {
                    _newPasswordCheckResponse.postValue("Nové heslo nesmie byť rovnaké ako staré heslo")
                }
                else {
                    isOldPasswordValid.postValue(true)
                    _oldPasswordResponse.postValue(" ")
                }
            }
            else {
                _oldPasswordResponse.postValue("Musíte vyplniť aktuálne heslo.")
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