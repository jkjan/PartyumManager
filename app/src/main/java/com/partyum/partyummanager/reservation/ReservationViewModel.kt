package com.partyum.partyummanager.reservation

import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.dao.Reservation

class ReservationViewModel: BaseViewModel() {
    var reservations: MutableLiveData<List<Reservation>>? = null
    lateinit var selectedReservation: MutableLiveData<Reservation>
    lateinit var selectedDocumentIndex: MutableLiveData<Int>

    override fun init() {
        selectedReservation = MutableLiveData(null)
        selectedDocumentIndex = MutableLiveData(-1)
    }

    // 예약들을 메인에서 넘어온 값들로 저장
    fun initReservations(reservations: List<Reservation>) {
        this.reservations = MutableLiveData(reservations)
    }

    // 예약 선택 프래그먼트에서 예약 하나를 선택함
    fun selectReservation(position: Int) {
        selectedReservation.value = reservations!!.value!![position]
    }

    // 문서 선택됨 -> 문서 액티비티로
    fun selectDocument(position: Int) {
        selectedDocumentIndex.value = position
    }
}