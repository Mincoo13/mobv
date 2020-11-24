package com.example.tuktuk

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tuktuk.databinding.FragmentRegistrationBinding
import org.json.JSONObject





/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {

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

        binding.registerButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->

            val name = binding.nameInput.text;
            val password = binding.passwordInput.text;
            val passwordCheck = binding.passwordCheckInput.text;
            val birth = binding.ageInput.text;
            Log.i("INFO", name.toString())
            Log.i("INFO", password.toString())
            Log.i("INFO", passwordCheck.toString())
            Log.i("INFO", birth.toString())

// Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(view.context)
            val url = "http://api.mcomputing.eu/mobv/service.php"
            val jsonBody = JSONObject()
            jsonBody.put("action", "register")
            jsonBody.put("apikey", "uX9yA9jR8hZ6wE0mT5rZ3kA4kA6zC5")
            jsonBody.put("email", name.toString())
            jsonBody.put("username", name.toString())
            jsonBody.put("password", password.toString())




// Request a string response from the provided URL.
            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonBody,
                Response.Listener { response ->
                    System.out.print(response)
                },
                Response.ErrorListener { error ->
                    // TODO: Handle error
                }
            )

// Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)

        }
        return binding.root
    }
}