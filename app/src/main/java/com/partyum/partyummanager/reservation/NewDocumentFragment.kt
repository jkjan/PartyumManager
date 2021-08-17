package com.partyum.partyummanager.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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

        val documentTypes = resources.getStringArray(R.array.document_types)
        val adapter = NewDocumentsRecycleViewAdapter(viewModel, documentTypes)
        binding.rvNewDocumentTypes.setHasFixedSize(true)
        binding.rvNewDocumentTypes.layoutManager = LinearLayoutManager(this.context)
        binding.rvNewDocumentTypes.adapter = adapter

        return view
    }

    override fun bindData() {
        // 뷰 바인딩
        binding.viewModel = viewModel
    }

    override fun setListeners() {

    }
}