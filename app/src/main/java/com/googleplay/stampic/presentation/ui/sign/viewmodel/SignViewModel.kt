package com.googleplay.stampic.presentation.ui.sign.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor() : ViewModel() {
    val signUpName = MutableLiveData<String>()
    val signUpEmail = MutableLiveData<String>()
    val signUpPassword = MutableLiveData<String>()
}