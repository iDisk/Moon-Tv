package com.moontv.application


import android.content.Context
import android.util.Log
import com.moontv.application.ext.convertToListObject
import com.moontv.application.model.Episode
import com.moontv.application.model.SeasonItem
import com.moontv.application.model.Source
import com.moontv.application.utils.PrefUtils

class SeasonTimeManager(private val context: Context) {
    private lateinit var seasons: List<SeasonItem>
    lateinit var prefUtils: PrefUtils
    var indexOfSource = 0
    var indexOfEpisode = 0
    var indexOfSeason = 0
    var indexOfMainSeason = 0
    var mainSeasonId = -1

    init {
        prefUtils = PrefUtils.with(context)
    }

    fun getLastPlayedEpisode(mainId: Int): Int {
        return PrefUtils.with(context).getLastPlayedEpisode(mainId)
    }

    fun getNextEpisodeDetails(): Episode? {
        if (hasNextEpisode()) {
            return seasons[indexOfSeason].episodes[indexOfEpisode + 1]
        } else if (hasNextSeason()) {
            return seasons[indexOfSeason + 1].episodes[0]
        }
        return null
    }

    fun getNextSeasonDetails(): SeasonItem? {
        if (hasNextEpisode()) {
            return seasons[indexOfSeason]
        } else {
            if (hasNextSeason())
                return seasons[indexOfSeason + 1]
        }
        return seasons[indexOfSeason]
    }

    private fun hasNextSeason(): Boolean = seasons.size > indexOfSeason + 1

    private fun hasNextEpisode(): Boolean =
        indexOfEpisode + 1 <= seasons[indexOfSeason].episodes.size - 1

    fun getCurrentSource() =
        seasons[indexOfSeason].episodes[indexOfEpisode].sources[indexOfSource]

    fun save(time: Long) {
        prefUtils.saveEpisodeTime(mainSeasonId, getCurrentSource().id, time)
    }

    fun setup(mainId: Int, sourceId: Int, seasonResponse: String) {
        var indexOfSource = 0
        var indexOfEpisode = 0
        var indexOfSeason = 0
        mainSeasonId = mainId

        seasons = seasonResponse.convertToListObject() ?: emptyList()
        loop@ for (season in seasons) {
            for (episode in season.episodes) {
                indexOfSource = 0
                for (source in episode.sources) {
                    if (source.id == sourceId) {
                        Log.d("TAG1", "setup: $indexOfSource, $indexOfEpisode, $indexOfSeason")
                        this.indexOfSource = indexOfSource
                        this.indexOfEpisode = indexOfEpisode
                        this.indexOfSeason = indexOfSeason
                        break@loop
                    }
                    indexOfSource++
                }
                indexOfEpisode++
            }
            indexOfEpisode = 0
            indexOfSeason++
        }

        Log.d(
            "TAG", "setup: $indexOfSource\n" +
                    "$indexOfEpisode\n" +
                    "$indexOfSeason"
        )
    }

    fun getTitle(): String {
        return seasons[indexOfSeason].title
    }


    fun getDescription(): String {
        return seasons[indexOfSeason].episodes[indexOfEpisode].title
    }

    fun playNextEpisode() {
        save(0L)
        if (hasNextEpisode())
            indexOfEpisode++
        else if (hasNextSeason()) {
            indexOfSeason++
            indexOfEpisode = 0
        }
    }

    fun getCurrentSourceTime(): Long {
        return prefUtils.getLastPlayedTimeOfEpisode(getCurrentSource().id)
    }

    fun deleteSeasonTiming() {
        prefUtils.deleteSeasonTimingTime(mainSeasonId)
    }

}