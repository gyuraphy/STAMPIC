package com.googleplay.stampic.presentation.ui.sign.view

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.googleplay.stampic.R
import com.googleplay.stampic.databinding.FragmentSignUpGenderBinding
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignUpGenderViewModel
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignViewModel

class SignUpGenderFragment :
    BaseFragment<FragmentSignUpGenderBinding>(R.layout.fragment_sign_up_gender) {
    private val signUpGenderViewModel: SignUpGenderViewModel by viewModels()
    private val signViewModel: SignViewModel by activityViewModels()
    override fun initStartView() {
        binding.vm = signUpGenderViewModel
    }

    override fun initDataBinding() {

    }

    override fun initAfterBinding() {

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_signUpGenderFragment_to_signUpBirthFragment)
        }

        // 성별 선택 버튼 클릭 리스너 설정
        binding.btnMale.setOnClickListener {
            Log.d("lsy","남자클릭!")
            signUpGenderViewModel.onGenderSelected(true)
        }

        binding.btnFemale.setOnClickListener {
            Log.d("lsy","여자클릭!")
            signUpGenderViewModel.onGenderSelected(false)
        }

        signUpGenderViewModel.isEnabled.observe(viewLifecycleOwner) { isEnabled ->
            if (isEnabled) {
                binding.btnMale.setBackgroundResource(R.drawable.bt_stroke_selected)
                binding.btnMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                binding.btnFemale.setBackgroundResource(R.drawable.bt_stroke)
                binding.btnFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.sub_color))
            } else {
                binding.btnMale.setBackgroundResource(R.drawable.bt_stroke)
                binding.btnMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.sub_color))
                binding.btnFemale.setBackgroundResource(R.drawable.bt_stroke_selected)
                binding.btnFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
            }
        }

        binding.btnNext.setOnClickListener {
            val selectedGender = signUpGenderViewModel.getSelectedGenderValue()

            if (selectedGender.isNullOrEmpty()) {
                binding.tvGenderError.isVisible = true
            } else {
                // 선택한 성별 데이터를 SignUpViewModel의 signUpGender 변수에 저장
                signViewModel.signUpGender.value = selectedGender

                // 다음 프래그먼트로 이동
                goToSignUpNickName()
            }
        }
    }
    private fun goToSignUpNickName() {
        findNavController().navigate(R.id.action_signUpGenderFragment_to_signUpNicknameFragment)
    }

}