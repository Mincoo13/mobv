package com.example.tuktuk.registration

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.tuktuk.R
import com.example.tuktuk.database.AppDatabaseDao
import com.example.tuktuk.database.LocalCache

import com.example.tuktuk.databinding.FragmentRegistrationBinding
import com.example.tuktuk.util.Injection
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {

    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var cache: LocalCache

    //helper global variable
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var registerContext: Context
    }

  @SuppressLint("SetTextI18n", "UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      binding = DataBindingUtil.inflate(
          inflater, R.layout.fragment_registration, container, false
      )
      binding.lifecycleOwner = this
      registrationViewModel = ViewModelProvider(this, Injection.provideViewModelFactory(context!!))
          .get(RegistrationViewModel::class.java)

      cache = Injection.provideCache(context!!)
      val animDrawable = binding.registrationLayout.background as AnimationDrawable
      animDrawable.setEnterFadeDuration(10)
      animDrawable.setExitFadeDuration(5000)
      animDrawable.start()

      // Set the onClickListener for the submitButton
      binding.toLoginButton.setOnClickListener { view : View ->
          view.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
      }

//      binding.registrationViewModel = registrationViewModel
      return binding.root
  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        registrationContext = view.context

        binding.registerButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->

            val name = binding.nameInput.text
            val email = binding.emailInput.text
            val password = binding.passwordInput.text
            val passwordCheck = binding.passwordCheckInput.text
            val birth = binding.ageInput.text

            if (name.toString() == "" || password.toString() == "" || passwordCheck.toString() == "" || birth.toString() == "") {
                binding.messageRegister.visibility = View.VISIBLE
                binding.messageRegister.text = "Musíte vyplniť všetky polia."
            }

            else if (password.toString() != passwordCheck.toString()) {
                binding.messageRegister.visibility = View.VISIBLE
                binding.messageRegister.text = "Heslá sa nezhodujú."
            }

//            else if (!isEmailValid(name.toString())) {
//                binding.messageRegister.visibility = View.VISIBLE
//                binding.messageRegister.text = "E-mail nie je zadaný v správnom tvare."
//            }

            else {
                binding.messageRegister.visibility = View.GONE
                binding.messageRegister.text = ""
            }

            register(name.toString(), email.toString(), password.toString())
        }
    }

    private fun register(name: String, email: String, password: String){
        GlobalScope.launch {
            val response: Deferred<Int> = async (Dispatchers.IO) {registrationViewModel.api("register", name, email, password)}
            val code = response.await()
            if (code == 200) {
                Log.i("INFO", "######")
//                activity?.runOnUiThread {
//                    playSuccessAnimation(startNewFragment)
//                }
            } else {
                Log.i("INFO", "code err register")
//                activity?.runOnUiThread {
//                    playErrorAnimation(code)
//                }
            }
        }
    }


    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}