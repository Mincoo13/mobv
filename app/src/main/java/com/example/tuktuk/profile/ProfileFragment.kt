package com.example.tuktuk.profile

import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
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
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.*
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

//        val uri: Uri = Uri.parse(SharedPreferences.image)
//        Log.v("INFO", SharedPreferences.image)
//        binding.profileImage.background.setImageURI(uri)
        Log.i("INFO", "ZAVOLALO SA ON CREATE")
        var imageView: CircleImageView = binding.profileImage
        Picasso.get()
            .load("http://api.mcomputing.eu/mobv/uploads/" + SharedPreferences.image)
            .placeholder(R.drawable.blank_profile_picture_973460_640)
            .error(R.drawable.blank_profile_picture_973460_640)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .into(imageView)
//        Picasso.with(requireContext()).load("http://api.mcomputing.eu/mobv/uploads/" + SharedPreferences.image)
//            .placeholder(R.drawable.blank_profile_picture_973460_640).error(R.drawable.blank_profile_picture_973460_640)
//            .into(imageView);

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("INFO", "ZAVOLALO SA ON VIEW CREATED")
        binding.logOutButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            Log.i("INFO", SharedPreferences.refresh)
            logout(SharedPreferences.refresh)
        }

        binding.toHomeArrow.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            view?.findNavController()?.navigate(R.id.action_profileFragment_to_homeFragment)
        }

        binding.changePswButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            view?.findNavController()?.navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

        val fragmentManager = (activity as FragmentActivity).supportFragmentManager

        binding.apply {
            profileImage.setOnClickListener(View.OnClickListener {
                onPause()
                ProfileDialogFragment().show(fragmentManager, "ProfileDialogFragment")
            })
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("INFO", "ZAVOLALO SA RESUME")
        info(SharedPreferences.token)
        val imageView: CircleImageView = binding.profileImage
        Picasso.get()
            .load("http://api.mcomputing.eu/mobv/uploads/" + SharedPreferences.image)
            .placeholder(R.drawable.blank_profile_picture_973460_640)
            .error(R.drawable.blank_profile_picture_973460_640)
            .into(imageView)
    }

    override fun onPause() {
        super.onPause()
        Log.i("INFO", "ZAVOLALO SA PAUSE")
        info(SharedPreferences.token)
        val imageView: CircleImageView = binding.profileImage
        Picasso.get()
            .load("http://api.mcomputing.eu/mobv/uploads/" + SharedPreferences.image)
            .placeholder(R.drawable.blank_profile_picture_973460_640)
            .error(R.drawable.blank_profile_picture_973460_640)
            .into(imageView)
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