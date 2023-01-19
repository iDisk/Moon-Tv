package com.moontv.application.player

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moontv.application.R
import com.moontv.application.SeasonTimeManager
import com.moontv.application.model.SubTitle
import com.moontv.application.network.ApiInterface
import com.moontv.application.network.RetrofitClient
import com.moontv.application.player.adapter.ChooseSubtitleAdapter
import org.json.JSONArray


class EpisodeFragment : VideoSupportFragment(), OnChangeSubtitleListener {
    lateinit var seasonTimeManager: SeasonTimeManager
    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<ExoLeanbackPlayerAdapter>
    private lateinit var player: ExoPlayer
    private var finish = false
    private var subTitle = ""

    private val mHandler = Handler()
    private var nextEpisodeHandler = Handler()

    private var timerRunnable: Runnable? = null

    private var nextEpisodeRunnable = Runnable {
        checkForNextEpisode()
    }

    private var isNextEpisodeDialogShowing = false

    //check for next episode
    private fun checkForNextEpisode() {
        if (!isNextEpisodeDialogShowing && player.isPlaying) {
            if (player.duration - player.currentPosition <= 15000) {
                isNextEpisodeDialogShowing = true
                if (seasonTimeManager.getNextEpisodeDetails() != null)
                    dialogNextEpisode()
            }
        }
        nextEpisodeHandler.postDelayed(nextEpisodeRunnable, 1000)
    }

    private val timerHandler = Handler()
    private var subTitles = mutableListOf<SubTitle>()
    private var selectedItem = 0

    private var mRunnable: Runnable = Runnable {
        mTransportControlGlue.host.hideControlsOverlay(true)
    }

