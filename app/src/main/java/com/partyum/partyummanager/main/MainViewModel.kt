package com.partyum.partyummanager.main

import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.dao.Reservation

class MainViewModel(private val mainModel: MainModel, private val mainRepository: MainRepository): BaseViewModel() {
    lateinit var command: MutableLiveData<Command>
    private lateinit var phoneNumber: MutableLiveData<String>
    lateinit var reservations: MutableLiveData<ArrayList<Reservation>>
    lateinit var notFoundCount: MutableLiveData<Int>
    lateinit var invalidNumber: MutableLiveData<Boolean>

    override fun init() {
        command = MutableLiveData(Command.NONE)
        phoneNumber = MutableLiveData("")
        reservations = MutableLiveData(arrayListOf())
        notFoundCount = MutableLiveData(0)
        invalidNumber = MutableLiveData(false)
    }

    fun onClick(command: Command) {
        this.command.value = command
    }

    // 전화번호 유효성을 체크하고, 유효할 시 DB에 검색
    fun getReservations(regex: Regex, phoneNumber: String) {
        invalidNumber.value = false
        notFoundCount.value = 0
        reservations.value = arrayListOf()

        if (!mainModel.isValidNumber(regex, phoneNumber)) {
            invalidNumber.value = true
        }
        else {
            mainRepository.getReservations(phoneNumber, reservations, notFoundCount)
        }
    }
}