package com.example.tuktuk.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tuktuk.Adapter.VideoGridAdapter.VideoViewHolder
import com.example.tuktuk.R
import com.example.tuktuk.databinding.MediaObjectBinding
import com.example.tuktuk.network.responses.VideosResponse
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoGridAdapter() : ListAdapter<VideosResponse, VideoViewHolder>(DiffCallback) {

//    private var mPlayer: SimpleExoPlayer? = null
//    private var playWhenReady = true
//    private var currentWindow = 0
//    private var playbackPosition: Long = 0

    class VideoViewHolder(private var binding: MediaObjectBinding): RecyclerView.ViewHolder(binding.root) {
        lateinit var videoURL: Uri
        lateinit var player: SimpleExoPlayer

        fun bind(video: VideosResponse) {
            Log.i("INFO", "VideoViewHolder bind")
            Log.i("INFO", Uri.parse("http://api.mcomputing.eu/mobv/uploads/"+video.videourl).toString())
            Log.i("INFO", video.videourl)


            videoURL = Uri.parse("http://api.mcomputing.eu/mobv/uploads/"+video.videourl)

            binding.video = video
            binding.executePendingBindings()
        }

        fun initPlayer(holder: VideoViewHolder, position: Int){
            Log.i("INFO", "POSITION: $position")
            if(position == 1) {
                player = SimpleExoPlayer.Builder(binding.root.context).build()

                player.playWhenReady = false
                player.repeatMode = Player.REPEAT_MODE_ALL
                player.prepare(buildMediaSource(), false, false)
//
                binding.videoView.useController = false
                binding.videoView.player = player
            }

        }


        fun buildMediaSource():ExtractorMediaSource  {
//            val userAgent = Util.getUserAgent(binding.videoView.context, binding.videoView.context.getString(
//                R.string.app_name))

//            val dataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
//            val mediaSource =  ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoURL)
//            val mediaSource =  HlsMediaSource.Factory(dataSourceFactory).createMediaSource(videoURL)

            val userAgent = Util.getUserAgent(binding.videoView.context, "ExoPlayer")
            val mediaSource = ExtractorMediaSource(videoURL, DefaultDataSourceFactory(binding.videoView.context, userAgent), DefaultExtractorsFactory(), null, null)
            return mediaSource

        }

        fun releasePlayer(position: Int) {
            if (position != null)
                player.release()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(MediaObjectBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
        Log.i("INFO", "onBindViewHolder")
        holder.initPlayer(holder, position)
        Log.i("INFO", "POSITION: $position")
        Log.i("INFO", "initPlayer")
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        holder.releasePlayer(holder.adapterPosition)
        super.onViewRecycled(holder)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<VideosResponse>() {
        override fun areItemsTheSame(oldItem: VideosResponse, newItem: VideosResponse): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: VideosResponse, newItem: VideosResponse): Boolean {
            return oldItem.postid == newItem.postid
        }
    }


}
