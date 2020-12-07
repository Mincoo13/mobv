package com.example.tuktuk.Adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tuktuk.databinding.MediaObjectBinding
import com.example.tuktuk.network.responses.VideosResponse
import kotlinx.android.synthetic.main.media_object.view.*

class VideoGridAdapter() : ListAdapter<VideosResponse, VideoGridAdapter.VideoViewHolder>(DiffCallback) {
    class VideoViewHolder(private var binding: MediaObjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val shareBtn: ImageView = itemView.shareBtn
        fun bind(video: VideosResponse) {
            binding.video = video
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(MediaObjectBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
        holder.shareBtn.setOnClickListener() {
            val context = holder.itemView.context
            val urlVideo =
                "Kukaj na toto super video cislo " + position.toString() + " zo skvelej aplikacie TukTuk!"
            context?.startActivity(shareVideo(urlVideo))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<VideosResponse>() {
        override fun areItemsTheSame(oldItem: VideosResponse, newItem: VideosResponse): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: VideosResponse, newItem: VideosResponse): Boolean {
            return oldItem.postid == newItem.postid
        }
    }

    private fun shareVideo(urlVideo: String): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlVideo)
            type = "text/plain"
        }
    }
}

