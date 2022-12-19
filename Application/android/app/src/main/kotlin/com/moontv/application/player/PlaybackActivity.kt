package com.smsolutions.tv.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.moontv.application.R
import com.moontv.application.player.EPGVideoFragment
import com.moontv.application.player.EpisodeFragment
import com.moontv.application.player.TrailerFragment



class PlaybackActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (savedInstanceState == null) {

            /** show fragment according to get data from flutter activity.*/
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    if (intent.getBooleanExtra(
                            "isTrailer",
                            false
                        )
                    ) TrailerFragment() else if (intent.getBooleanExtra(
                            "isSeries",
                            false
                        )
                    ) EpisodeFragment() else EPGVideoFragment()
                ).commit()
        }

    }
}
