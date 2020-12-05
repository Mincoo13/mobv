package com.example.tuktuk.home

import android.content.Context
import androidx.lifecycle.*
import com.example.tuktuk.database.*
import java.io.File


class HomeViewModel(
    private val repository: DataRepository
) : ViewModel() {

    init {

    }

    suspend fun uploadVideo(
        fileUri: File,
        token: String,
        context: Context
    ): Int {
        return repository.uploadVideo(fileUri, token, context)
    }
}