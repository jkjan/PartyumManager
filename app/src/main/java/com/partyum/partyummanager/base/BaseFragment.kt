package com.partyum.partyummanager.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseFragment <T: ViewDataBinding> : DialogFragment() {
    lateinit var binding: T
    abstract val viewModel: BaseViewModel
    abstract val layoutResourceId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)

        super.onCreateView(inflater, container, savedInstanceState)
        bindData()
        setListeners()

        return binding.root
    }

    // 데이터바인딩
    abstract fun bindData()

    // 리스너 달기
    abstract fun setListeners()
}