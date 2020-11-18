package com.example.tuktuk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.tuktuk.databinding.FragmentLoginBinding
import com.example.tuktuk.databinding.FragmentRegistrationBinding


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
        // Set the onClickListener for the submitButton
        binding.button3.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->

            val name = binding.editTextTextPersonName.text;
            val password = binding.editTextTextPassword2.text;
            val passwordCheck = binding.editTextTextPassword3.text;
            val birth = binding.editTextNumber.text;
            Log.i("INFO", name.toString())
            Log.i("INFO", password.toString())
            Log.i("INFO", passwordCheck.toString())
            Log.i("INFO", birth.toString())

        }
        return binding.root
    }
}