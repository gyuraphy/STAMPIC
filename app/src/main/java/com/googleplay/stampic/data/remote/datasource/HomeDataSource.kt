package com.googleplay.stampic.data.remote.datasource

import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData

interface HomeDataSource {

    suspend fun attrList(): ResponseAttrData

}