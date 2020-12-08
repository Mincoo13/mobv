/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.tuktuk.Adapter

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.tuktuk.R
import com.example.tuktuk.network.responses.VideosResponse
import com.example.tuktuk.util.SharedPreferences
import kotlinx.android.synthetic.main.fragment_profile.view.*

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<VideosResponse>?) {
    val adapter = recyclerView.adapter as VideoGridAdapter
    adapter.submitList(data)
}

//@BindingAdapter("imageUrl")
//fun bindProfileImage(imgView: ImageView, imgUrl: String?) {
//        val imgUri = "http://api.mcomputing.eu/mobv/uploads/" + SharedPreferences.image
//        Glide.with(imgView.context)
//            .load(imgUri)
//            .placeholder(R.drawable.blank_profile_picture_973460_640)
//            .error(R.drawable.blank_profile_picture_973460_640)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
//            .into(imgView)
//}

@BindingAdapter("profile")
fun bindProfile(view: ImageView, profileSrc: String?) {
    val imgUri: Uri
    if (profileSrc != null) {
        val fullPath = "http://api.mcomputing.eu/mobv/uploads/" + profileSrc
        imgUri = fullPath.toUri().buildUpon().scheme("http").build()
        Log.i("profilePic", imgUri.toString())
    } else {
        imgUri = Uri.parse("android.resource://" + view.context.packageName + "/drawable/blank_profile_picture_973460_640")
        Log.i("profilePic", imgUri.toString())
    }
            Glide.with(view.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .placeholder(R.drawable.blank_profile_picture_973460_640)
                        .error(R.drawable.blank_profile_picture_973460_640)
                )
                .into(view)
}
