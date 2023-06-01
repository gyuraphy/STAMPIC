package com.googleplay.stampic.presentation.ui.sign.view

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.googleplay.stampic.R
import com.googleplay.stampic.databinding.FragmentSignUpBirthBinding
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignUpBirthViewModel
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpBirthFragment :
    BaseFragment<FragmentSignUpBirthBinding>(R.layout.fragment_sign_up_birth) {
    private val signUpBirthViewModel: SignUpBirthViewModel by viewModels()
    private val signViewModel: SignViewModel by activityViewModels()
    override fun initStartView() {
        binding.vm = signUpBirthViewModel
    }
    override fun initDataBinding() {

    }
    override fun initAfterBinding() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_signUpBirthFragment_to_signUpPasswordFragment)
        }

        binding.btnNext.setOnClickListener {
            Log.d("lsy", "버스다음클릭!")
            if (signUpBirthViewModel.textCount.value == 8) {
                val edtBirth = binding.etBirth.text.toString()
                val pattern = Pattern.compile("^(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\$")
                val isSuccess = pattern.matcher(edtBirth).find()
                if (isSuccess) {
                    signViewModel.signUpBirth.value =
                        signUpBirthViewModel.signUpBirth.value
                    goToSignUpNickname()
                } else {
                        binding.tvBirthError.isVisible = true
                        binding.tvBirthError.text =
                        requireContext().resources.getString(R.string.sign_up_birth_error)
                }
            } else {
                binding.tvBirthError.isVisible = true
                binding.tvBirthError.text =
                    requireContext().resources.getString(R.string.sign_up_birth_length_error)
            }
        }
    }
    private fun goToSignUpNickname() {
        findNavController().navigate(R.id.action_signUpBirthFragment_to_signUpGenderFragment)
    }
}