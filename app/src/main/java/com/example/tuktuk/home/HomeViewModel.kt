package com.example.tuktuk.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.example.tuktuk.database.*
import com.example.tuktuk.network.responses.VideosResponse
import kotlinx.coroutines.launch
import java.io.File


class HomeViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _videos = MutableLiveData<List<VideosResponse>>()

    val videos: LiveData<List<VideosResponse>>
        get() = _videos


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
            } catch (e: Exception) {
                _videos.value = ArrayList()
            }
        }
    }
}