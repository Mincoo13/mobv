package com.example.tuktuk.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.tuktuk.R
import com.example.tuktuk.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentProfileBinding>(inflater,
            R.layout.fragment_profile,container,false)

        val fragmentManager = (activity as FragmentActivity).supportFragmentManager

        binding.apply {
            profileImage.setOnClickListener(View.OnClickListener {
                ProfileDialogFragment().show(fragmentManager, "ProfileDialogFragment")

            })
        }

        return binding.root
    }
}