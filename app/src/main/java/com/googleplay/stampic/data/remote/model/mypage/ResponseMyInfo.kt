package com.googleplay.stampic.data.remote.model.mypage

import com.googleplay.stampic.data.remote.model.BaseResponse

data class ResponseMyInfo(
    val data: Data,
) : BaseResponse() {
    data class Data(
        val id: Int,
        val email: String,
        val name: String,
        val nickName: String,
        val stampCount: String,
        val stampAttrList: List<Int>,
    )
}
