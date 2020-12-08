package com.example.tuktuk.registration

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.tuktuk.database.DataRepository
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class RegistrationViewModel(
    private val repository: DataRepository
) : ViewModel() {

    val name: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val passwordCheck: MutableLiveData<String> = MutableLiveData()
    val age: MutableLiveData<String> = MutableLiveData("dd.mm.yyyy")

//    ################
//    RESPONSES
    @RequiresApi(Build.VERSION_CODES.O)
    val ageResponse: LiveData<String> = Transformations.map(age) {
        validateAge(it.toString())
    }

    val emailResponse: LiveData<String> = Transformations.map(email) {
        validateEmail(it)
    }

    val nameResponse: LiveData<String> = Transformations.map(name) {
        validateName(it)
    }

    private val _passwordResponse: MutableLiveData<String> = MutableLiveData()
    val passwordResponse: LiveData<String>
        get() = _passwordResponse

    private val _passwordCheckResponse: MutableLiveData<String> = MutableLiveData()
    val passwordCheckResponse: LiveData<String>
        get() = _passwordCheckResponse

//    ################
//    CHECK VALIDITY

    private val isPasswordValid = MutableLiveData<Boolean>()
    private val isNameValid = MutableLiveData<Boolean>()
    private val isAgeValid = MutableLiveData<Boolean>()
    private val isEmailValid = MutableLiveData<Boolean>()
    private val _isValid: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        fun update() {
            val pswd = isPasswordValid.value ?: return
            val email = isEmailValid.value ?: return
            val name = isNameValid.value ?: return
            val age = isAgeValid.value ?: return

            value = pswd && email && name && age
        }

        addSource(isPasswordValid) {update()}
        addSource(isEmailValid) {update()}
        addSource(isAgeValid) {update()}
        addSource(isNameValid) {update()}

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
        isPasswordValid.postValue(false)
        isNameValid.postValue(false)
        isEmailValid.postValue(false)
        isAgeValid.postValue(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateAge(age: String): String {
        isAgeValid.postValue(false)
        if (age != "dd.mm.yyyy") {
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH)
            val date = LocalDate.parse(age, formatter)
            val diff = ChronoUnit.DAYS.between(today, date)
            if (diff <= -(17*365)) {
                isAgeValid.postValue(true)
                return ""
            }
            else {
                return "Nemate dostacujuci vek"
            }
        }
        return ""
    }

    fun validateEmail(mail: String): String {
        isEmailValid.postValue(false)
        if (mail.isNotEmpty()) {
            val responseCheck = runBlocking { checkExistUserByEmail(mail)}
            if (!checkEmailValid(mail)) {
                return "E-mailova adresa nema spravny tvar"
            }
            else if (responseCheck) {
                return "E-mailova adresa uz je pouzita"
            }
            else {
                isEmailValid.postValue(true)
                return ""
            }
        }
        return ""
    }

    fun validateName(username: String): String {
        isNameValid.postValue(false)
        if (username.isNotEmpty()) {
            val responseCheck = runBlocking { checkExistUserByUsername(username)}
            if (responseCheck) {
                return "Meno je uz pouzite"
            }
            else {
                isNameValid.postValue(true)
                return ""
            }
        }
        return ""
    }

    fun validatePassword() {
        isPasswordValid.postValue(false)
        password.value?.let {
            if (it.isNotEmpty()) {
                if (passwordCheck.value.toString() != it) {
                    _passwordCheckResponse.postValue("Hesla sa nezhoduju")
                }
                else {
                    isPasswordValid.postValue(true)
                    _passwordCheckResponse.postValue(" ")
                }
            }
            else {
                _passwordCheckResponse.postValue("Musite vyplnit heslo")
            }
        }
    }

    suspend fun api(action: String, name: String, email: String, password: String): Int{
        return repository.userRegister(action, name, email, password)
    }

    suspend fun userExists(action: String, name: String): Int {
        return repository.userExists(action, name)
    }

    fun checkEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private suspend fun checkExistUserByEmail(email: String): Boolean {
        return viewModelScope.async { repository.checkExistUserByEmail(email) }.await()
    }

    private suspend fun checkExistUserByUsername(username: String): Boolean {
        return viewModelScope.async { repository.checkExistUserByUsername(username) }.await()
    }

}