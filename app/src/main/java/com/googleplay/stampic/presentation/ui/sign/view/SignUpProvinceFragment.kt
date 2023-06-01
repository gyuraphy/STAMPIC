package com.googleplay.stampic.presentation.ui.sign.view

import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.googleplay.stampic.R
import com.googleplay.stampic.databinding.FragmentSignUpProvinceBinding
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignUpProvinceViewModel
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpProvinceFragment :
    BaseFragment<FragmentSignUpProvinceBinding>(R.layout.fragment_sign_up_province) {

    private val signUpProvinceViewModel: SignUpProvinceViewModel by viewModels()
    private val signViewModel: SignViewModel by activityViewModels()

    override fun initStartView() {
        binding.vm = signUpProvinceViewModel
    }

    override fun initDataBinding() {

    }

    override fun initAfterBinding() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_signUpProvinceFragment_to_signUpUseAgreementFragment)
        }

        binding.spProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedProvince = parent?.getItemAtPosition(position) as String
                signUpProvinceViewModel.onProvinceSelected(selectedProvince)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.tvProvinceError.isVisible = true
                binding.tvProvinceError.text = requireContext().resources.getString(R.string.sign_up_province_error)
            }
        }

        binding.btnNext.setOnClickListener {
            signViewModel.signUpProvince.value = signUpProvinceViewModel.getProvinceSelectedValue()
            Log.d("lsy","signUpProvince에 들어있는 지역?"+signViewModel.signUpProvince.value.toString())
            goToSignUpEmail()
        }
    }

    private fun goToSignUpEmail() {
        findNavController().navigate(R.id.action_signUpProvinceFragment_to_signUpEmailFragment)
    }
}