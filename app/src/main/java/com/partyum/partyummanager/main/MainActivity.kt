package com.partyum.partyummanager.main

import android.content.Intent
import android.util.Log
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.databinding.ActivityMainBinding
import com.partyum.partyummanager.reservation.ReservationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val viewModel: MainViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    private var searchFragment: SearchFragment? = null
    private var newReservationFragment: NewReservationFragment? = null

    override fun bindData() {
        // 뷰 바인딩
        binding.mainVM = viewModel
    }

    override fun setListeners() {
        // 메인 버튼 리스너
        viewModel.command.observe(this, {
            when (it) {
                Command.NEW_RESERVATION -> {
                    viewModel.initNewReservation()
                    newReservationFragment = NewReservationFragment()
                    newReservationFragment!!.show(supportFragmentManager, newReservationFragment!!.tag)

                    // 새 예약 추가되고 얻은 키
                    viewModel.reservationKey.observe(this, { key ->
                        if (key != null) {
                            if (newReservationFragment != null) {
                                newReservationFragment!!.dismiss()
                                newReservationFragment = null
                            }
                            goToReservationActivity(key)
                        }
                    })
                }

                Command.SEARCH_RESERVATION -> {
                    viewModel.initSearch()
                    searchFragment = SearchFragment()
                    searchFragment!!.show(supportFragmentManager, searchFragment!!.tag)

                    // 검색 결과에서 선택된 예약의 키
                    viewModel.reservationKey.observe(this, { key ->
                        if (key != null) {
                            if (searchFragment != null) {
                                searchFragment!!.dismiss()
                                searchFragment = null
                            }
                            goToReservationActivity(key)
                        }
                    })
                }

                Command.RESERVATION_STATUS -> {
                    // TODO("예약 현황 기능 추가")
                    viewModel.showSnackBar(R.string.reservation_status)
                }

                Command.ADMINISTRATOR -> {
                    viewModel.showSnackBar(R.string.administrator)
                }

                else -> {}
            }
        })
    }

    private fun goToReservationActivity(reservationKey: String) {
        // 예약건들을 예약 액티비티로 보냄
        val intent = Intent(this, ReservationActivity::class.java)
        intent.putExtra(getString(R.string.main_to_reservation_intent), reservationKey)
        Log.i("main activity", "${reservationKey}를 넘겨줍니다.")
        startActivity(intent)
    }
}
