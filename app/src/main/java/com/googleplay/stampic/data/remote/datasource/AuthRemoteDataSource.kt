package com.googleplay.stampic.data.remote.datasource

import com.googleplay.stampic.data.remote.model.sign.RequestSignInData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignInData

interface AuthRemoteDataSource {
    suspend fun loginUser(body: RequestSignInData): Result<ResponseSignInData>
}