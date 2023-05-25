package com.googleplay.stampic.domain.repository

import com.googleplay.stampic.data.remote.model.post.ResponseSearchPlace

interface PlaceRepository {

    suspend fun searchPlace(query: String): Result<ResponseSearchPlace>
}
