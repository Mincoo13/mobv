package com.example.tuktuk

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.tuktuk.databinding.FragmentRegistrationBinding
import org.json.JSONObject





/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentRegistrationBinding>(inflater,
            R.layout.fragment_registration,container,false)

        val animDrawable = binding.registrationLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        // Set the onClickListener for the submitButton

        binding.toLoginButton.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }

        binding.registerButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->

            val name = binding.nameInput.text
            val email = binding.emailInput.text
            val password = binding.passwordInput.text
            val passwordCheck = binding.passwordCheckInput.text
            val birth = binding.ageInput.text
            Log.i("INFO", name.toString())
            Log.i("INFO", email.toString())
            Log.i("INFO", password.toString())
            Log.i("INFO", passwordCheck.toString())
            Log.i("INFO", birth.toString())


            if (name.toString() == "" || email.toString() == "" || password.toString() == "" || passwordCheck.toString() == "" || birth.toString() == "") {
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


// Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(view.context)
            val url = "http://api.mcomputing.eu/mobv/service.php"

            val jsonBodyExists = JSONObject()
            jsonBodyExists.put("action", "exists")
            jsonBodyExists.put("Content-Type", "application/json")
            jsonBodyExists.put("Cache-Control", "no-cache")
            jsonBodyExists.put("Accept", "application/json")
            jsonBodyExists.put("apikey", "uX9yA9jR8hZ6wE0mT5rZ3kA4kA6zC5")
            jsonBodyExists.put("username", name.toString())

            // Request a string response from the provided URL.
            val jsonObjectRequestExists = JsonObjectRequest(Request.Method.POST, url, jsonBodyExists,
                { response ->
                    Log.i("INFO", response.toString())
                    Log.i("INFO", "Meno je k dispozicii")
                },
                { error ->
                    Log.i("ERROR", error.toString())
                    Log.i("ERROR", "Uzivatel s danym menom uz existuje")
                }
            )

// Add the request to the RequestQueue.
            queue.add(jsonObjectRequestExists)

            val jsonBodyRegister = JSONObject()
            jsonBodyRegister.put("action", "register")
            jsonBodyRegister.put("Content-Type", "application/json")
            jsonBodyRegister.put("Cache-Control", "no-cache")
            jsonBodyRegister.put("Accept", "application/json")
            jsonBodyRegister.put("apikey", "uX9yA9jR8hZ6wE0mT5rZ3kA4kA6zC5")
            jsonBodyRegister.put("email", email.toString())
            jsonBodyRegister.put("username", name.toString())
            jsonBodyRegister.put("password", password.toString())




// Request a string response from the provided URL.
            val jsonObjectRequestRegister = JsonObjectRequest(Request.Method.POST, url, jsonBodyRegister,
                { response ->
                    Log.i("INFO", response.toString())
                },
                { error ->
                    Log.i("ERROR", error.toString())
                }
            )

// Add the request to the RequestQueue.
            queue.add(jsonObjectRequestRegister)

        }
        return binding.root
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}