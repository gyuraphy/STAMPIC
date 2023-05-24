package com.googleplay.stampic.domain.repository

import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.sign.RequestSignUpData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignUpData

interface SignUpRepository {

    suspend fun emailCheck(
        email: String,
    ): BaseResponse

    suspend fun signUp(
        body: RequestSignUpData,
    ): ResponseSignUpData
}