package com.partyum.partyummanager.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.dao.Document
import com.partyum.partyummanager.dao.Info
import com.partyum.partyummanager.model.MainModel
import com.partyum.partyummanager.repository.DocumentRepository
import com.partyum.partyummanager.repository.ReservationRepository

class ReservationViewModel(private val documentRepository: DocumentRepository, private val reservationRepository: ReservationRepository): BaseViewModel() {
    lateinit var reservationKey: String
    lateinit var reservation: MutableLiveData<Info>
    lateinit var docs: MutableLiveData<List<Pair<String, Document>>>
    lateinit var selectedDocumentKey: MutableLiveData<String>
    lateinit var command: MutableLiveData<Command>
    lateinit var newDocumentKey: MutableLiveData<String>

    override fun init() {
        command = MutableLiveData(null)
        reservation = MutableLiveData(null)
        selectedDocumentKey = MutableLiveData(null)
        docs = MutableLiveData(null)
        newDocumentKey = MutableLiveData(null)
    }

    // 예약 데이터 동기화
    fun getInfoFromDB(): LiveData<Info> {
        reservationRepository.getReservation(reservationKey, reservation)
        return reservation
    }

    // 문서 데이터 동기화
    fun getDocumentsFromDB(): LiveData<List<Pair<String, Document>>> {
        documentRepository.getDocs(reservationKey, docs)
        return docs
    }

    // 문서 선택됨 -> 문서 액티비티로
    fun selectDocument(documentKey: String) {
        selectedDocumentKey.value = documentKey
    }

    // 액티비티로 커맨드 보내기
    fun onClick(command: Command) {
        this.command.value = command
    }

    // 새 문서 생성
    fun newDocumentSelected(name: String, type: String) {
        val now = MainModel.getNowToString("yyyy-MM-dd HH:mm:ss")

        val newDocument = Document(
            name=name,
            type=type,
            createdDateTime=now,
            modifiedDateTime=now
        )

        documentRepository.createNewDocument(newDocument, reservationKey, newDocumentKey)
    }
}