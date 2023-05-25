package com.googleplay.stampic.data.remote.datasource

import com.googleplay.stampic.data.remote.model.post.ResponseSearchPlace

interface PlaceRemoteDataSource {
    suspend fun searchPlace(query: String): Result<ResponseSearchPlace>
}