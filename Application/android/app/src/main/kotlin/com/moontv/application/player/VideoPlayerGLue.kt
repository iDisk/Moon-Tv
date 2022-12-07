package com.moontv.application.player

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import com.moontv.application.R

class VideoPlayerGLue(
    context: Context,
    impl: ExoLeanbackPlayerAdapter,
    private val hasSubTitle: Boolean,
    private val subtitleListener: OnChangeSubtitleListener
) : PlaybackTransportControlGlue<ExoLeanbackPlayerAdapter>(
    context, impl
) {

    private val closedCaptioningAction: PlaybackControlsRow.ClosedCaptioningAction by lazy {
        PlaybackControlsRow.ClosedCaptioningAction(context).apply {
            index = PlaybackControlsRow.ClosedCaptioningAction.INDEX_ON
        }
    }

    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter?) {
        if (hasSubTitle)
            primaryActionsAdapter?.add(this.closedCaptioningAction)
        super.onCreatePrimaryActions(primaryActionsAdapter)
    }

    override fun onActionClicked(action: Action?) {
        if (action is PlaybackControlsRow.ClosedCaptioningAction) {
            val index =
                if (this.closedCaptioningAction.index == PlaybackControlsRow.ClosedCaptioningAction.INDEX_OFF) PlaybackControlsRow.ClosedCaptioningAction.INDEX_ON else PlaybackControlsRow.ClosedCaptioningAction.INDEX_OFF

            this.closedCaptioningAction.index = index
            notifyItemChanged(
                controlsRow.primaryActionsAdapter as ArrayObjectAdapter,
                this.closedCaptioningAction
            )
            subtitleListener.onChangeSubTitle(
                index == PlaybackControlsRow.ClosedCaptioningAction.INDEX_ON,
                false,-1
            )
            host.isControlsOverlayAutoHideEnabled = true
//
            isControlsOverlayAutoHideEnabled = true
        } else {
            subtitleListener.onChangeSubTitle(enable = false, otherAction = true, pos = -1)
            super.onActionClicked(action)

        }
    }



}