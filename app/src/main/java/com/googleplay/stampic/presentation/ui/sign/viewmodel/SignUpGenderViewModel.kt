package com.googleplay.stampic.presentation.ui.sign.viewmodel

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.googleplay.stampic.R
import javax.inject.Inject

class SignUpGenderViewModel @Inject constructor() : ViewModel() {
    val signUpGender = MutableLiveData<String>()
    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> = _isEnabled

    // 성별 정보를 저장할 ObservableBoolean
    val isMaleSelected = ObservableBoolean(false)
    val isFemaleSelected = ObservableBoolean(false)

    // 선택한 성별 정보를 저장할 변수
//    private var selectedGender: String? = null

    private val _selectedGender = MutableLiveData<String>()
    val selectedGender: LiveData<String> = _selectedGender

    init {
        // 초기 상태 설정
    }
    // 성별 선택 이벤트 핸들러
    fun onGenderSelected(isMale: Boolean) {
        isMaleSelected.set(isMale)
        isFemaleSelected.set(!isMale)
        _selectedGender.value = if (isMale) "남자" else "여자"
        Log.d("lsy","${_selectedGender.value}")
        updateButtonStates(isMale)
    }

    // 선택한 성별 정보를 반환하는 메서드
    fun getSelectedGenderValue(): String? {
        return selectedGender.value
    }

    private fun updateButtonStates(isMale: Boolean) {
        // 버튼 참조 초기화

        // isEnabled 값 업데이트
        _isEnabled.value = _selectedGender.value == if (isMale) "남자" else "여자"
        Log.d("lsy", "궁금해요 isEnabled의 값"+"${_isEnabled.value.toString()}")
    }
}