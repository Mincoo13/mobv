package com.example.tuktuk.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.tuktuk.database.*
import com.example.tuktuk.network.responses.VideosResponse
import kotlinx.coroutines.GlobalScope
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

//    val videos= getAllVideos()
//
//    val emailResponse: MutableLiveData<List<VideosResponse>> = Transformations.map()

//    val videos: LiveData<List<VideosResponse>> = Transformations.map(repositoryVideos) {
//        repositoryVideos.value
//    }

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

    private fun getVideos() {
        viewModelScope.launch {
            try {
                _videos.value = repository.getVideos()
                Log.i("INFO", _videos.value.toString())
                Log.i("INFO", "---- getVideos POCET VIDEI:  "+ _videos.value?.size.toString())
            } catch (e: Exception) {
                _videos.value = ArrayList()
            }
        }
    }

    private fun removeVideo(videoID: Int): Int {
        var responseCode: Int = 0
        GlobalScope.launch {
            try {
                responseCode = repository.removeVideo(videoID)
            } catch (e: Exception) {
                responseCode = 500
            }
        }
        return responseCode
    }
//
//    private fun getAllVideos(): MutableLiveData<List<VideosResponse>> {
//        var videos: MutableLiveData<List<VideosResponse>> = MutableLiveData<List<VideosResponse>>()
//        viewModelScope.launch {
//            videos = repository.getVideos()!!
//        }
//        return videos
//    }

//    private fun getAllVideos(): MutableLiveData<List<VideosResponse>> {
//        var videos: MutableLiveData<List<VideosResponse>> = MutableLiveData<List<VideosResponse>>()
//        viewModelScope.launch {
//            videos = repository.getVideos()!!
//        }
//        return videos
//    }
}