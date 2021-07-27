package com.partyum.partyummanager.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.databinding.FragmentNewDocumentsBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewDocumentFragment : BaseFragment<FragmentNewDocumentsBinding>() {
    override val viewModel: ReservationViewModel by sharedViewModel()
    override val layoutResourceId: Int
        get() = R.layout.fragment_new_documents

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val documentTemplates = resources.getStringArray(R.array.document_templates)
        val adapter = NewDocumentsRecycleViewAdapter(viewModel, documentTemplates)
        binding.rvNewDocumentTemplates.setHasFixedSize(true)
        binding.rvNewDocumentTemplates.layoutManager = LinearLayoutManager(this.context)
        binding.rvNewDocumentTemplates.adapter = adapter

        return view
    }

    override fun bindData() {
        // 뷰 바인딩
        binding.viewModel = viewModel
    }

    override fun setListeners() {

    }
}