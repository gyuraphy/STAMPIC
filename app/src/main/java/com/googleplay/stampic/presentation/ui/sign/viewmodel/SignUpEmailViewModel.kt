package com.googleplay.stampic.presentation.ui.sign.viewmodel

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.googleplay.stampic.domain.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpEmailViewModel @Inject constructor(private val signUpRepository: SignUpRepository) :
    ViewModel() {
    val signUpEmail = MutableLiveData<String>()

    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> = _isEnabled

    private val _isExisted = MutableLiveData<Boolean>()
    val isExisted: LiveData<Boolean> = _isExisted

    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            _isEnabled.value = p0?.length!! > 0
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    fun emailCheck(email: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                signUpRepository.emailCheck(email)
            }.onSuccess {
                _isExisted.value = it.code == 0
            }.onFailure {
                Timber.tag("error").d(it.message.toString())
            }
        }
    }
}