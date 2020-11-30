package com.example.tuktuk

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.tuktuk.database.LocalCache
import com.example.tuktuk.databinding.FragmentLoginBinding
import com.example.tuktuk.databinding.FragmentRegistrationBinding
import com.example.tuktuk.registration.LoginViewModel
import com.example.tuktuk.registration.RegistrationViewModel
import com.example.tuktuk.util.Injection
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*
import java.lang.RuntimeException

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var cache: LocalCache

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        binding.lifecycleOwner = this
        loginViewModel = ViewModelProvider(this, Injection.provideLoginViewModelFactory(context!!))
            .get(LoginViewModel::class.java)

        cache = Injection.provideCache(context!!)
        val animDrawable = binding.loginLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        binding.toRegistrationButton.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            val name = binding.nameLoginInput.text
            val password = binding.passwordLoginInput.text

            login(name.toString(), password.toString())
        }
    }

    private fun login(name: String, password: String){
        GlobalScope.launch {
            val responseExists: Deferred<Int> = async (Dispatchers.IO) {loginViewModel.userExists("exists", name)}
            when (responseExists.await()) {
                409 -> {
                    val response: Deferred<Int> = async (Dispatchers.IO) {loginViewModel.userLogin("login", name, password)}
                    when (response.await()) {
                        200 -> {
                            Log.i("INFO", "Pouzivatel prihlaseny.")
                            view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                        401 -> {
                            Log.i("INFO", "Nespravne zadane udaje.")
                        }
                        500 -> {
                            Log.i("INFO", "Nastala neocakavana chyba.")
                        }
                        else -> {
                            Log.i("INFO", "Nastala naozaj neocakavana chyba.")
                        }
                    }
                }
                200 -> {
                    Log.i("INFO", "Pouzivatel neexistuje.")
                }
                500 -> {
                    Log.i("INFO", "Nastala neocakavana chyba.")
                }
                else -> {
                    Log.i("INFO", "Nastala naozaj neocakavana chyba.")
                }
            }
        }
    }
}