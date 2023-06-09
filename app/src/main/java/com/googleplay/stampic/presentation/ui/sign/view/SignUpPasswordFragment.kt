package com.googleplay.stampic.presentation.ui.sign.view

import android.text.InputType
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.googleplay.stampic.R
import com.googleplay.stampic.databinding.FragmentSignUpPasswordBinding
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignUpPasswordViewModel
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpPasswordFragment :
    BaseFragment<FragmentSignUpPasswordBinding>(R.layout.fragment_sign_up_password) {
    private val signUpPasswordViewModel: SignUpPasswordViewModel by viewModels()
    private val signViewModel: SignViewModel by activityViewModels()

    override fun initStartView() {
        binding.vm = signUpPasswordViewModel
    }

    override fun initDataBinding() {
        signUpPasswordViewModel.textCount.observe(viewLifecycleOwner) {
            if (it in 8..12) {
                binding.tvCount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray600
                    )
                )
            } else {
                binding.tvCount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_eb0555
                    )
                )
            }
        }
    }

    override fun initAfterBinding() {
        binding.ivBlind.setOnClickListener {
            it.isSelected = !it.isSelected
            signUpPasswordViewModel.togglePasswordVisible()
            changeInputType()
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_signUpPasswordFragment_to_signUpEmailFragment)
        }

        binding.btnNext.setOnClickListener {
            Log.d("lsy", "패스워드다음클릭!")
            if (signUpPasswordViewModel.textCount.value in 8..12) {
                val edtPassword = binding.etPassword.text.toString()
                val pattern = Pattern.compile("^[a-zA-Z\\d!@#$%^&+=]*$")
                val isSuccess = pattern.matcher(edtPassword).find()
                if (isSuccess) {
                    signViewModel.signUpPassword.value =
                        signUpPasswordViewModel.signUpPassword.value
                    goToSignUpBirth()
                } else {
                    binding.tvPasswordError.isVisible = true
                    binding.tvPasswordError.text =
                        requireContext().resources.getString(R.string.sign_up_password_error)
                }
            } else {
                binding.tvPasswordError.isVisible = true
                binding.tvPasswordError.text =
                    requireContext().resources.getString(R.string.sign_up_password_length_error)
            }
        }
    }

    private fun changeInputType() {
        if (signUpPasswordViewModel.isPasswordVisible.value == true) {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.etPassword.setSelection(binding.etPassword.length())
    }

    private fun goToSignUpBirth() {
        findNavController().navigate(R.id.action_signUpPasswordFragment_to_signUpBirthFragment)
    }
}