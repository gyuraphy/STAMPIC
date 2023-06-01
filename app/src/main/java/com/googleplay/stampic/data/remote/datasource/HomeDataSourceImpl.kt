package com.googleplay.stampic.data.remote.datasource


import com.googleplay.stampic.data.api.HomeService
import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(private val homeService: HomeService) :
    HomeDataSource {

    override suspend fun attrList(): ResponseAttrData {
        return homeService.attrList()
    }
}