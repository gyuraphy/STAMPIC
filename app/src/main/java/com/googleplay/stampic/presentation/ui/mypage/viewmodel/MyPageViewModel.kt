package com.googleplay.stampic.presentation.ui.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.googleplay.stampic.data.remote.model.mypage.ResponseMyInfo
import com.googleplay.stampic.domain.repository.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val myPageRepository: MyPageRepository) :
    ViewModel() {

    private val _myInfoData = MutableLiveData<ResponseMyInfo.Data>()
    val myInfoData: LiveData<ResponseMyInfo.Data> = _myInfoData

    private val _stampCount = MutableLiveData<String>()
    val stampCount: LiveData<String> = _stampCount

    fun getMyInfo() {
        viewModelScope.launch {
            kotlin.runCatching {
                myPageRepository.getMyInfo()
            }.onSuccess {
                Log.d("dd","getMyInfo 성공?")

                _myInfoData.value = it.data
                _stampCount.value = it.data.stampCount // 사용자 정보에서 stampCount 값을 가져와서 업데이트
//                _stampAttrList.value = it.data.stampAttrList
            }.onFailure {
                Timber.tag("error").d(it.message.toString())
            }
        }
    }
}