package com.googleplay.stampic.domain.repository

import com.googleplay.stampic.data.remote.model.sign.ResponseSignInData
import com.googleplay.stampic.data.remote.model.sign.RequestSignInData

interface AuthRepository {
    suspend fun loginUser(body: RequestSignInData): Result<ResponseSignInData>
}
