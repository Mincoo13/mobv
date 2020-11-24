package com.example.tuktuk

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.tuktuk.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.fragment_login.*
import java.lang.RuntimeException

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,
            R.layout.fragment_login,container,false)

        val animDrawable = binding.loginLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        binding.loginButton.setOnClickListener {
            throw RuntimeException("App Crashed");
        }

        binding.toRegistrationButton.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

}