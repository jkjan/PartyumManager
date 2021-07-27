package com.partyum.partyummanager.main

import android.util.Log
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.databinding.FragmentNewReservationBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewReservationFragment: BaseFragment<FragmentNewReservationBinding>() {
    override val viewModel: MainViewModel by sharedViewModel()
    override val layoutResourceId: Int
        get() = R.layout.fragment_new_reservation

    override fun bindData() {
        // 뷰 바인딩
        binding.viewModel = viewModel
    }

    override fun setListeners() {
        viewModel.command.observe(this, {
            when (it) {
                Command.NEW_RESERVATION_SUBMITTED -> {

                    Log.i("add reservation", "${binding.etNewGroomName.text}과 ${binding.etNewBrideName.text}을 추가합니다.")

                    viewModel.createNewReservation(
                        binding.etNewGroomName.text.toString(),
                        binding.etNewGroomNumber.text.toString(),
                        binding.etNewBrideName.text.toString(),
                        binding.etNewBrideNumber.text.toString(),
                        getString(R.string.datetime_format))

                    dismiss()
                }
                else -> {}
            }
        })
    }
}