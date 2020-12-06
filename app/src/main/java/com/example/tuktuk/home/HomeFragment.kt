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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.tuktuk.Adapter.VideoGridAdapter
import com.example.tuktuk.R
import com.example.tuktuk.database.LocalCache
import com.example.tuktuk.databinding.FragmentHomeBinding
import com.example.tuktuk.util.Injection
import com.example.tuktuk.util.SharedPreferences
import kotlinx.coroutines.*
import java.io.File

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var cache: LocalCache
    lateinit var videoUri : Uri
    lateinit var videoFile : File
    val REQUEST_VIDEO_CAPTURE=1
    private var vAdapter: VideoGridAdapter? = null
//    private lateinit var scrollListener: RecyclerViewScrollListener

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
            info(SharedPreferences.token)
            view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.imageView2.setOnClickListener(){
            info(SharedPreferences.token)
            recordVideo()
        }

        val adapter = VideoGridAdapter()
        binding.recyclerView.adapter = adapter

        val manager = GridLayoutManager(activity, 1)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.setHasFixedSize(true)

        binding.setLifecycleOwner(this)

        return binding.root
    }

    /*VIDEO*/
    private fun recordVideo(){
        videoFile =createVideoFile()
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
            uploadVideo()
        }
    }

    private fun info(token: String) {
        GlobalScope.launch {
            val response: Deferred<Int> = async (Dispatchers.IO) {homeViewModel.userInfo("userProfile", token)}
            when (response.await()) {
                200 -> {
                    Log.i("INFO", "Podarilo sa")
                }
                401 -> {
                    Log.i("INFO", "Nespravny token")
                    view?.findNavController()?.navigate(R.id.loginFragment)
                }
            }

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

    private fun uploadVideo() {
        GlobalScope.launch {
            val responseExists: Deferred<Int> = async(Dispatchers.IO) {homeViewModel.uploadVideo(
                videoFile, SharedPreferences.token, requireContext()
            ) }
            when (responseExists.await()) {
                200 -> {
                    Log.i("INFO", "Video bolo uploadnute.")
//                    Picasso.get().invalidate("http://api.mcomputing.eu/mobv/uploads/" + SharedPreferences.image)
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


    private fun setAdapter() {
        vAdapter = VideoGridAdapter()

        val manager = GridLayoutManager(activity, 1)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = vAdapter



//        val snapHelper: SnapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(recyclerView!!)

//        scrollListener = object : RecyclerViewScrollListener() {
//            override fun onItemIsFirstVisibleItem(index: Int) {
//                Log.d("visible item index", index.toString())
//                // play just visible item
//                if (index != -1)
//                    PlayerViewAdapter.playIndexThenPausePreviousPlayer(index)
//            }
//
//        }
//        recyclerView!!.addOnScrollListener(scrollListener)
//        mAdapter!!.SetOnItemClickListener(object : TikTokRecyclerAdapter.OnItemClickListener {
//            override fun onItemClick(view: View?, position: Int, model: MediaObject?) {
//
//            }
//        })
    }

//    override fun onPause() {
//        super.onPause()
//        PlayerViewAdapter.releaseAllPlayers()
//    }

}