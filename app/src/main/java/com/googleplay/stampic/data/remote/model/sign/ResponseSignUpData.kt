package com.googleplay.stampic.data.remote.model.sign

import com.googleplay.stampic.data.remote.model.BaseResponse

data class ResponseSignUpData(
    val data: Data?,
) : BaseResponse() {
    data class Data(
        val id: Int,
        val email: String,
        val name: String,
        val nickName: String,
        val birth: String,
        val gender: String,
        val province: String,
        val atk: String,
    )
}