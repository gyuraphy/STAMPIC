package com.googleplay.stampic.presentation.ui.sign.view

import android.util.Patterns
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.googleplay.stampic.R
import com.googleplay.stampic.databinding.FragmentSignUpEmailBinding
import com.googleplay.stampic.domain.model.DialogInfo
import com.googleplay.stampic.presentation.ui.base.BaseFragment
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignUpEmailViewModel
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignViewModel
import com.googleplay.stampic.presentation.util.DialogFragmentUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpEmailFragment :
    BaseFragment<FragmentSignUpEmailBinding>(R.layout.fragment_sign_up_email) {
    private val signUpEmailViewModel: SignUpEmailViewModel by viewModels()
    private val signViewModel: SignViewModel by activityViewModels()

    override fun initStartView() {
        binding.vm = signUpEmailViewModel
    }

    override fun initDataBinding() {
        signUpEmailViewModel.isExisted.observe(viewLifecycleOwner) {
            if (it) {
                signViewModel.signUpEmail.value = signUpEmailViewModel.signUpEmail.value
                goToSignUpPassword()
            } else {
                showErrorDialog()
            }
        }
    }

    override fun initAfterBinding() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_signUpEmailFragment_to_signUpProvinceFragment)
        }

        binding.btnNext.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signUpEmailViewModel.emailCheck(email)
            } else {
                binding.tvEmailError.isVisible = true
            }
        }
    }

    private fun showErrorDialog() {
        val dialog = DialogFragmentUtil(
            DialogInfo(
                null,
                resources.getString(R.string.sign_up_email_dialog_error),
                null,
                resources.getString(R.string.sign_in_confirm)
            )
        ) {}

        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun goToSignUpPassword() {
        findNavController().navigate(R.id.action_signUpEmailFragment_to_signUpPasswordFragment)
    }
}