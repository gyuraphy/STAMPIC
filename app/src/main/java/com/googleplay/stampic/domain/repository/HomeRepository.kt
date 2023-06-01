package com.googleplay.stampic.domain.repository

import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData

interface HomeRepository {
    suspend fun attrList(): ResponseAttrData
}