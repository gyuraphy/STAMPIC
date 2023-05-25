package com.googleplay.stampic.data.repository

import com.googleplay.stampic.data.remote.datasource.SignUpDataSource
import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.sign.RequestSignUpData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignUpData
import com.googleplay.stampic.domain.repository.SignUpRepository
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(private val signUpDataSource: SignUpDataSource) :
    SignUpRepository {

    override suspend fun emailCheck(email: String): BaseResponse {
        return signUpDataSource.emailCheck(email)
    }

    override suspend fun signUp(body: RequestSignUpData): ResponseSignUpData {
        return signUpDataSource.signUp(body)
    }
}