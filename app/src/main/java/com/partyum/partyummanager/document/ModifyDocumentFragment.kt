package com.partyum.partyummanager.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.databinding.FragmentModifyDocumentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ModifyDocumentFragment : BaseFragment<FragmentModifyDocumentBinding>() {
    override val viewModel: DocumentViewModel by sharedViewModel()
    override val layoutResourceId: Int
        get() = R.layout.fragment_modify_document

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        binding.etModifyingDocumentName.setText(viewModel.documentInfo.value!!.name)
        binding.etModifyingCreatedDateTime.setText(viewModel.documentInfo.value!!.createdDateTime)
        binding.invalidDate.visibility = View.GONE

        return view
    }

    override fun bindData() {
        binding.viewModel = viewModel
    }

    override fun setListeners() {
        viewModel.command.observe(this, {
            when(it) {
                Command.MODIFIED_DOCUMENT_SUBMITTED-> {
                    viewModel.modifyDocumentInfo(binding.etModifyingDocumentName.text.toString(), binding.etModifyingCreatedDateTime.text.toString())
                }
                else -> {}
            }
        })

        viewModel.invalidDate.observe(this, {
            if (it) {
                // 날짜가 유효하지 않음
                binding.invalidDate.visibility = View.VISIBLE
            }
        })
    }
}