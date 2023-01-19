package com.moontv.application.roomdb

import androidx.room.*
import com.google.android.exoplayer2.MediaItem
import com.moontv.application.model.MoonTvMediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {

    @Query("SELECT * FROM MediaItem ORDER BY date DESC LIMIT 10")
    suspend fun getAllMedia(): MutableList<MoonTvMediaItem>

    @Query("SELECT * FROM MediaItem ORDER BY date")
    fun getAllMediaFlow(): Flow<MutableList<MoonTvMediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodItem: MoonTvMediaItem)

    @Delete
    suspend fun delete(foodItem: MoonTvMediaItem)
//
//    @Query("SELECT * FROM MediaItem WHERE id = :id LIMIT 1")
//    suspend fun getMedia(id: Int): MediaItem?

    @Query("DELETE FROM MediaItem WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM MediaItem WHERE mainId = :mainSeasonId")
    fun deleteMediaBySeasonId(mainSeasonId: Int)


}