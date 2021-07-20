package com.partyum.partyummanager.document

import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.databinding.ActivityDocumentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class DocumentActivity : BaseActivity<ActivityDocumentBinding>() {
    override val viewModel: DocumentViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_document

    override fun bindData() {
        binding.documentVM = viewModel
    }

    override fun setListeners() {
    }
}