package com.googleplay.stampic.presentation.ui.sign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpProvinceViewModel @Inject constructor() : ViewModel() {
    private val signUpProvince = MutableLiveData<String>()

    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> = _isEnabled

    fun onProvinceSelected(selectedProvince: String) {
        signUpProvince.value = selectedProvince
        _isEnabled.value = !selectedProvince.isNullOrEmpty()
    }

    fun getProvinceSelectedValue(): String? {
        return signUpProvince.value
    }
}