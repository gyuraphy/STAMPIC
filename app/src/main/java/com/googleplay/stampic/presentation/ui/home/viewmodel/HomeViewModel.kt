package com.googleplay.stampic.presentation.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.googleplay.stampic.domain.repository.HomeRepository
import com.googleplay.stampic.data.remote.model.home.ResponseAttrData
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository):
    ViewModel() {

    private val _attrList = MutableLiveData<MutableList<ResponseAttrData.Data.AttrInfo>>()
    val attrList: LiveData<MutableList<ResponseAttrData.Data.AttrInfo>> = _attrList

    private val _attrAllIdList = MutableLiveData<String>()
    val attrAllIdList: LiveData<String> = _attrAllIdList

    var attrIdList = mutableListOf<Int>()

    // 명소 목록 조회
    fun getAttrList() {
        viewModelScope.launch {
            kotlin.runCatching {
                homeRepository.attrList()
            }.onSuccess {
                _attrList.value = it.data.attrInfoList
                attrIdList = mutableListOf()
                for (i in it.data.attrInfoList.indices) {
                    attrIdList.add(it.data.attrInfoList[i].id)
                }
                _attrAllIdList.value = attrIdList.joinToString()

            }.onFailure {
                Timber.tag("error").d(it.message.toString())
            }
        }
    }

}