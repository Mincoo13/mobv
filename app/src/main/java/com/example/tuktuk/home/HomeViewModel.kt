package com.example.tuktuk.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tuktuk.Adapter.VideoGridAdapter
import com.example.tuktuk.database.DataRepository
import com.example.tuktuk.database.User
import com.example.tuktuk.network.responses.VideosResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


class HomeViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _videos = MutableLiveData<List<VideosResponse>>()

    val videos: LiveData<List<VideosResponse>>
        get() = _videos

//    private val _response = MutableLiveData<String>()
//
//    val videos: LiveData<List<VideosResponse>>
//        get() = _videos

////    var videos = getUsers()
//    fun getUsers(): LiveData<List<VideosResponse>>? {
//        _videos.postValue(loadUsers())
//        Log.i("INFO", "videos")
//        Log.i("INFO", _videos.value.toString())
//        return _videos
//    }

//    val videos: LiveData<List<VideosResponse>>
//        get() = _videos

    private fun loadUsers(): List<VideosResponse> {
        var videos: List<VideosResponse> = ArrayList()
        viewModelScope.launch {
            videos = repository.getVideos()!!
        }
        return videos
    }
    init {
        getVideos()
    }

    suspend fun userInfo(action: String, token: String): Int {
        return repository.userInfo(action, token)
    }

    suspend fun uploadVideo(
        fileUri: File,
        token: String,
        context: Context
    ): Int {
        return repository.uploadVideo(fileUri, token, context)
    }
//
    fun getVideos() {
        viewModelScope.launch {
            async (Dispatchers.IO) {
                try {
                    _videos.postValue(repository.getVideos())
                } catch (e: Exception) {
                    _videos.value = ArrayList()
                }
            }
        }
    }

    fun refreshVideos(adapter: VideoGridAdapter) {
        viewModelScope.launch {
            try {
                _videos.postValue(repository.getVideos())
                adapter.submitList(_videos.value)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                _videos.value = ArrayList()
            }
        }
    }
}