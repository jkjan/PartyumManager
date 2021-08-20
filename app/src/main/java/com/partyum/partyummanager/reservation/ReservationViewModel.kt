package com.partyum.partyummanager.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.dao.DocumentInfo
import com.partyum.partyummanager.dao.ReservationInfo
import com.partyum.partyummanager.dto.DocumentEntry
import com.partyum.partyummanager.model.MainModel
import com.partyum.partyummanager.repository.DocumentRepository
import com.partyum.partyummanager.repository.ReservationRepository

class ReservationViewModel(private val documentRepository: DocumentRepository, private val reservationRepository: ReservationRepository): BaseViewModel() {
    val reservationInfo: MutableLiveData<ReservationInfo> = MutableLiveData(null)
    val docs: MutableLiveData<List<Pair<String, DocumentInfo>>> = MutableLiveData(null)
    val selectedDocumentKey: MutableLiveData<String> = MutableLiveData(null)

    lateinit var newDocumentKey: MutableLiveData<String>
    lateinit var invalidNumber: MutableLiveData<Boolean>
    lateinit var invalidDate: MutableLiveData<Boolean>

    override fun init() {
    }

    fun setReservationKey(reservationKey: String) {
        reservationRepository.reservationKey = reservationKey
        documentRepository.reservationKey = reservationKey
        reservationRepository.dbFailed = dbFailed
        documentRepository.dbFailed = dbFailed
    }

    fun initModification() {
        invalidNumber = MutableLiveData(false)
        invalidDate = MutableLiveData(false)
    }

    fun initAddNewDocument() {
        newDocumentKey = MutableLiveData(null)
    }

    // 예약 데이터 동기화
    fun getReservationInfoFromDB(): LiveData<ReservationInfo> {
        reservationRepository.getReservationInfo(reservationInfo)
        return reservationInfo
    }

    // 문서 데이터 동기화
    fun getDocumentsFromDB(): LiveData<List<Pair<String, DocumentInfo>>> {
        documentRepository.getDocumentEntries(docs)
        return docs
    }

    // 문서 선택됨 -> 문서 액티비티로
    fun selectDocument(documentKey: String) {
        selectedDocumentKey.value = documentKey
    }

    // 새 문서 생성
    fun newDocumentSelected(name: String, type: String) {
        if (type == "ctr" && docs.value!!.count {
            it.second.type == "ctr"
        } == 1) {
            command.value = Command.CLOSE_FRAGMENT
            showSnackBar(R.string.document_add_warning)
            return
        }

        val now = MainModel.getNowToString(MainModel.DATE_TIME_FORMAT)

        val newDocumentInfo = DocumentInfo(
            name=name,
            type=type,
            createdDateTime=now,
            modifiedDateTime=now
        )

        documentRepository.createNewDocumentInfo(newDocumentInfo, newDocumentKey)
    }

    // 예약 삭제
    fun deleteReservation() {
        reservationRepository.deleteReservation()
    }

    fun createNewReservation(groomName: String, groomNumber: String, brideName: String, brideNumber:String, createdDateTime: String) {
        if (groomName.isNotEmpty() && groomNumber.isNotEmpty() && brideName.isNotEmpty() && brideNumber.isNotEmpty() && createdDateTime.isNotEmpty()) {
            // 둘 중 하나가 잘못된 번호가 입력됨
            if (!MainModel.isValidNumber(groomNumber) || !MainModel.isValidNumber(brideNumber)) {
                invalidNumber.value = true
            } else if (!MainModel.isValidDateTime(createdDateTime)) {
                invalidDate.value = true
            }
            else {
                // 현재 시간을 문자열로 얻어옴
                val now = MainModel.getNowToString(MainModel.DATE_TIME_FORMAT)

                // 새로운 예약 데이터
                val newReservation =
                    ReservationInfo(
                        groomName=groomName,
                        groomNumber=groomNumber,
                        brideName=brideName,
                        brideNumber=brideNumber,
                        createdDateTime=createdDateTime,
                        modifiedDateTime=now
                    )

                // 새 예약 데이터를 DB에 추가
                reservationRepository.createNewReservationInfo(newReservation, null)

                command.value = Command.CLOSE_FRAGMENT
            }
        }
    }

    fun getReservationKey(): String {
        return reservationRepository.reservationKey
    }
 }