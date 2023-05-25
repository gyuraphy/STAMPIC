package com.googleplay.stampic.data.remote.datasource

import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.sign.RequestSignUpData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignUpData

interface SignUpDataSource {

    suspend fun emailCheck(
        email: String
    ): BaseResponse

    suspend fun signUp(
        body: RequestSignUpData
    ): ResponseSignUpData
}