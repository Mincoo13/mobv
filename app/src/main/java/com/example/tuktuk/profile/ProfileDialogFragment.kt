package com.example.tuktuk.profile

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.tuktuk.R
import com.example.tuktuk.database.LocalCache
import com.example.tuktuk.databinding.FragmentProfileDialogBinding
import com.example.tuktuk.util.Injection
import com.example.tuktuk.util.RealPath
import com.example.tuktuk.util.SharedPreferences
import kotlinx.android.synthetic.main.fragment_profile_dialog.view.*
import kotlinx.coroutines.*
import java.io.File
import java.net.URI


class ProfileDialogFragment: DialogFragment() {
    private lateinit var profileDialogViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileDialogBinding
    private lateinit var cache: LocalCache
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner)
        var rootView: View = inflater.inflate(R.layout.fragment_profile_dialog, container, false)

        rootView.close.setOnClickListener{
            dismiss()
        }

        rootView.addPhoto.setOnClickListener{
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED) {
                // permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else {
                pickImageFromGallery()
            }
        }

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile_dialog, container, false
        )

        binding.lifecycleOwner = this
        profileDialogViewModel = ViewModelProvider(
            this, Injection.provideProfileViewModelFactory(
                requireContext()
            )
        )
            .get(ProfileViewModel::class.java)
        binding.profileDialogViewModel = profileDialogViewModel

        return rootView
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val uri: Uri? = data?.data
            Log.i("INFO", "CESTA")
            Log.i("INFO", uri.toString())
//            if (uri != null) {
//                requireContext().getContentResolver().openInputStream(uri)?.bufferedReader()?.forEachLine {
//                    val toast = Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
//                    toast.show()
//                }
//            }
            val uriString = uri.toString()
            var filePath = context?.let { data.getFilePath(it) }

            Log.i("INFO", "CESTA K IMG")
            Log.i("INFO", filePath.toString())
            var file = File(filePath)
            val value = uriString.replace("content://", "")

            GlobalScope.launch {
                val responseExists: Deferred<Int> = async(Dispatchers.IO) {profileDialogViewModel.uploadImage(
                    file, SharedPreferences.token, requireContext()
                ) }
                when (responseExists.await()) {
                    200 -> {
                    }
                    409 -> {
                        Log.i("INFO", "Pouzivatel existuje.")
                    }
                    500 -> {
                        Log.i("INFO", "Nastala neocakavana chyba.")
                    }
                    else -> {
                        Log.i("INFO", "Nastala naozaj neocakavana chyba.")
                    }
                }
            }

            Log.v("INFO", uri.toString())
            SharedPreferences.image = uri.toString()
        }
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this.requireContext(), "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun Intent?.getFilePath(context: Context): String {
        return this?.data?.let { data -> RealPath.getRealPath(context, data) ?: "" } ?: ""
    }

    fun Uri?.getFilePath(context: Context): String {
        return this?.let { uri -> RealPath.getRealPath(context, uri) ?: "" } ?: ""
    }

    fun ClipData.Item?.getFilePath(context: Context): String {
        return this?.uri?.getFilePath(context) ?: ""
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }
}