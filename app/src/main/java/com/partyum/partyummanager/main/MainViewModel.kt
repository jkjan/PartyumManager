package com.partyum.partyummanager.main

import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.dao.ReservationInfo
import com.partyum.partyummanager.dto.ReservationEntry
import com.partyum.partyummanager.model.MainModel
import com.partyum.partyummanager.repository.ReservationRepository

class MainViewModel(private val reservationRepository: ReservationRepository): BaseViewModel() {
    // 예약 검색 전화번호
    private lateinit var phoneNumber: MutableLiveData<String>

    // 검색된 예약들
    lateinit var reservationEntries: MutableLiveData<List<Pair<String, ReservationEntry>>>

    // 번호 유효성 여부
    lateinit var invalidNumber: MutableLiveData<Boolean>

    lateinit var invalidDate: MutableLiveData<Boolean>

    // 검색, 새 예약 생성 시 최종 결정된 키
    lateinit var reservationKey: MutableLiveData<String>


    override fun init() {
    }

    // 새 예약 추가 시 사용되는 변수 초기화
    fun initNewReservation() {
        reservationRepository.dbFailed = dbFailed
        reservationKey = MutableLiveData(null)
        invalidDate = MutableLiveData(false)
        invalidNumber = MutableLiveData(false)
    }

    // 새 예약 생성
    fun createNewReservation(groomName: String, groomNumber: String, brideName: String, brideNumber:String, createdDateTime: String) {
        if (groomName.isNotEmpty() && groomNumber.isNotEmpty() && brideNumber.isNotEmpty() && brideName.isNotEmpty() && createdDateTime.isNotEmpty()) {
            // 둘 중 하나가 잘못된 번호가 입력됨
            if (!MainModel.isValidNumber(groomNumber) || !MainModel.isValidNumber(brideNumber)) {
                invalidNumber.value = true
            } else if (!MainModel.isValidDateTime(createdDateTime)) {
                invalidDate.value = true
            } else {
                invalidNumber.value = false
                invalidDate.value = false

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
                reservationRepository.createNewReservationInfo(newReservation, reservationKey)
            }
        }
    }

    // 검색 시 사용되는 데이터 초기화
    fun initSearch() {
        reservationEntries = MutableLiveData(null)
        reservationKey = MutableLiveData(null)
        phoneNumber = MutableLiveData("")
        invalidNumber = MutableLiveData(false)
        invalidDate = MutableLiveData(false)
        reservationRepository.dbFailed = dbFailed
    }

    // 전화번호 유효성을 체크하고, 유효할 시 DB에 검색
    fun getReservations(phoneNumber: String): MutableLiveData<List<Pair<String, ReservationEntry>>> {
        if (!MainModel.isValidNumber(phoneNumber)) {
            invalidNumber.value = true
        }
        else {
            reservationRepository.getReservationsByNumber(phoneNumber, reservationEntries)
        }
        return reservationEntries
    }

    // 예약 선택됨
    fun selectReservation(reservationKey: String) {
        this.reservationKey.value = reservationKey
    }
}