package com.moontv.application.network

import com.moontv.application.model.SubtitleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

//Subtitle end point
interface ApiInterface {
        @GET("/api/subtitles/by/episode/{id}/4F5A9C3D9A86FA54EACEDDD635185/4F5A9C3D9A86FA54EACEDDD635185/")
    suspend fun getSubtitle(@Path("id") id: Int): Response<List<SubtitleResponse>>
}