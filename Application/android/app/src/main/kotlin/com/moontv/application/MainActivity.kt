package com.moontv.application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.annotation.NonNull
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moontv.application.ext.convertToListObject
import com.moontv.application.model.Season
import com.moontv.application.model.SeasonItem
import com.moontv.application.native_view.NativeViewFactory
import com.moontv.application.utils.PrefUtils
import com.smsolutions.tv.ui.PlaybackActivity
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : FlutterActivity() {
    private val CHANNEL = "VIDEO_PLAYER_CHANNEL"
    lateinit var prefUtils: PrefUtils
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        prefUtils = PrefUtils.with(context)

    }

    private var playerLaunchedTimeStamp = 0L


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        flutterEngine
            .platformViewsController
            .registry
            .registerViewFactory("player_view", NativeViewFactory())

        prefUtils = PrefUtils.with(context)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // This method is invoked on the main thread.
                call, result ->
            if (call.method == "launchVideoPlayer" && playerLaunchedTimeStamp < System.currentTimeMillis()) {
                playerLaunchedTimeStamp = (System.currentTimeMillis() + 500)
                val intent = Intent(this@MainActivity, PlaybackActivity::class.java)
                val url = call.argument<Any>("url")
                val id = call.argument<Any>("id")
                val subTitle = call.argument<String>("subTitle")
                val title = call.argument<Any>("title")
                val resume = call.argument<Boolean>("resume")
                val description = call.argument<Any>("description")
                val isTrailer = call.argument<Boolean>("isTrailer")
                val isLiveTv = call.argument<Boolean>("isLiveTv")
                intent.putExtra("url", "$url")
                intent.putExtra("title", "$title")
                intent.putExtra("id", "$id")
                intent.putExtra("subTitle", "$subTitle")
                intent.putExtra("resume", resume)
                intent.putExtra("description", "$description")
                intent.putExtra("isTrailer", isTrailer)
                intent.putExtra("isLiveTv", isLiveTv)
                startActivity(intent)

                result.success(true)
            } else if (call.method == "getVideoLastTime") {
                Log.d("TAG", "configureFlutterEngine: ${call.argument<Int>("id")}")
                result.success(getCurrentTime(call.argument<Int>("id") ?: 0))
            }
            /** it will return episodes source id from the main season id*/
            else if (call.method == "getLastPlayedEpisodeDetail") {
                result.success(prefUtils.getLastPlayedEpisode(call.argument<Int>("id") ?: -1))
            }
            /** launch episode fragment and pass data*/
            else if (call.method == "playEpisodes" && playerLaunchedTimeStamp < System.currentTimeMillis()) {
                playerLaunchedTimeStamp = (System.currentTimeMillis() + 500)

                val intent = Intent(this@MainActivity, PlaybackActivity::class.java)
                val id = call.argument<Int>("id")
                val mainId = call.argument<Int>("mainId")
                val subTitle = call.argument<String>("subTitle")
                val title = call.argument<String>("title")
                val resume = call.argument<Boolean>("resume")
                val description = call.argument<String>("description")
                val seasons = call.argument<String>("seasons")

                intent.putExtra("title", "$title")
                intent.putExtra("id", id)
                intent.putExtra("mainId", mainId)
                intent.putExtra("subTitle", "$subTitle")
                intent.putExtra("resume", resume)
                intent.putExtra("description", "$description")
                intent.putExtra("isSeries", true)
                intent.putExtra("seasons", seasons)
                startActivity(intent)
                result.success(true)
            }
        }
    }

    //returns last played time of the movie
    private fun getCurrentTime(id: Int): Long {
        var time = 0L
        context.let { context ->
            val sharedPreferences =
                context.getSharedPreferences("hrone_app_config", Context.MODE_PRIVATE)
            time = sharedPreferences.getLong("trackID${id}", 0)
        }
        Log.d("TAG", "getCurrentTime: ${time}")
        return time
    }

}
