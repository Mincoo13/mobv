package com.example.tuktuk.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.EventListener
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

// extension function for show toast
fun Context.toast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class PlayerViewAdapter {

    companion object{
        // for hold all players generated
        private var playersMap: MutableMap<Int, SimpleExoPlayer>  = mutableMapOf()
        // for hold current player
        private var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null
        fun releaseAllPlayers(){
            playersMap.map {
                it.value.release()
            }
        }

        // call when item recycled to improve performance
        fun releaseRecycledPlayers(index: Int){
            playersMap[index]?.release()
        }

        // call when scroll to pause any playing player
        fun pauseCurrentPlayingVideo(){
            if (currentPlayingVideo != null){
                currentPlayingVideo?.second?.playWhenReady = false
            }
        }

        fun playIndexThenPausePreviousPlayer(index: Int){
            if (playersMap.get(index)?.playWhenReady == false) {
                pauseCurrentPlayingVideo()
                playersMap.get(index)?.playWhenReady = true
                currentPlayingVideo = Pair(index, playersMap.get(index)!!)
            }

        }

        /*
        *  url is a url of stream video
        *  progressbar for show when start buffering stream
        * thumbnail for show before video start
        * */
        @JvmStatic
        @BindingAdapter(value = ["video_url", "item_index"], requireAll = false)
        fun PlayerView.loadVideo(url: String, item_index: Int? = null) {
            Log.i("INFO", "BINDING PLAYERVIEW ##############################")
            Log.i("INFO", url)

//            val player = SimpleExoPlayer.Builder(context).build()
//
//            player.playWhenReady = false
//            player.repeatMode = Player.REPEAT_MODE_ALL
//            // When changing track, retain the latest frame instead of showing a black screen
//            setKeepContentOnPlayerReset(true)
//            // We'll show the controller, change to true if want controllers as pause and start
//            this.useController = false
            // Provide url to load the video from here
            var dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "testapp"))
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(url))
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .build()

            Log.i("INFO", mediaItem.playbackProperties.toString())
            Log.i("INFO", mediaItem.mediaId.toString())

            val exoPlayer = SimpleExoPlayer.Builder(context).build().apply {
                playWhenReady = true
                setMediaItem(mediaItem, false)
                useController = false
                prepare()
            }
            this.player = exoPlayer

//            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo")).createMediaSource(Uri.parse(url))

//            player.prepare(mediaSource)

//            this.player = player
//
//             add player with its index to map
            if (playersMap.containsKey(item_index))
                playersMap.remove(item_index)
            if (item_index != null)
                playersMap[item_index] = exoPlayer

            this.player!!.addListener(object : EventListener {
                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                    this@loadVideo.context.toast("Oops! Error occurred while playing media.")
                }
            })
        }
    }
}