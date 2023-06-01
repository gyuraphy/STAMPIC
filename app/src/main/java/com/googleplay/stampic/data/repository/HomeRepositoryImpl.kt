package com.googleplay.stampic.data.repository

import com.googleplay.stampic.data.remote.datasource.HomeDataSource
import com.googleplay.stampic.data.remote.model.BaseResponse
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData
import com.googleplay.stampic.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(private val homeDataSource: HomeDataSource) :
    HomeRepository {

    override suspend fun attrList(): ResponseAttrData {
        return homeDataSource.attrList()
    }

}