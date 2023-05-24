package com.googleplay.stampic.presentation.ui.sign.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.googleplay.stampic.R
import com.googleplay.stampic.presentation.ui.sign.viewmodel.SignUpGenderViewModel

class SignUpGenderFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpGenderFragment()
    }

    private lateinit var viewModel: SignUpGenderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_gender, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignUpGenderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}