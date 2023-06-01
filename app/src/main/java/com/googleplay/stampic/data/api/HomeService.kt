package com.googleplay.stampic.data.api

import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData
import retrofit2.http.*

interface HomeService {

    @GET("/home")
    suspend fun attrList(): ResponseAttrData

}