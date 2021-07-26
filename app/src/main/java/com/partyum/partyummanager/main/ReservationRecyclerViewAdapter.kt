package com.partyum.partyummanager.main

import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseRecyclerViewAdapter
import com.partyum.partyummanager.databinding.ItemReservationBinding
import com.partyum.partyummanager.reservation.ReservationViewModel

class ReservationRecyclerViewAdapter(private val viewModel: MainViewModel) : BaseRecyclerViewAdapter<ItemReservationBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.item_reservation

    override fun getItemCount(): Int {
        // 예약건수 반환
        return if (viewModel.reservations.value != null) {
            viewModel.reservations.value!!.size
        } else {
            0
        }
    }

    override fun setView(position: Int) {
        // 뷰 바인딩, 위치 넣어주기
        binding.viewModel = viewModel
        binding.position = position
        binding.reservationKey = viewModel.reservations.value!![position].first
    }
}