package com.example.tuktuk.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tuktuk.Adapter.MediaAdapter
import com.example.tuktuk.MediaObject
import com.example.tuktuk.R
import com.example.tuktuk.database.LocalCache
import com.example.tuktuk.databinding.FragmentHomeBinding
import com.example.tuktuk.util.Injection
import com.example.tuktuk.util.SharedPreferences
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import java.io.File

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var cache: LocalCache
    lateinit var videoUri : Uri
    val REQUEST_VIDEO_CAPTURE=1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater,
            R.layout.fragment_home,container,false)
        binding.lifecycleOwner = this
        homeViewModel = ViewModelProvider(this, Injection.provideHomeViewModelFactory(requireContext())).get(HomeViewModel::class.java)
        binding.homeViewModel = homeViewModel

        cache = Injection.provideCache(requireContext())
        binding.toProfile.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.imageView2.setOnClickListener(){
            recordVideo()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val exampleList = generateDummyList(20)
        recycler_view.adapter = MediaAdapter(exampleList)
        recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
        recycler_view.setHasFixedSize(true)

    }

    private fun generateDummyList(size: Int): List<MediaObject> {
        val list = ArrayList<MediaObject>()
        for (i in 0 until size) {
            val item = MediaObject("Item $i", "Line 2")
            list += item
        }
        return list
    }

    /*VIDEO*/
    private fun recordVideo(){
        val videoFile=createVideoFile()
        Log.i("Info", this.requireContext().toString())
        if(videoFile !=null){
            videoUri= FileProvider.getUriForFile(
                this.requireContext(),
                "com.video.record.fileprovider2",
                videoFile
            )
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT,videoUri)

            startActivityForResult(intent,REQUEST_VIDEO_CAPTURE)
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode==REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK){

//            video_view.setVideoURI(videoUri)
            //video_view.start()
            uploadVideo(videoUri)
        }
    }

    private fun createVideoFile(): File {
        val fileName="MyVideo"
        val storageDir=getActivity()?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            fileName,
            ".mp4",
            storageDir)

    }

    private fun uploadVideo(videoUri: Uri) {
        GlobalScope.launch {
            val responseExists: Deferred<Int> = async(Dispatchers.IO) {homeViewModel.uploadVideo(
                videoUri, SharedPreferences.token, requireContext()
            ) }
            when (responseExists.await()) {
                200 -> {
                    Picasso.get().invalidate("http://api.mcomputing.eu/mobv/uploads/" + SharedPreferences.image)
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
    }

}