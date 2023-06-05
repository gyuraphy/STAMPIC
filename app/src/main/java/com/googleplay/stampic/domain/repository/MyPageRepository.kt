package com.googleplay.stampic.domain.repository

import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.mypage.ResponseMyInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface MyPageRepository {

    suspend fun getMyInfo(): ResponseMyInfo

    suspend fun modifyProfile(
        data: HashMap<String, RequestBody>,
        image: MultipartBody.Part?,
    ): BaseResponse

    suspend fun deleteAccount(): BaseResponse
}