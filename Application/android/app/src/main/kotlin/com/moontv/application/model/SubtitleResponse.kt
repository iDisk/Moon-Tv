package com.moontv.application.model

import com.google.gson.annotations.SerializedName

data class SubtitleResponse(

    @field:SerializedName("image")
    val image: String = "",

    @field:SerializedName("subtitles")
    val subtitles: List<SubtitlesItem> = emptyList(),

    @field:SerializedName("language")
    val language: String = "",

    @field:SerializedName("id")
    val id: Int = 0
)

data class SubtitlesItem(

    @field:SerializedName("id")
    val id: Int = 0,

    @field:SerializedName("type")
    val type: String = "",

    @field:SerializedName("url")
    val url: String = ""
)
