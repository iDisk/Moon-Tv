package com.moontv.application.utils

import android.content.Context
import android.content.SharedPreferences

/** shared preference handle class*/
class PrefUtils {
    companion object {
        private var singleton: PrefUtils? = null
        private lateinit var preferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        const val PARENT_SEASON_KEY = "PARENT_SEASON_KEY"
        const val SOURCE_KEY = "SOURCE_KEY"
        const val MOVIE_SOURCE_KEY = "MOVIE_SOURCE_KEY"
        fun with(context: Context): PrefUtils {
            if (null == singleton)
                singleton = Builder(context, null, -1).build()
            return singleton as PrefUtils
        }

        fun with(context: Context, name: String, mode: Int): PrefUtils {
            if (null == singleton)
                singleton = Builder(context, name, mode).build()
            return singleton as PrefUtils
        }

    }

    constructor(context: Context) {
        preferences = context.getSharedPreferences("hrone_app_config", Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    constructor(context: Context, name: String, mode: Int) {
        preferences = context.getSharedPreferences(name, mode)
        editor = preferences.edit()
    }

    fun save(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun save(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun save(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun save(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun save(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun save(key: String, value: Set<String>) {
        editor.putStringSet(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return try {
            preferences.getFloat(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toFloat()
        }
    }

    fun getInt(key: String, defValue: Int): Int {
        return try {
            preferences.getInt(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toInt()
        }
    }

    fun getLastPlayedEpisode(mainId: Int): Int = preferences.getInt("$PARENT_SEASON_KEY$mainId", -1)

    fun getLastPlayedTimeOfEpisode(sourceId: Int): Long {
        return try {
            preferences.getLong("$SOURCE_KEY$sourceId", 0)
        } catch (ex: ClassCastException) {
            0
        }
    }

    fun saveEpisodeTime(mainId: Int, sourceId: Int, time: Long) {
        save("$PARENT_SEASON_KEY$mainId", sourceId)
        save("$SOURCE_KEY$sourceId", time)
    }

    fun deleteSeasonTimingTime(mainId: Int) = remove("$PARENT_SEASON_KEY$mainId")
    fun deleteEpisodeTime(sourceId: Int) = remove("$SOURCE_KEY$sourceId")
    fun deleteMovieTime(id: Int) = remove("$MOVIE_SOURCE_KEY$id")
    fun saveMovieTime(mainId: Int, sourceId: Int, time: Long) {
        save("$MOVIE_SOURCE_KEY$sourceId", time)
    }


    fun getLastPlayedTimeOfMovie(id: Int): Long = preferences.getLong("$MOVIE_SOURCE_KEY$id", 0L)

    fun getLong(key: String, defValue: Long): Long {
        return try {
            preferences.getLong(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toLong()
        }
    }

    fun getString(key: String, defValue: String): String? {
        return preferences.getString(key, defValue)
    }

    fun getStringSet(key: String, defValue: Set<String>): Set<String>? {
        return preferences.getStringSet(key, defValue)
    }

    fun getAll(): MutableMap<String, *>? {
        return preferences.all
    }

    private fun remove(key: String) {
        editor.remove(key).apply()
    }

    fun clear() {
        editor.clear().apply()
    }

    private class Builder(val context: Context, val name: String?, val mode: Int) {

        fun build(): PrefUtils {
            if (mode == -1 || name == null) {
                return PrefUtils(context)
            }
            return PrefUtils(context, name, mode)
        }
    }


}