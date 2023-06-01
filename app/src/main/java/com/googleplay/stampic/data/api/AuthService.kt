package com.googleplay.stampic.data.api

import com.googleplay.stampic.data.remote.model.sign.RequestSignInData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignInData
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @POST("/auth/login")
    suspend fun loginUser(
        @Body body: RequestSignInData
    ): Response<ResponseSignInData>
}