package com.example.tuktuk.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.example.tuktuk.database.*


class HomeViewModel(
    private val repository: DataRepository
) : ViewModel() {

    init {

    }

    suspend fun uploadVideo(
        fileUri: Uri,
        token: String,
        context: Context
    ): Int {
        return repository.uploadVideo(fileUri, token, context)
    }
}