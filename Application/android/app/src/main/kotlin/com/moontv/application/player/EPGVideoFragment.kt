package com.moontv.application.player

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moontv.application.R
import com.moontv.application.model.SubTitle
import com.moontv.application.player.adapter.ChooseSubtitleAdapter
import org.json.JSONArray


//import org.robolectric.shadows.ShadowContextImpl.CLASS_NAME


class EPGVideoFragment : VideoSupportFragment(), OnChangeSubtitleListener {

    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<ExoLeanbackPlayerAdapter>
    private lateinit var player: ExoPlayer
    private var finish = false
    private var subTitle = ""

    private val mHandler = Handler()
    private var subTitles = mutableListOf<SubTitle>()
    private var selectedItem = 0


    private var mRunnable: Runnable = Runnable {
        mTransportControlGlue.host.hideControlsOverlay(true)
    }

    private val chooseSubtitleAdapter by lazy { ChooseSubtitleAdapter(context, subTitles, this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.apply {

            subTitle = activity?.intent?.getStringExtra("subTitle").orEmpty()
            encodeSubTitle()

            player = ExoPlayer.Builder(requireActivity()).build()
            val glueHost = VideoSupportFragmentGlueHost(this@EPGVideoFragment)
            val subtitleView = findViewById<SubtitleView>(R.id.exo_subtitles)
            val playerAdapter = ExoLeanbackPlayerAdapter(this, player, 100, subtitleView)
            mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
            mTransportControlGlue =
                context?.let {
                    VideoPlayerGLue(
                        context = it,
                        playerAdapter,
                        subTitles.isNotEmpty(),
                        this@EPGVideoFragment
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


            val uri = Uri.parse(activity?.intent?.getStringExtra("url"))
            val mediaItem: MediaItem.Builder = MediaItem.Builder().setUri(uri)

            if (subTitle.isNotEmpty()) {
                val subtitle: MediaItem.SubtitleConfiguration =
                    MediaItem.SubtitleConfiguration.Builder(
                        Uri.parse(
                            subTitles.firstOrNull()?.url.orEmpty()
                        )
                    )
                        .setMimeType(MimeTypes.TEXT_VTT) // The correct MIME type (required).
                        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT) // MUST,  Selection flags for the track (optional).
                        .build()
                mediaItem.setSubtitleConfigurations(ImmutableList.of(subtitle))
            }

            player.setMediaItem(mediaItem.build())
            player.prepare()
            player.play()
            if (activity?.intent?.getBooleanExtra("resume", false) == true) {
                mTransportControlGlue.seekTo(getCurrentTime())
                player.seekTo(getCurrentTime())
            }
            addPlayerListeners()

        }

    }

    private fun encodeSubTitle() {
        val jsonArr = JSONArray(subTitle)
        for (i in 0 until jsonArr.length()) {
            subTitles.add(
                SubTitle(
                    jsonArr.getJSONObject(i).getString("id"),
                    jsonArr.getJSONObject(i).getString("type"),
                    jsonArr.getJSONObject(i).getString("url"), "",
                    jsonArr.getJSONObject(i).getString("language"),
                    i == 0
                )
            )
        }
        subTitles.forEach {
            Log.d("TAG", "encodeSubTitle() called ${it.language}")
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mRunnable)
        mTransportControlGlue.pause()
        if (!finish) {
            saveCurrentTime(if (player.bufferedPercentage >= 98) 0L else player.currentPosition)
        } else saveCurrentTime(0)

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
                        saveCurrentTime(0)
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

    private fun getCurrentTime(): Long {
        var time = 0L
        context?.let { context ->
            val sharedPreferences =
                context.getSharedPreferences("hrone_app_config", Context.MODE_PRIVATE)
            time = sharedPreferences.getLong("trackID${activity?.intent?.getStringExtra("id")}", 0)
        }
        Log.d("TAG", "getCurrentTime: ${time}")
        return time
    }

    fun saveCurrentTime(l: Long) {
        context?.let { context ->
            val sharedPreferences =
                context.getSharedPreferences("hrone_app_config", Context.MODE_PRIVATE)
            sharedPreferences.edit().putLong("trackID${activity?.intent?.getStringExtra("id")}", l)
                .apply()
            Log.d("TAG", "saveCurrentTime: ${l}")
        }
    }

    override fun onChangeSubTitle(enable: Boolean, otherAction: Boolean, pos: Int) {
        if ((subTitles.size > 1) && pos >= 0) {
            val mutableList = subTitles.map { it.copy(selected = false) } as MutableList<SubTitle>
            mutableList[pos] = mutableList[pos].copy(selected = true)
            subTitles.clear()
            subTitles.addAll(mutableList)
            chooseSubtitleAdapter?.notifyDataSetChanged()
            setupVideoPlayer(subTitles[pos].url)
        }

        mHandler.removeCallbacks(mRunnable)

        if (!otherAction) {
            if (pos == -1 && enable && subTitles.size > 1)
                dialog()
            mHandler.postDelayed(mRunnable, 5000)
            activity?.findViewById<SubtitleView>(R.id.exo_subtitles)?.visibility =
                if (enable) View.VISIBLE else View.INVISIBLE

        }
    }

    private fun dialog() {
        val dialog = Dialog(requireContext(), R.style.dialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.fragment_subtitle_list)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chooseSubtitleAdapter
        dialog.show()
        recyclerView.requestFocus()

    }

    private fun setupVideoPlayer(url: String) {
        val currentPosition = player.currentPosition
        player.release()
        player = ExoPlayer.Builder(requireActivity()).build()
        val glueHost = VideoSupportFragmentGlueHost(this@EPGVideoFragment)
        val subtitleView = activity?.findViewById<SubtitleView>(R.id.exo_subtitles)
        val playerAdapter = ExoLeanbackPlayerAdapter(context, player, 100, subtitleView)
        mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        mTransportControlGlue =
            context?.let {
                VideoPlayerGLue(
                    context = it,
                    playerAdapter,
                    subTitle.isNotEmpty(),
                    this@EPGVideoFragment
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


        val uri = Uri.parse(activity?.intent?.getStringExtra("url"))
        val mediaItem: MediaItem.Builder = MediaItem.Builder().setUri(uri)

        val subtitle: MediaItem.SubtitleConfiguration =
            MediaItem.SubtitleConfiguration.Builder(
                Uri.parse(
                    url
                )
            )
                .setMimeType(MimeTypes.TEXT_VTT) // The correct MIME type (required).
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT) // MUST,  Selection flags for the track (optional).
                .build()
        mediaItem.setSubtitleConfigurations(ImmutableList.of(subtitle))
        player.clearMediaItems()
        player.setMediaItem(mediaItem.build())
        player.prepare()
        player.play()
        player.seekTo(currentPosition)
        mTransportControlGlue.seekTo(currentPosition)
        addPlayerListeners()
    }
}

