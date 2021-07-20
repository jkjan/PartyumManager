package com.partyum.partyummanager.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.databinding.ReservationSelectFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

// 메인 액티비티에서 검색된 예약건수가 하나 이상일 시 예약 액티비티에서 띄우는 예약 선택 프래그먼트
class ReservationSelectFragment : BaseFragment<ReservationSelectFragmentBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.reservation_select_fragment
    override val viewModel: ReservationViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // 뷰 바인딩
        binding.viewModel = viewModel

        // 검색된 예약건들을 띄움
        val adapter = ReservationRecyclerViewAdapter(viewModel)
        binding.rvReservationSelect.setHasFixedSize(true)
        binding.rvReservationSelect.layoutManager = LinearLayoutManager(this.context)
        binding.rvReservationSelect.adapter = adapter

        return view
    }

}