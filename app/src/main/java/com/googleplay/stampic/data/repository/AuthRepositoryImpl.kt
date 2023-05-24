package com.googleplay.stampic.data.repository

import com.googleplay.stampic.data.remote.model.sign.ResponseSignInData
import com.googleplay.stampic.data.remote.datasource.AuthRemoteDataSource
import com.googleplay.stampic.data.remote.model.sign.RequestSignInData
import com.googleplay.stampic.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authRemoteDataSource: AuthRemoteDataSource) :
    AuthRepository {

    override suspend fun loginUser(body: RequestSignInData): Result<ResponseSignInData> {
        return authRemoteDataSource.loginUser(body = body)
    }
}