package com.googleplay.stampic.data.api

import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.sign.RequestSignUpData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignUpData
import retrofit2.http.*

interface SignUpService {

    @GET("/auth/check")
    suspend fun emailCheck(
        @Query("email") email: String,
    ): BaseResponse

    @POST("/auth")
    suspend fun signUp(
        @Body body: RequestSignUpData,
    ): ResponseSignUpData
}