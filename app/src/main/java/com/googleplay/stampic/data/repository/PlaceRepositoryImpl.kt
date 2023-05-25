package com.googleplay.stampic.data.repository

import com.googleplay.stampic.data.remote.datasource.PlaceRemoteDataSource
import com.googleplay.stampic.data.remote.model.post.ResponseSearchPlace
import com.googleplay.stampic.domain.repository.PlaceRepository
import com.googleplay.stampic.presentation.di.OtherHttpClient
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(@OtherHttpClient private val placeRemoteDataSource: PlaceRemoteDataSource) :
    PlaceRepository {

    override suspend fun searchPlace(
        query: String,
    ): Result<ResponseSearchPlace> {
        return placeRemoteDataSource.searchPlace(query = query)
    }
}