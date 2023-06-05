package com.googleplay.stampic.data.remote.datasource

import com.googleplay.stampic.data.api.MyPageService
import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.mypage.ResponseMyInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MyPageDataSourceImpl @Inject constructor(private val myPageService: MyPageService):
    MyPageDataSource {

    override suspend fun getMyInfo(): ResponseMyInfo {
        return myPageService.getMyInfo()
    }

    override suspend fun modifyProfile(
        data: HashMap<String, RequestBody>,
        image: MultipartBody.Part?,
    ): BaseResponse {
        return myPageService.modifyProfile(data, image)
    }

    override suspend fun deleteAccount(): BaseResponse {
        return myPageService.deleteAccount()
    }
}