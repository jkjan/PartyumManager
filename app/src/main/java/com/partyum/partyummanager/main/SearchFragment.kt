package com.partyum.partyummanager.main

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.databinding.FragmentSearchBinding
import com.partyum.partyummanager.model.PhoneFormattingTextWatcher
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val viewModel: MainViewModel by sharedViewModel()
    override val layoutResourceId: Int
        get() = R.layout.fragment_search

    override fun bindData() {
        // 뷰 바인딩
        binding.mainVM = viewModel
    }

    override fun setListeners() {
        viewModel.command.observe(this, {
            // 사용자가 검색 버튼을 클릭
            when (it) {
                Command.NUMBER_SUBMITTED -> {
                    search()
                }
                else -> {}
            }
        })

//        binding.etInputNumber.setOnKeyListener { _, keyCode, event ->
//            // 사용자가 엔터를 누름
//            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                search()
//                true
//            } else
//                false
//        }

        viewModel.notFoundCount.observe(this, {
            // 전화번호를 신랑, 신부 모두에서 찾아도 없음
            binding.tvSearchNotFound.visibility = if (it >= 2) {
                binding.tvSearchNotFound.text = getString(R.string.search_not_found)
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        viewModel.invalidNumber.observe(this, {
            // 전화번호가 유효하지 않음
            binding.tvSearchNotFound.visibility = if (it) {
                binding.tvSearchNotFound.text = getString(R.string.search_invalid_number)
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        viewModel.reservations.observe(this, {
            if (it.isNotEmpty()) {
                // 예약이 검색됨
                binding.llReservations.visibility = View.VISIBLE
                binding.rvReservationSelect.setHasFixedSize(true)
                binding.rvReservationSelect.layoutManager = LinearLayoutManager(this.context)
                binding.rvReservationSelect.adapter = ReservationRecyclerViewAdapter(viewModel)
            }
        })
    }

    private fun init() {
        // 프래그먼트 정보 초기화
        binding.llReservations.visibility = View.GONE
        viewModel.reservations.value = listOf()
        viewModel.notFoundCount.value = 0
        viewModel.invalidNumber.value = false
    }

    fun search() {
        init()

        viewModel.getReservations(
            getString(R.string.phone_regex).toRegex(),
            binding.etInputNumber.text.toString()
        )
            .observe(this, {
                if (binding.rvReservationSelect.adapter != null) {
                    binding.rvReservationSelect.adapter!!.notifyDataSetChanged()
                    viewModel.showSnackBar(R.string.data_retrieved)
                }
            })
    }
}