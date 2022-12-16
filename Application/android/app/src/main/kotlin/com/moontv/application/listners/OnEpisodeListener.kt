package com.moontv.application.listners

sealed interface OnEpisodeListener{
    fun onNextEpisode()
    fun onCancelEpisode()
    fun onPreviewEpisode()
}