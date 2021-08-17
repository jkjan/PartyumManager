package com.partyum.partyummanager.reservation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseFragment
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.dao.ReservationInfo
import com.partyum.partyummanager.databinding.FragmentModifyReservationBinding
import com.partyum.partyummanager.reservation.ReservationViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ModifyReservationFragment: BaseFragment<FragmentModifyReservationBinding>() {
    override val viewModel: ReservationViewModel by sharedViewModel()
    override val layoutResourceId: Int
        get() = R.layout.fragment_modify_reservation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  super.onCreateView(inflater, container, savedInstanceState)
        val info = viewModel.reservationInfo.value!!

        binding.invalidNumber.visibility = View.GONE
        binding.invalidDate.visibility = View.GONE

        binding.etNewGroomName.setText(info.groomName)
        binding.etNewBrideName.setText(info.brideName)
        binding.etNewGroomNumber.setText(info.groomNumber)
        binding.etNewBrideNumber.setText(info.brideNumber)
        binding.etCreatedDateTime.setText(info.createdDateTime)
        return view
    }

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
                        binding.etCreatedDateTime.text.toString()
                    )

                    viewModel.invalidNumber.observe(this, { invalidNumber->
                        if (invalidNumber) {
                            // 전화번호가 유효하지 않음
                            binding.invalidNumber.visibility = View.VISIBLE
                            binding.invalidDate.visibility = View.GONE
                        }
                    })

                    viewModel.invalidDate.observe(this, { invalidDate->
                        if (invalidDate) {
                            binding.invalidNumber.visibility = View.GONE
                            binding.invalidDate.visibility = View.VISIBLE
                        }
                    })
                }
                else -> {}
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.invalidNumber.value = false
        viewModel.invalidDate.value = false
    }
}