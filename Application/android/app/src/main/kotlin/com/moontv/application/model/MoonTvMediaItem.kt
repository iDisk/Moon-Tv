package com.moontv.application.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MediaItem")
data class MoonTvMediaItem(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "episodeId") val episodeId: Int = 0,
    @ColumnInfo(name = "mainId") val mainId: Int = 0,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "poster") val poster: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "subTitle") val subTitle: String,
    @ColumnInfo(name = "isMovie") val isMovie: Boolean,
    @ColumnInfo(name = "response") val response: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "currentTime") val currentTime: Long,
    @ColumnInfo(name = "totalTime") val totalTime: Long,

    )