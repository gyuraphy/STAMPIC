package com.googleplay.stampic.data.api

import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.mypage.ResponseMyInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MyPageService {

    @GET("/auth/my/info")
    suspend fun getMyInfo(): ResponseMyInfo

    @Multipart
    @PATCH("/auth")
    suspend fun modifyProfile(
        @PartMap data: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part?
    ): BaseResponse

    @DELETE("/auth")
    suspend fun deleteAccount(): BaseResponse
}