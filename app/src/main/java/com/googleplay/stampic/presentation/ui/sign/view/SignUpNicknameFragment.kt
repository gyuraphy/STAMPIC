package com.googleplay.stampic.presentation.ui.sign.view

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.googleplay.stampic.R
import com.googleplay.stampic.data.remote.model.sign.RequestSignUpData
import com.googleplay.stampic.databinding.FragmentSignUpNicknameBinding
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignUpNicknameViewModel
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpNicknameFragment :
    BaseFragment<FragmentSignUpNicknameBinding>(R.layout.fragment_sign_up_nickname) {
    private val signUpNicknameViewModel: SignUpNicknameViewModel by viewModels()
    private val signViewModel: SignViewModel by activityViewModels()

    override fun initStartView() {
        binding.vm = signUpNicknameViewModel
    }

    override fun initDataBinding() {
        signUpNicknameViewModel.textCount.observe(viewLifecycleOwner) {
            if (it in 0..14) {
                binding.tvCount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray600
                    )
                )
            } else {
                binding.tvNicknameError.isVisible = true
                binding.tvNicknameError.text =
                    requireContext().resources.getString(R.string.sign_up_nickname_count_error)
                binding.tvCount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_eb0555
                    )
                )
            }
        }

        signUpNicknameViewModel.signUpSuccess.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_signUpNicknameFragment_to_signUpWelcomeFragment)
            }
        }
    }

    override fun initAfterBinding() {
        val hintList = requireContext().resources.getStringArray(R.array.nickname_hint).toList()
        binding.etNickname.hint = hintList.shuffled().take(1).joinToString()

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_signUpNicknameFragment_to_signUpGenderFragment)
        }

        binding.btnNext.setOnClickListener {
            Log.d("lsy", "넘기려는 데이터 중 province는 담겼을까?"+signViewModel.signUpProvince.value.toString())
            signUpNicknameViewModel.signUp(
                RequestSignUpData(
                    name = signViewModel.signUpName.value.toString(),
                    email = signViewModel.signUpEmail.value.toString(),
                    password = signViewModel.signUpPassword.value.toString(),
                    nickName = signUpNicknameViewModel.signUpNickname.value.toString(),
                    birth = signViewModel.signUpBirth.value.toString(),
                    gender = signViewModel.signUpGender.value.toString(),
                    province = signViewModel.signUpProvince.value.toString(),
                )
            )
        }
    }
}