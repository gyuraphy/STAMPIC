package com.googleplay.stampic.presentation.ui.sign.view

import androidx.navigation.fragment.findNavController
import com.googleplay.stampic.R
import com.googleplay.stampic.databinding.FragmentSignUpWelcomeBinding
import com.googleplay.stampic.presentation.ui.base.BaseFragment

class SignUpWelcomeFragment :
    BaseFragment<FragmentSignUpWelcomeBinding>(R.layout.fragment_sign_up_welcome) {

    override fun initStartView() {}

    override fun initDataBinding() {}

    override fun initAfterBinding() {
        binding.btnNext.setOnClickListener { goToSignIn() }
    }

    private fun goToSignIn() {
        findNavController().navigate(R.id.action_signUpWelcomeFragment_to_signInFragment)
    }
}