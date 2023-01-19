package com.moontv.application.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moontv.application.model.MoonTvMediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [MoonTvMediaItem::class], version = 1, exportSchema = false)
abstract class RoomDB : RoomDatabase(){

    abstract fun mediaItemDao() : MediaItemDao

    companion object {

        @Volatile
        private var INSTANCE : RoomDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope):RoomDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "room_db"
                ).addCallback(FoodItemCallback(scope))
                    .build()

                INSTANCE = instance

                // return instance
                instance
            }
        }
    }

    private class FoodItemCallback(val scope: CoroutineScope):RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { foodItemRoomDB ->
                scope.launch {
                    // if you want to populate database
                    // when RoomDatabase is created
                    // populate here

                }
            }
        }
    }
}