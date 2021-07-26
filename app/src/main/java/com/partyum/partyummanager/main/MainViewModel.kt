package com.partyum.partyummanager.main

import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.dao.Info
import com.partyum.partyummanager.dao.Reservation
import com.partyum.partyummanager.model.MainModel

class MainViewModel(private val mainModel: MainModel, private val mainRepository: MainRepository): BaseViewModel() {
    // 메인 화면 리스너
    lateinit var command: MutableLiveData<Command>

    // 예약 검색 전화번호
    private lateinit var phoneNumber: MutableLiveData<String>

    // 검색된 예약들
    lateinit var reservations: MutableLiveData<List<Pair<String, Info>>>

    // 번호 검색 안 된 횟수 (최대 2)
    lateinit var notFoundCount: MutableLiveData<Int>

    // 번호 유효성 여부
    lateinit var invalidNumber: MutableLiveData<Boolean>

    // 검색, 새 예약 생성 시 최종 결정된 키
    lateinit var reservationKey: MutableLiveData<String>


    override fun init() {
        command = MutableLiveData(Command.NONE)
    }

    fun onClick(command: Command) {
        this.command.value = command
    }

    fun initNewReservation() {
        reservationKey = MutableLiveData(null)
    }

    fun createNewReservation(groomName: String, brideName: String, format: String) {
        // 현재 시간을 문자열로 얻어옴
        val now = mainModel.getNowToString(format)

        // 새로운 예약 데이터
        val newReservation = Reservation(Info(groomName=groomName, brideName=brideName, createdDateTime=now, modifiedDateTime=now))

        // 새 예약 데이터를 DB에 추가
        mainRepository.createNewReservation(newReservation, reservationKey)
    }

    fun initSearch() {
        reservations = MutableLiveData(listOf())
        reservationKey = MutableLiveData(null)
        phoneNumber = MutableLiveData("")
        notFoundCount = MutableLiveData(0)
        invalidNumber = MutableLiveData(false)
    }

    // 전화번호 유효성을 체크하고, 유효할 시 DB에 검색
    fun getReservations(regex: Regex, phoneNumber: String): MutableLiveData<List<Pair<String, Info>>> {
        if (!mainModel.isValidNumber(regex, phoneNumber)) {
            invalidNumber.value = true
        }
        else {
            mainRepository.getReservations(phoneNumber, reservations, notFoundCount)
        }
        return reservations
    }

    fun selectReservation(reservationKey: String) {
        this.reservationKey.value = reservationKey
    }
}