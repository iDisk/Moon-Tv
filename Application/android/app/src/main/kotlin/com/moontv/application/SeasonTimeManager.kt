package com.moontv.application


import android.content.Context
import android.util.Log
import com.moontv.application.ext.convertToListObject
import com.moontv.application.model.Episode
import com.moontv.application.model.SeasonItem
import com.moontv.application.utils.PrefUtils

/** this class use for handle season timing*/
class SeasonTimeManager {
    companion object {
        private lateinit var seasons: List<SeasonItem>
        var indexOfSource = 0
        var indexOfEpisode = 0
        var indexOfSeason = 0
        var indexOfMainSeason = 0
        var mainSeasonId = -1
        lateinit var prefUtils: PrefUtils
        private var singleton: SeasonTimeManager? = null

        fun with(context: Context): SeasonTimeManager {
            prefUtils = PrefUtils.with(context)
            if (null == singleton)
                singleton = SeasonTimeManager()
            return singleton as SeasonTimeManager
        }
    }


    fun getIndexOfSource() = indexOfSource
    fun getIndexOfEpisode() = indexOfEpisode
    fun getIndexOfSeason() = indexOfSeason
    fun getLastPlayedEpisode(mainId: Int): Int {
        return prefUtils.getLastPlayedEpisode(mainId)
    }

    // get Next Episode Details
    fun getNextEpisodeDetails(): Episode? {
        if (hasNextEpisode()) {
            return seasons[indexOfSeason].episodes[indexOfEpisode + 1]
        } else if (hasNextSeason()) {
            return seasons[indexOfSeason + 1].episodes[0]
        }
        return null
    }

    //get Next Season Detail
    fun getNextSeasonDetails(): SeasonItem? {
        if (hasNextEpisode()) {
            return seasons[indexOfSeason]
        } else {
            if (hasNextSeason())
                return seasons[indexOfSeason + 1]
        }
        return seasons[indexOfSeason]
    }

    /** @return if next season available returns true otherwise false*/
    private fun hasNextSeason(): Boolean = seasons.size > indexOfSeason + 1

    /** @return if next episode available returns true otherwise false*/
    private fun hasNextEpisode(): Boolean =
        indexOfEpisode + 1 <= seasons[indexOfSeason].episodes.size - 1

    /** get current episode source object*/
    fun getCurrentSource() =
        seasons[indexOfSeason].episodes[indexOfEpisode].sources[indexOfSource]

    /** get current episode source object*/
    fun getCurrentEpisode() =
        seasons[indexOfSeason].episodes[indexOfEpisode]

    /** save the current episode time*/
    fun save(time: Long) {
        prefUtils.saveEpisodeTime(mainSeasonId, getCurrentSource().id, time)
    }

    /** @param mainId = main id of the season
     * @param seasonResponse string response of the season
     * @param sourceId id of source that plays next
     */
    fun setup(mainId: Int, sourceId: Int, seasonResponse: String) {
        var ios = 0
        var ioe = 0
        var iose = 0
        mainSeasonId = mainId

        seasons = seasonResponse.convertToListObject() ?: emptyList()
        loop@ for (season in seasons) {
            for (episode in season.episodes) {
                ios = 0
                for (source in episode.sources) {
                    if (source.id == sourceId) {
                        prefUtils
                        indexOfSource = ios
                        indexOfEpisode = ioe
                        indexOfSeason = iose
                        break@loop
                    }
                    ios++
                }
                ioe++
            }
            ioe = 0
            iose++
        }

        Log.d(
            "TAG", "setup: $ios\n" +
                    "$ioe\n" +
                    "$iose"
        )
    }

    /** @return title of the current season*/
    fun getTitle(): String {
        return seasons[indexOfSeason].title
    }

    /** @return returns the description */
    fun getDescription(): String {
        return seasons[indexOfSeason].episodes[indexOfEpisode].title
    }

    /** play next Episode*/
    fun playNextEpisode() {
        save(0L)
        if (hasNextEpisode())
            indexOfEpisode++
        else if (hasNextSeason()) {
            indexOfSeason++
            indexOfEpisode = 0
        }
    }
    /** @return current episode time*/
    fun getCurrentSourceTime(): Long {
        return prefUtils.getLastPlayedTimeOfEpisode(getCurrentSource().id)
    }

    /** delete the season from shared preference*/
    fun deleteSeasonTiming() {
        prefUtils.deleteSeasonTimingTime(mainSeasonId)
    }

}