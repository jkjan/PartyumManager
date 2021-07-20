package com.partyum.partyummanager.main

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.databinding.SearchFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchFragment : BaseFragment<SearchFragmentBinding>() {
    override val viewModel: MainViewModel by sharedViewModel()
    override val layoutResourceId: Int
        get() = R.layout.search_fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // 뷰 바인딩
        binding.mainVM = viewModel

        // 번호 포맷에 맞춰 자동으로 하이픈 넣어주는 리스너 추가
        binding.etInputNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        return view
    }

    override fun onStart() {
        super.onStart()

        viewModel.command.observe(this, {
            // 사용자가 검색 버튼을 클릭
            when {
                it == Command.NUMBER_SUBMITTED -> {
                    viewModel.getReservations(getString(R.string.phone_regex).toRegex(), binding.etInputNumber.text.toString())
                }
            }
        })

        binding.etInputNumber.setOnKeyListener {_, keyCode, event ->
            // 사용자가 엔터를 누름
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                viewModel.getReservations(getString(R.string.phone_regex).toRegex(), binding.etInputNumber.text.toString())
                true
            }
            else
                false
        }

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
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.notFoundCount.value = 0
        viewModel.invalidNumber.value = false
    }
}