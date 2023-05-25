package com.googleplay.stampic.data.remote.datasource

import com.googleplay.stampic.data.api.PlaceService
import com.googleplay.stampic.data.remote.model.post.ResponseSearchPlace
import com.googleplay.stampic.presentation.di.OtherHttpClient
import javax.inject.Inject

class PlaceRemoteDataSourceImpl @Inject constructor(@OtherHttpClient private val placeService: PlaceService) :
    PlaceRemoteDataSource {

    override suspend fun searchPlace(query: String): Result<ResponseSearchPlace> {
        val response = placeService.searchPlace(query=query)
        if (response.isSuccessful) {
            response.body()?.let { return Result.success(it) }
        }
        return Result.failure(IllegalStateException(response.message()))
    }
}