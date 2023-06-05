package com.googleplay.stampic.data.remote.model.sign

import com.googleplay.stampic.data.remote.model.BaseResponse

data class ResponseSignInData(
    val data: ResultSignIn?
) : BaseResponse() {
    data class ResultSignIn(
        val id: Int,
        val email: String,
        val nickName: String,
        val province: String,
        val profileImageUrl: String,
        val birth: String,
        val accessToken: String,
    )
}