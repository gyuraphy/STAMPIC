package com.googleplay.stampic.data.remote.model.home

import com.googleplay.stampic.data.remote.model.BaseResponse

data class ResponseAttrData(
    val data: Data,
) : BaseResponse() {
    data class Data(
        val attrInfoList: MutableList<AttrInfo>
    ) {
        data class AttrInfo (
            val id: Int,
            val place: String,
            val contents: String,
            val thumb: String
        )
    }
}
