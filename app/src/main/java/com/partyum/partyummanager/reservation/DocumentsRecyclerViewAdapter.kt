package com.partyum.partyummanager.reservation

import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseRecyclerViewAdapter
import com.partyum.partyummanager.databinding.ItemDocumentBinding

class DocumentsRecyclerViewAdapter(private val viewModel: ReservationViewModel) : BaseRecyclerViewAdapter<ItemDocumentBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.item_document

    override fun getItemCount(): Int {
        // 문서 수 반환
        return if (viewModel.reservation.value?.docs != null) {
            viewModel.reservation.value!!.docs.size
        } else {
            0
        }
    }

    override fun setView(position: Int) {
        // 뷰 바인딩, 위치값 넣어주기
        binding.viewModel = viewModel
        binding.position = position
    }
}