    private val chooseSubtitleAdapter by lazy { ChooseSubtitleAdapter(context, subTitles, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { seasonTimeManager = SeasonTimeManager.with(it) }
        activity?.intent?.let {
            seasonTimeManager?.setup(
                it.getIntExtra("mainId", -1),
                it.getIntExtra("id", -1),
                it.getStringExtra("seasons") ?: ""
            )
        }

        //setup player
        activity?.apply {
            subTitle = activity?.intent?.getStringExtra("subTitle").orEmpty()
            encodeSubTitle()
            player = ExoPlayer.Builder(requireActivity()).build()
            val glueHost = VideoSupportFragmentGlueHost(this@EpisodeFragment)
            val subtitleView = findViewById<SubtitleView>(R.id.exo_subtitles)
            val playerAdapter = ExoLeanbackPlayerAdapter(this, player, 100, subtitleView)
            mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
            mTransportControlGlue =
                context?.let {
                    VideoPlayerGLue(
                        context = it,
                        playerAdapter,
                        subTitles.isNotEmpty(),
                        this@EpisodeFragment
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

            mTransportControlGlue.title = seasonTimeManager.getTitle()
            mTransportControlGlue.subtitle = seasonTimeManager.getDescription()


            val uri = Uri.parse(seasonTimeManager.getCurrentSource()?.url)
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
                mTransportControlGlue.seekTo(seasonTimeManager.getCurrentSourceTime())
                player.seekTo(seasonTimeManager.getCurrentSourceTime())
            }
            addPlayerListeners()

            nextEpisodeHandler.postDelayed(nextEpisodeRunnable, 1000)
        }
    }

    //convert string to list
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
        subTitles.forEach {}
    }

    override fun onPause() {
        super.onPause()
        //remove all handlers
        mHandler.removeCallbacks(mRunnable)
        nextEpisodeHandler.removeCallbacks(nextEpisodeRunnable)
        if (timerRunnable != null)
            timerHandler.removeCallbacks(timerRunnable!!)

        mTransportControlGlue.pause()

        //save episode time or move to the next episode and finish
        if (!finish) {
            seasonTimeManager.save(
                if (player.bufferedPercentage >= 98) 0L else player.currentPosition,
                player.duration
            )
            if (player.bufferedPercentage >= 98) {
                if (seasonTimeManager.getNextEpisodeDetails() != null) {
                    seasonTimeManager.playNextEpisode()
                    seasonTimeManager.save(0, player.duration)
                }
            }
        } else {
            if (seasonTimeManager.getNextEpisodeDetails() == null) {
                seasonTimeManager.deleteSeasonTiming()
            }
            seasonTimeManager.save(0,player.duration)
        }

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

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        seasonTimeManager.save(0, player.duration)

                        Log.d(
                            "TAG",
                            "onPlayerStateChanged: ${seasonTimeManager.getNextEpisodeDetails()}"
                        )
                        if (seasonTimeManager.getNextEpisodeDetails() == null) {
                            seasonTimeManager.deleteSeasonTiming()
                        }
                        finish = true
                        player.release()
                        activity?.finish()
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
                FirebaseCrashlytics.getInstance().recordException(
                    Exception(
                        "URL = ${activity?.intent?.getStringExtra("url")}," +
                                "ID = ${activity?.intent?.getStringExtra("id")}" +
                                "Error = ${error?.stackTrace}"
                    )
                )
            }
        })
    }


    //player click events handle here like subtitle
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
                subTitleDialog()
            mHandler.postDelayed(mRunnable, 5000)
            activity?.findViewById<SubtitleView>(R.id.exo_subtitles)?.visibility =
                if (enable) View.VISIBLE else View.INVISIBLE

        }
    }

    //show subtitle side menu
    private fun subTitleDialog() {
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

    //show next episode dialog
    private fun dialogNextEpisode() {
        var time = 10
        var playNext = false
        val dialog = Dialog(requireContext(), R.style.dialogThemeBottom)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.next_episode_layout)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        val imageView = dialog.findViewById<AppCompatImageView>(R.id.appCompatImageView)
        val title = dialog.findViewById<AppCompatTextView>(R.id.tvSeasonEpisodeTitle)
        val subTitle = dialog.findViewById<AppCompatTextView>(R.id.tvEpisodeTitle)
        val tvTimer = dialog.findViewById<AppCompatTextView>(R.id.tvTimer)
        val playNow = dialog.findViewById<ImageButton>(R.id.playNow)



        playNow.setOnClickListener {
            playNext = true
            dialog.dismiss()
        }
        context?.let {
            Glide.with(it)
                .load(seasonTimeManager.getNextEpisodeDetails()?.image)
                .apply(
                    RequestOptions()
                    .placeholder(R.drawable.image_place_holder)
                )
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fallback(R.drawable.image_place_holder)
                .into(imageView)

        }
        title.text =
            "Episode ${seasonTimeManager.getIndexOfEpisode() + 2} | ${seasonTimeManager.getNextEpisodeDetails()?.title.orEmpty()}\n(${seasonTimeManager.getNextSeasonDetails()?.title.orEmpty()})"
        subTitle.text = seasonTimeManager.getNextEpisodeDetails()?.description.orEmpty()

        //on dismiss dialog load next episode or finish the activity
        dialog.setOnDismissListener {
            if (playNext) {
                isNextEpisodeDialogShowing = false
                playNextEpisode()
                player.pause()
            } else
                activity?.finish()
        }

        timerRunnable = Runnable {
            tvTimer.text = "Playing Next Episode in $time Seconds"
            time--
            if (time != 0)
                timerHandler.postDelayed(timerRunnable!!, 1000)
            else {
                playNext = true
                dialog.dismiss()
            }
        }
        timerHandler.postDelayed(timerRunnable!!, 1000)
        dialog.show()

        playNow.requestFocus()
    }

    //play next Episode if available or finish the activity
    private fun playNextEpisode() {
        if (seasonTimeManager.getNextEpisodeDetails() != null) {
            seasonTimeManager.playNextEpisode()
            getSubtitle()
        } else activity?.finish()
    }

    //setup video player again with new subtitle
    private fun setupVideoPlayer(url: String) {
        player.release()
        player = ExoPlayer.Builder(requireActivity()).build()
        val glueHost = VideoSupportFragmentGlueHost(this@EpisodeFragment)
        val subtitleView = activity?.findViewById<SubtitleView>(R.id.exo_subtitles)
        val playerAdapter = ExoLeanbackPlayerAdapter(context, player, 100, subtitleView)
        mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        mTransportControlGlue =
            context?.let {
                VideoPlayerGLue(
                    context = it,
                    playerAdapter,
                    subTitles.isNotEmpty(),
                    this@EpisodeFragment
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


        mTransportControlGlue.title = seasonTimeManager?.getTitle()
        mTransportControlGlue.subtitle = seasonTimeManager?.getDescription()


        val uri = Uri.parse(seasonTimeManager?.getCurrentSource()?.url.orEmpty())
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
        player.seekTo(0)
        mTransportControlGlue.seekTo(0)
        addPlayerListeners()
    }

    //get the subtitle from the api
    private fun getSubtitle() {
        val retrofit = RetrofitClient.getInstance()
        val apiInterface = retrofit.create(ApiInterface::class.java)
        lifecycleScope.launchWhenCreated {
            try {
                var index = 0
                val response = apiInterface.getSubtitle(seasonTimeManager.getCurrentEpisode().id)

                subTitles.clear()
                if (response.isSuccessful) {
                    response.body().orEmpty().forEach { subTitle ->
                        subTitle.subtitles.forEach {
                            subTitles.add(
                                SubTitle(
                                    "${it.id}",
                                    it.type,
                                    it.url,
                                    subTitle.image,
                                    subTitle.language,
                                    index == 0
                                )
                            )
                            index++
                        }
                    }
                }
                setupVideoPlayer(subTitles.firstOrNull()?.url.orEmpty())
            } catch (Ex: Exception) {
                Log.e("Error", Ex.localizedMessage)
            }
        }

    }

}

