package com.partyum.partyummanager.main

import android.content.Intent
import android.util.Log
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.databinding.ActivityMainBinding
import com.partyum.partyummanager.reservation.ReservationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val viewModel: MainViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    private var fragment: SearchFragment? = null

    override fun bindData() {
        // 뷰 바인딩
        binding.mainVM = viewModel
    }

    override fun setListeners() {
        viewModel.command.observe(this, {
            when (it) {
                // 새 예약
                Command.NEW_RESERVATION -> {
                    // TODO("예약 생성 기능 추가")
                }
                // 검색
                Command.SEARCH -> {
                    fragment = SearchFragment()
                    fragment!!.show(supportFragmentManager, fragment!!.tag)
                }
                // 예약 현황
                Command.RESERVATION_STATUS -> {
                    // TODO("예약 현황 기능 추가")
                }
                // 관리자 기능
                Command.ADMINISTRATOR -> {
                    viewModel.showSnackBar(R.string.administrator)
                }
                else -> {}
            }
        })

        viewModel.reservations.observe(this, {
            // 검색된 예약건이 하나 이상
            if (it.isNotEmpty()) {
                // 프래그먼트를 닫고 삭제함
                if (fragment != null) {
                    fragment!!.dismiss()
                    fragment = null
                }

                Log.i("main activity", "${it.size}건의 예약을 찾았습니다.")

                // 예약건들을 예약 액티비티로 보냄
                val intent = Intent(this, ReservationActivity::class.java)
                intent.putExtra(getString(R.string.main_to_reservation_intent), viewModel.reservations.value!!)
                startActivity(intent)
            }
        })
    }
}
