package com.partyum.partyummanager.reservation

import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseRecyclerViewAdapter
import com.partyum.partyummanager.databinding.ItemNewDocumentBinding

class NewDocumentsRecycleViewAdapter(private val viewModel: ReservationViewModel, private val documentTypes: Array<String>): BaseRecyclerViewAdapter<ItemNewDocumentBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.item_new_document

    override fun getItemCount(): Int = documentTypes.size

    override fun setView(position: Int) {
        val splitString = documentTypes[position].split("|")
        binding.viewModel = viewModel
        binding.name = splitString[0]
        binding.type = splitString[1]
    }
}