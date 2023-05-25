package com.googleplay.stampic.data.api

import com.googleplay.stampic.BuildConfig
import com.googleplay.stampic.data.remote.model.post.ResponseSearchPlace
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/local/search/keyword.json")
    suspend fun searchPlace(
        @Header("Authorization") authorization: String = BuildConfig.GOOGLE_API_KEY,
        @Query("query") query: String
    ): Response<ResponseSearchPlace>
}