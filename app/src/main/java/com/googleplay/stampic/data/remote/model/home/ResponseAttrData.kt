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
            val title: String,
            val contents: String,
            val image: String,
            val thumb: String,
            val addr: String,
            val gugunNm: String,
            val lat: Float,
            val lng: Float
        )
    }
}
