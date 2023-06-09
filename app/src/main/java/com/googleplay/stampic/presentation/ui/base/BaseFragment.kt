package com.googleplay.stampic.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.googleplay.stampic.presentation.util.LoadingDialog
import timber.log.Timber

abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes val layout: Int) : Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layout, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStartView()
        initDataBinding()
        initAfterBinding()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
        _binding = null
    }

    abstract fun initStartView()

    abstract fun initDataBinding()

    abstract fun initAfterBinding()

    fun showLoadingDialog() {
        mLoadingDialog.show()
    }

    fun dismissLoadingDialog() {
        mLoadingDialog.dismiss()
    }

    protected inner class LifeCycleEventLogger(private val className: String) : LifecycleObserver {
        fun registerLogger(lifecycle: Lifecycle) {
            lifecycle.addObserver(this)
        }

        fun log() {
            Timber.d("${className}LifeCycleEvent", "${lifecycle.currentState}")
        }
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }



}