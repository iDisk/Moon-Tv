package com.moontv.application.player

import android.net.Uri
import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moontv.application.R


class TrailerFragment : VideoSupportFragment(), OnChangeSubtitleListener {

    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<ExoLeanbackPlayerAdapter>
    private lateinit var player: ExoPlayer
    private var finish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.apply {
            player = ExoPlayer.Builder(requireActivity()).build()
            val glueHost = VideoSupportFragmentGlueHost(this@TrailerFragment)
            val subtitleView = findViewById<SubtitleView>(R.id.exo_subtitles)
            val playerAdapter = ExoLeanbackPlayerAdapter(this, player, 100, subtitleView)
            mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
            mTransportControlGlue =
                context?.let {
                    VideoPlayerGLue(
                        context = it,
                        playerAdapter,
                        false,
                        this@TrailerFragment
                    )
                }!!

            mTransportControlGlue.host = glueHost
            glueHost.isControlsOverlayAutoHideEnabled = true
            mTransportControlGlue.isSeekEnabled = true
            mTransportControlGlue.isControlsOverlayAutoHideEnabled = true
            isControlsOverlayAutoHideEnabled = true
            mTransportControlGlue.playWhenPrepared()
            isControlsOverlayAutoHideEnabled = true


            playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)


            mTransportControlGlue.title = activity?.intent?.getStringExtra("title")
            mTransportControlGlue.subtitle = activity?.intent?.getStringExtra("description")

            hideControlsOverlay(false)
            glueHost.hideControlsOverlay(false)
            mTransportControlGlue.host.hideControlsOverlay(false)

            val uri = Uri.parse(activity?.intent?.getStringExtra("url"))
            val mediaItem: MediaItem.Builder = MediaItem.Builder().setUri(uri)

            player.setMediaItem(mediaItem.build())
            player.prepare()
            player.play()
            addPlayerListeners()

        }
    }


    override fun onPause() {
        super.onPause()
        mTransportControlGlue.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!mTransportControlGlue.isPlaying) {
            mTransportControlGlue.play()
        }
    }

    override fun onStop() {
        player.release()
        super.onStop()
    }

    private fun addPlayerListeners() {
        player.addListener(object : Player.Listener {
            override fun onSeekProcessed() {
                mTransportControlGlue.play()
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {

                    Player.STATE_ENDED -> {
                        finish = true
                        player.release()
                        activity?.finish()
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                FirebaseCrashlytics.getInstance().recordException(
                    Exception(
                    "URL = ${activity?.intent?.getStringExtra("url")}," +
                            "ID = ${activity?.intent?.getStringExtra("id")}" +
                            "Error = ${error.stackTrace}")
                )
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
                FirebaseCrashlytics.getInstance().recordException(
                    Exception(
                    "URL = ${activity?.intent?.getStringExtra("url")}," +
                            "ID = ${activity?.intent?.getStringExtra("id")}" +
                            "Error = ${error?.stackTrace}")
                )
            }
        })
    }
    override fun onChangeSubTitle(enable: Boolean, otherAction: Boolean, pos: Int) {}
}

