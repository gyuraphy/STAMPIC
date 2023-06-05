package com.googleplay.stampic.presentation.ui.sign.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.googleplay.stampic.data.local.AuthLocalDataSource
import com.googleplay.stampic.data.remote.model.sign.RequestSignInData
import com.googleplay.stampic.data.remote.model.sign.ResponseSignInData
import com.googleplay.stampic.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
    private val authRepository: AuthRepository
) : ViewModel() {
    val id = MutableLiveData<String>()
    val pw = MutableLiveData<String>()

    private val _signInData = MutableLiveData<ResponseSignInData>()
    var signInData: LiveData<ResponseSignInData> = _signInData

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSignInData() {
        Log.d("dd","getSignInData() 실행")
        _isLoading.value = true
        viewModelScope.launch {
            authRepository.loginUser(RequestSignInData(id.value!!, pw.value!!)).onSuccess {
                Log.d("dd","getSignInData() onSuccess 실행")
                Log.d("dd","it의 값: "+it.toString())
                _signInData.value = it
                Log.d("dd","_signInData.value의 값: "+_signInData.value.toString())
                it.data?.let { result ->
                    saveAccessToken(result.accessToken)
                    Log.d("dd","atk 가 정상적으로 들어왔는지?"+result.accessToken)
                    saveUserId(result.id)
                }
                _isLoading.value = false
            }.onFailure {
                Timber.tag("getSignInData error").d(it.message.toString())
            }
        }
    }

    fun     saveAccessToken(value: String) {
        authLocalDataSource.accessToken = value
    }

    fun saveAutoLoginState(value: Boolean) {
        authLocalDataSource.isAutoLogin = value
    }

    fun saveUserId(value: Int) {
        authLocalDataSource.userId = value
    }
}