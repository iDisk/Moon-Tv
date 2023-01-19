package com.moontv.application

import android.app.Application
import com.moontv.application.roomdb.MediaItemRepository
import com.moontv.application.roomdb.RoomDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MoonTvApp : Application() {
    private val database by lazy { RoomDB.getDatabase(this, CoroutineScope(SupervisorJob())) }
    val repository by lazy { MediaItemRepository(database.mediaItemDao()) }

}