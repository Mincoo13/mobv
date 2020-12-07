package com.example.tuktuk.Adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import android.content.res.Resources
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
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class VideoGridAdapter() : ListAdapter<VideosResponse, VideoViewHolder>(DiffCallback) {

    inner class VideoViewHolder(private var binding: MediaObjectBinding): RecyclerView.ViewHolder(binding.root) {

        private fun getScreenHeight(): Int {
            val px = Resources.getSystem().displayMetrics.heightPixels
            return px
        }
        val shareBtn: ImageView = itemView.shareBtn
        fun bind(video: VideosResponse) {

            var imageView: CircleImageView = binding.profileImage
            Picasso.get()
                .load("http://api.mcomputing.eu/mobv/uploads/" + video.profile)
                .placeholder(R.drawable.blank_profile_picture_973460_640)
                .error(R.drawable.blank_profile_picture_973460_640)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView)

            binding.videoView.layoutParams.height = getScreenHeight()

            binding.video = video
            binding.index = adapterPosition
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
        holder.shareBtn.setOnClickListener() {
            val context = holder.itemView.context
            val urlVideo =
                "Kukaj na toto super video cislo " + position.toString() + " zo skvelej aplikacie TukTuk!"
            context?.startActivity(shareVideo(urlVideo))
        }
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        PlayerViewAdapter.releaseRecycledPlayers(holder.adapterPosition)
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

    private fun shareVideo(urlVideo: String): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlVideo)
            type = "text/plain"
        }
    }
}
