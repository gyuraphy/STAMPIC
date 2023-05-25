package com.googleplay.stampic.data.remote.datasource

import com.googleplay.stampic.data.api.SignUpService
import com.googleplay.stampic.data.remote.datasource.SignUpDataSource
import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.sign.RequestSignUpData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignUpData
import javax.inject.Inject

class SignUpDataSourceImpl @Inject constructor(private val signUpService: SignUpService) :
    SignUpDataSource {

    override suspend fun emailCheck(email: String): BaseResponse {
        return signUpService.emailCheck(email)
    }

    override suspend fun signUp(body: RequestSignUpData): ResponseSignUpData {
        return signUpService.signUp(body)
    }
}