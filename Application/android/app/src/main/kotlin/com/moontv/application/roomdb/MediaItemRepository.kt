package com.moontv.application.roomdb

import androidx.annotation.WorkerThread
import com.moontv.application.model.MoonTvMediaItem
import kotlinx.coroutines.flow.Flow

class MediaItemRepository(private val mediaItemDeo: MediaItemDao) {

    @WorkerThread
    suspend fun insert(mediaItem: MoonTvMediaItem) {
        mediaItemDeo.insert(mediaItem)
    }

    @WorkerThread
    suspend fun delete(mediaItem: MoonTvMediaItem) {
        mediaItemDeo.delete(mediaItem)
    }

    @WorkerThread
    suspend fun getAllMedia(): MutableList<MoonTvMediaItem> {
        return mediaItemDeo.getAllMedia()
    }

//    @WorkerThread
//    suspend fun getMedia(id:Int) {
//        mediaItemDeo.getMedia(id)
//    }

    @WorkerThread
    suspend fun deleteMedia(id: Int) {
        mediaItemDeo.delete(id)
    }

    @WorkerThread
    fun getAllMediaFlow(): Flow<MutableList<MoonTvMediaItem>> = mediaItemDeo.getAllMediaFlow()

    @WorkerThread
    fun deleteMediaBySeason(mainSeasonId: Int) = mediaItemDeo.deleteMediaBySeasonId(mainSeasonId)


}