package com.partyum.partyummanager.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.dao.Reservation

class ReservationViewModel(private val reservationRepository: ReservationRepository): BaseViewModel() {
    var reservations: MutableLiveData<List<Reservation>>? = null
    lateinit var reservation: MutableLiveData<Reservation>
    lateinit var selectedDocumentIndex: MutableLiveData<Int>
    lateinit var code: MutableLiveData<String>

    override fun init() {
        reservation = MutableLiveData(null)
        selectedDocumentIndex = MutableLiveData(-1)
        code = MutableLiveData(null)
    }

    // 예약들을 메인에서 넘어온 값들로 저장
    fun initReservations(reservations: List<Reservation>) {
        this.reservations = MutableLiveData(reservations)
    }

    // 예약 선택 프래그먼트에서 예약 하나를 선택함
    fun selectReservation(position: Int) {
        code.value = reservations!!.value!![position].code
    }

    // 문서 선택됨 -> 문서 액티비티로
    fun selectDocument(position: Int) {
        selectedDocumentIndex.value = position
    }

    // 예약 정보 동기화
    fun getReservationFromDB(): LiveData<Reservation> {
        reservationRepository.getReservation(code, reservation)
        return reservation
    }
}