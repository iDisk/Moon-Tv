package com.smsolutions.tv.ui

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.replace
import com.moontv.application.R
import com.moontv.application.SubtitleListFragment
import com.moontv.application.player.EPGVideoFragment
import com.moontv.application.player.TrailerFragment

/** Loads [PlaybackVideoFragment]. */
class PlaybackActivity : FragmentActivity() {
    val list = SubtitleListFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    if (intent.getBooleanExtra(
                            "isTrailer",
                            false
                        )
                    ) TrailerFragment() else EPGVideoFragment()
                ).commit()
        }

    }
}
