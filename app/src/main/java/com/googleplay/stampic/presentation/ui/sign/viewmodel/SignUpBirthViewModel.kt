package com.googleplay.stampic.presentation.ui.sign.viewmodel

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpBirthViewModel @Inject constructor() : ViewModel() {
    val signUpBirth = MutableLiveData<String>()

    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> = _isEnabled

    private val _textCount = MutableLiveData(0)
    val textCount: LiveData<Int> = _textCount

    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            _textCount.value = p0?.length
            _isEnabled.value = p0?.length!! > 0
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

}