package com.partyum.partyummanager.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.partyum.partyummanager.base.BaseRepository
import com.partyum.partyummanager.dao.Document
import com.partyum.partyummanager.dao.Info
import com.partyum.partyummanager.dao.Reservation

class ReservationRepository : BaseRepository() {
    fun getReservation(
        key: String,
        reservation: MutableLiveData<Info>
    ) {

        Log.i("firebase-sync", "${key}의 변경을 감지합니다.")

        val query = db.getReference("reservations").child(key).child("info")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("firebase-sync", "예약 키 ${key}의 데이터가 변경되었습니다.")
                Log.i("firebase-sync", "got value ${snapshot.value}")
                if (snapshot.value != null) {
                    reservation.postValue(snapshot.getValue<Info>())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("firebase-sync", "변경사항을 가져오지 못하였습니다.")
            }
        })
    }

    private fun getReservationsByNumber(
        phoneNumber: String,
        numberOwner: String,
        reservations: MutableLiveData<List<Pair<String, Info>>>,
        notFoundCount: MutableLiveData<Int>,
    ) {
        val dbRef = db.getReference("reservations")

        // 신랑 혹은 신부에 따라 휴대폰 번호를 DB 에서 검색함
        val query = dbRef.orderByChild("info/" + numberOwner + "Number").equalTo(phoneNumber)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("firebase-get", "$phoneNumber Got value ${dataSnapshot.value}")

                // 검색된 데이터 있음
                if (dataSnapshot.value != null) {
                    var temp = arrayListOf<Pair<String, Info>>()

                    // 찾은 데이터 중 null 값이 아닌 것들만 예약 리스트에 저장
                    dataSnapshot.getValue<HashMap<String, Reservation?>>()?.forEach {
                        if (it.value != null) {
                            temp.add(Pair(it.key, it.value!!.info))
                        }
                    }

                    // ViewModel 에서 온 예약 데이터에 저장
                    if (reservations.value != null) {
                        // 신부 검색일 경우 추가
                        if (numberOwner == "bride") {
                            temp = reservations.value!!.plus(temp) as ArrayList<Pair<String, Info>>
                        }

                        reservations.postValue(
                            temp.sortedBy {
                                it.second.modifiedDateTime
                            }
                        )
                    }
                } else {
                    notFoundCount.value = notFoundCount.value?.plus(1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebase-get", "Failed to get value from $phoneNumber")
            }
        })
    }

    fun getReservations(
        phoneNumber: String,
        reservations: MutableLiveData<List<Pair<String, Info>>>,
        notFoundCount: MutableLiveData<Int>
    ) {

        // 번호를 신랑에서 한 번, 신부에서 한 번 검색
        getReservationsByNumber(phoneNumber, "groom", reservations, notFoundCount)
        getReservationsByNumber(phoneNumber, "bride", reservations, notFoundCount)
    }

    fun createNewReservation(newReservation: Reservation, key: MutableLiveData<String>) {

        val dbRef = db.getReference("reservations")

        Log.i("main repository", "예약 추가: $newReservation")

        val newReservationKey = dbRef.push().key

        dbRef.child(newReservationKey!!).setValue(newReservation).addOnSuccessListener {
            Log.i("firebase-push", "예약 데이터 추가에 성공했습니다.")
            key.postValue(newReservationKey)

        }.addOnFailureListener {
            Log.e("firebase-push", "예약 데이터 추가에 실패했습니다.")
        }
    }
}