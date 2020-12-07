package com.example.tuktuk.Adapter

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.EventListener
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

fun Context.toast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class PlayerViewAdapter {

    companion object{
        private var playersMap: MutableMap<Int, SimpleExoPlayer>  = mutableMapOf()

        fun releaseAllPlayers(){
            playersMap.map {
                it.value.release()
            }
        }

        fun releaseRecycledPlayers(index: Int){
            playersMap[index]?.release()
        }

        @JvmStatic
        @BindingAdapter(value = ["video_url", "item_index"], requireAll = false)
        fun PlayerView.loadVideo(url: String, item_index: Int? = null) {
            val player = SimpleExoPlayer.Builder(context).build()

            player.playWhenReady = true
            player.repeatMode = Player.REPEAT_MODE_ALL
            setKeepContentOnPlayerReset(true)
            this.useController = false
            this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo")).createMediaSource(Uri.parse(url))

            player.prepare(mediaSource)
            this.player = player

            if (playersMap.containsKey(item_index))
                playersMap.remove(item_index)
            if (item_index != null)
                playersMap[item_index] = player

            this.player!!.addListener(object : EventListener {
                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                    this@loadVideo.context.toast("Oops! Error occurred while playing media.")
                }
            })
        }
    }
}