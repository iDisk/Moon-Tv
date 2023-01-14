package com.moontv.application.native_view

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.moontv.application.R
import io.flutter.plugin.platform.PlatformView

internal class PlayerView(var context: Context, id: Int, val creationParams: Map<String?, Any?>?) :
    PlatformView {
    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0

    private  var view: View = LayoutInflater.from(context).inflate(R.layout.player_view, null,false)
    override fun getView(): View {
        return view
    }

    init {
        initializePlayer()
    }



    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(context).build()
        view.findViewById<PlayerView>(R.id.video_view).player = simpleExoplayer
        preparePlayer(creationParams?.get("url")?.toString().orEmpty() )
        view.findViewById<PlayerView>(R.id.video_view).controllerAutoShow = false
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = true
    }



    private fun preparePlayer(videoUrl: String) {
        val uri = Uri.parse(videoUrl)
        val mediaItem: MediaItem.Builder = MediaItem.Builder().setUri(uri)
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(mediaItem.build())

        simpleExoplayer.prepare(hlsMediaSource)
    }

    override fun onInputConnectionLocked() {
        super.onInputConnectionLocked()
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.release()
    }

    override fun dispose() {
        releasePlayer()
    }


}
