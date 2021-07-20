package com.partyum.partyummanager.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.ktx.getValue
import com.partyum.partyummanager.base.BaseRepository
import com.partyum.partyummanager.dao.Reservation

class MainRepository: BaseRepository() {
    private fun getReservationsByNumber(phoneNumber: String, numberOwner: String, reservations: MutableLiveData<ArrayList<Reservation>>, notFoundCount: MutableLiveData<Int>) {
        val dbRef = db.getReference("reservations")

        // 신랑 혹은 신부에 따라 휴대폰 번호를 DB 에서 검색함
        dbRef.orderByChild(numberOwner + "Number").equalTo(phoneNumber).get().addOnSuccessListener { dataSnapshot ->
            // 데이터 검색 성공
            Log.i("firebase-get", "Got value ${dataSnapshot.value}")

            // 검색된 데이터 있음
            if (dataSnapshot.value != null) {
                val temp = arrayListOf<Reservation>()

                // 찾은 데이터 중 null 값이 아닌 것들만 예약 리스트에 저장
                dataSnapshot.getValue<ArrayList<Reservation?>>()?.forEach {
                    if (it != null) {
                        temp.add(it)
                    }
                }

                // ViewModel 에서 온 예약 데이터에 저장
                reservations.value?.addAll(temp)
                reservations.postValue(reservations.value)
            } else {
                // 검색된 데이터 없음 -> notFoundCount 를 1 올림
                notFoundCount.value = notFoundCount.value?.plus(1)
            }

        }.addOnFailureListener {
            // 데이터 검색 실패
            Log.e("firebase-get", "Failed to get value from $phoneNumber")
        }
    }

    fun getReservations(phoneNumber: String, reservations: MutableLiveData<ArrayList<Reservation>>, notFoundCount: MutableLiveData<Int>) {
        initRepository()

        // 번호를 신랑에서 한 번, 신부에서 한 번 검색
        getReservationsByNumber(phoneNumber, "groom", reservations, notFoundCount)
        getReservationsByNumber(phoneNumber, "bride", reservations, notFoundCount)
    }
}
