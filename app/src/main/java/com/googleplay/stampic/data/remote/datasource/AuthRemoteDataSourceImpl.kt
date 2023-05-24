package com.googleplay.stampic.data.remote.datasource

import com.googleplay.stampic.data.api.AuthService
import com.googleplay.stampic.data.remote.model.sign.RequestSignInData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignInData
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(private val authService: AuthService) :
    AuthRemoteDataSource {

    override suspend fun loginUser(body: RequestSignInData): Result<ResponseSignInData> {
        val response = authService.loginUser(body = body)
        if (response.isSuccessful) {
            response.body()?.let { return Result.success(it) }
        }
        return Result.failure(IllegalStateException(response.message()))
    }
}