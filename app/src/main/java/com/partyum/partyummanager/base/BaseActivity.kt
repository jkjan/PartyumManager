package com.partyum.partyummanager.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar


// 액티비티에서 공통적으로 행해지는 것들
abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity() {

    lateinit var binding: T
    abstract val viewModel: BaseViewModel
    abstract val layoutResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Log.i("main", "no dark mode")
        binding = DataBindingUtil.setContentView(this, layoutResourceId)
        binding.lifecycleOwner = this

        super.onCreate(savedInstanceState)
        snackBarObserving()
        bindData()
        viewModel.init()
        setListeners()
    }

    // 데이터바인딩
    abstract fun bindData()

    // 리스너 달기
    abstract fun setListeners()

    // 스낵바 관찰 시작
    private fun snackBarObserving() {
        viewModel.observeSnackBarMessage(this) {
            Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_LONG).show()
        }
        viewModel.observeSnackBarMessageStr(this) {
            Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_LONG).show()
        }
    }
}