package com.example.tuktuk.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.tuktuk.R
import com.example.tuktuk.database.LocalCache
import com.example.tuktuk.databinding.FragmentProfileBinding
import com.example.tuktuk.login.LoginViewModel
import com.example.tuktuk.util.Injection
import com.example.tuktuk.util.SharedPreferences
import kotlinx.coroutines.*

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var cache: LocalCache

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        binding.lifecycleOwner = this
        profileViewModel = ViewModelProvider(this, Injection.provideProfileViewModelFactory(requireContext()))
            .get(ProfileViewModel::class.java)

        info(SharedPreferences.token)

        binding.emailProfile.text = SharedPreferences.email
        binding.usernameProfile.text = SharedPreferences.username

        val uri: Uri = Uri.parse(SharedPreferences.image)
        Log.v("INFO", SharedPreferences.image)
        binding.imageView3.setImageURI(uri)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logOutButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            Log.i("INFO", SharedPreferences.refresh)
            logout(SharedPreferences.refresh)
        }

        val fragmentManager = (activity as FragmentActivity).supportFragmentManager

        binding.apply {
            profileImage.setOnClickListener(View.OnClickListener {
                ProfileDialogFragment().show(fragmentManager, "ProfileDialogFragment")
            })
        }
    }

    private fun info(token: String) {
        GlobalScope.launch {
            val response: Deferred<Int> = async (Dispatchers.IO) {profileViewModel.userInfo("userProfile", token)}
            when (response.await()) {
                200 -> {
                    Log.i("INFO", "Podarilo sa")
                }
                401 -> {
                    Log.i("INFO", "Nespravny token")
                    SharedPreferences.isLogin = false
                    view?.findNavController()?.navigate(R.id.action_profileFragment_to_loginFragment)
                }
            }

        }
    }

    private fun logout(refresh: String) {
        GlobalScope.launch {
            val response: Deferred<Int> = async (Dispatchers.IO) {profileViewModel.userLogout("refreshToken", refresh)}
            SharedPreferences.isLogin = false
            view?.findNavController()?.navigate(R.id.action_profileFragment_to_loginFragment)
            when (response.await()) {
                200 -> {
                    Log.i("INFO", refresh)
                    Log.i("INFO", "Odhlaseny")
                }
                else -> {
                    Log.i("INFO", refresh)
                    Log.i("INFO", "Chyba")
                }
            }

        }
    }
}