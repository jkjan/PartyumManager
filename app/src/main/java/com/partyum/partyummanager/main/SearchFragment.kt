package com.partyum.partyummanager.main

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.databinding.FragmentSearchBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val viewModel: MainViewModel by sharedViewModel()
    override val layoutResourceId: Int
        get() = R.layout.fragment_search
    lateinit var adapter: ReservationRecyclerViewAdapter

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

        viewModel.invalidNumber.observe(this, {
            // 전화번호가 유효하지 않음
            binding.tvSearchNotFound.visibility = if (it) {
                binding.tvSearchNotFound.text = getString(R.string.search_invalid_number)
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }

    private fun init() {
        // 프래그먼트 정보 초기화
        binding.llReservations.visibility = View.GONE
        viewModel.invalidNumber.value = false
    }

    fun search() {
        init()

        viewModel.getReservations(
            binding.etInputNumber.text.toString()
        )
            .observe(this, {
                if (it != null) {
                    if (it.isNotEmpty()) {
                        if (binding.rvReservationSelect.adapter != null) {
//                            binding.rvReservationSelect.adapter!!.notifyDataSetChanged()
                            if (this::adapter.isInitialized) {
                                binding.rvReservationSelect.adapter = adapter
                            }
                        }
                        else {
                            binding.llReservations.visibility = View.VISIBLE
                            binding.rvReservationSelect.setHasFixedSize(true)
                            binding.rvReservationSelect.layoutManager = LinearLayoutManager(this.context)
                            binding.rvReservationSelect.adapter = ReservationRecyclerViewAdapter(viewModel)
                        }
                    }
                    else {
                        binding.tvSearchNotFound.text = getString(R.string.search_not_found)
                        binding.tvSearchNotFound.visibility = View.VISIBLE
                    }
                }
            })
    }
}