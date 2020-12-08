package com.example.tuktuk.profile

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.tuktuk.R
import com.example.tuktuk.database.LocalCache
import com.example.tuktuk.databinding.FragmentChangePasswordBinding
import com.example.tuktuk.registration.RegistrationViewModel
import com.example.tuktuk.util.Injection
import com.example.tuktuk.util.SharedPreferences
import kotlinx.coroutines.*
import okhttp3.internal.wait

class ChangePasswordFragment : Fragment() {

    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var cache: LocalCache

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!SharedPreferences.isLogin) {
            view?.findNavController()?.navigate(R.id.action_changePasswordFragment_to_loginFragment)
        }

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_change_password, container, false
        )
        binding.lifecycleOwner = this
        changePasswordViewModel = ViewModelProvider(this, Injection.provideChangePasswordViewModelFactory(requireContext()))
            .get(ChangePasswordViewModel::class.java)
        binding.changePasswordViewModel = changePasswordViewModel

        cache = Injection.provideCache(requireContext())
        val animDrawable = binding.changePasswordLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(2000)
        animDrawable.start()

        binding.passwordCheckButton.isEnabled = false
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changePasswordViewModel.isValid.observe(viewLifecycleOwner, Observer<Boolean> { isValidated ->
            binding.passwordCheckButton.isEnabled = isValidated
        })

        binding.passwordCheckButton.setOnClickListener {
            info(SharedPreferences.token, true)
        }

        binding.toProfileButton.setOnClickListener@Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            info(SharedPreferences.token)
            view?.findNavController()?.navigate(R.id.action_changePasswordFragment_to_profileFragment)
        }
    }

    private fun passwordChange(token: String, oldPassword: String, newPassword: String) {
        GlobalScope.launch {
            val response: Deferred<Int> = async (Dispatchers.IO) {changePasswordViewModel.passwordChange("password", token, oldPassword, newPassword)}
            when (response.await()) {
                200 -> {
                    Log.i("INFO", "Zmena hesla sa podarila")
                    view?.findNavController()?.navigate(R.id.action_changePasswordFragment_to_profileFragment)
                }
                else -> {

                    Log.i("INFO", "Zmena hesla sa nepodarila")
                }
            }
        }
    }

    private fun info(token: String, passwordChange: Boolean = false) {
        GlobalScope.launch {
            val response: Deferred<Int> = async (Dispatchers.IO) {changePasswordViewModel.userInfo("userProfile", token)}
            when (response.await()) {
                200 -> {
                    Log.i("INFO", "Podarilo sa")
                    if (passwordChange) {
                        passwordChange(SharedPreferences.token, changePasswordViewModel.oldPassword.value!!, changePasswordViewModel.newPassword.value!!)
                    }
                }
                401 -> {
                    Log.i("INFO", "Nespravny token")
                    view?.findNavController()?.navigate(R.id.loginFragment)
                }
            }

        }
    }
}