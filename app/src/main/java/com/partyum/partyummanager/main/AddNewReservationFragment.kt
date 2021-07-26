package com.partyum.partyummanager.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.databinding.FragmentAddNewReservationBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddNewReservationFragment: BaseFragment<FragmentAddNewReservationBinding>() {
    override val viewModel: MainViewModel by sharedViewModel()
    override val layoutResourceId: Int
        get() = R.layout.fragment_add_new_reservation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // 뷰 바인딩
        binding.viewModel = viewModel

        return view
    }

    override fun onStart() {
        super.onStart()

        viewModel.command.observe(this, {
            when (it) {
                Command.NEW_RESERVATION_SUBMITTED -> {

                    Log.i("add reservation", "${binding.etNewGroomName.text}과 ${binding.etNewBrideName.text}을 추가합니다.")

                    viewModel.createNewReservation(
                        binding.etNewGroomName.text.toString(),
                        binding.etNewBrideName.text.toString(),
                        getString(R.string.datetime_format))

                    dismiss()
                }
                else -> {}
            }
        })
    }
}