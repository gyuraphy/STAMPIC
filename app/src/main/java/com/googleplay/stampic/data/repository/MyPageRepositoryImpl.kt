package com.googleplay.stampic.data.repository

import com.googleplay.stampic.data.remote.datasource.MyPageDataSource
import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.mypage.ResponseMyInfo
import com.googleplay.stampic.domain.repository.MyPageRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(private val myPageDataSource: MyPageDataSource):
    MyPageRepository {

    override suspend fun getMyInfo(): ResponseMyInfo {
        return myPageDataSource.getMyInfo()
    }

    override suspend fun modifyProfile(
        data: HashMap<String, RequestBody>,
        image: MultipartBody.Part?,
    ): BaseResponse {
        return myPageDataSource.modifyProfile(data, image)
    }

    override suspend fun deleteAccount(): BaseResponse {
        return myPageDataSource.deleteAccount()
    }
}