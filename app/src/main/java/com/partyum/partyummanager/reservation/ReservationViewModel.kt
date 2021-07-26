package com.partyum.partyummanager.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.dao.Document
import com.partyum.partyummanager.dao.Info

class ReservationViewModel(private val reservationRepository: ReservationRepository): BaseViewModel() {
    lateinit var reservationKey: String
    lateinit var reservation: MutableLiveData<Info>
    lateinit var docs: MutableLiveData<List<Pair<String, Document>>>
    lateinit var selectedDocumentKey: MutableLiveData<String>

    override fun init() {
        reservation = MutableLiveData(null)
        selectedDocumentKey = MutableLiveData(null)
        docs = MutableLiveData(null)
    }

    // 예약 데이터 동기화
    fun getInfoFromDB(): LiveData<Info> {
        reservationRepository.getReservation(reservationKey, reservation)
        return reservation
    }

    // 문서 데이터 동기화
    fun getDocumentsFromDB(): LiveData<List<Pair<String, Document>>> {
        reservationRepository.getDocs(reservationKey, docs)
        return docs
    }

    // 문서 선택됨 -> 문서 액티비티로
    fun selectDocument(documentKey: String) {
        selectedDocumentKey.value = documentKey
    }
}