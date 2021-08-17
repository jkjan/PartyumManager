package com.partyum.partyummanager.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.partyum.partyummanager.base.BaseRepository
import com.partyum.partyummanager.dao.ReservationInfo
import com.partyum.partyummanager.dto.ReservationEntry

class ReservationRepository : BaseRepository() {
    fun getReservationInfo(
        reservation: MutableLiveData<ReservationInfo>,
    ) {
        Log.i("firebase-sync", "${reservationKey}의 변경을 감지합니다.")

        val query = reservations.child(reservationKey).child("info")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("firebase-sync", "예약 키 ${reservationKey}의 데이터가 변경되었습니다.")
                Log.i("firebase-sync", "got value ${snapshot.value}")
                if (snapshot.value != null) {
                    reservation.postValue(snapshot.getValue<ReservationInfo>())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("firebase-sync", "변경사항을 가져오지 못하였습니다.")
            }
        })
    }

    fun getReservationsByNumber(
        phoneNumber: String,
        reservations: MutableLiveData<List<Pair<String, ReservationEntry>>>,
    ) {
        // 신랑 혹은 신부에 따라 휴대폰 번호를 DB 에서 검색함
        val query = dbRef.getReference("reservations")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = arrayListOf<Pair<String, ReservationEntry>>()

                if (snapshot.exists()) {
                    snapshot.children.forEach { child->
                        val info = child.child("info").getValue<ReservationInfo>()

                        if (info != null && (info.groomNumber == phoneNumber || info.brideNumber == phoneNumber)) {
                            if (child.key != null) {
                                temp.add(Pair(child.key!!, ReservationEntry(info.groomName, info.brideName, info.modifiedDateTime)))
                            }
                        }
                    }

                    reservations.postValue(temp)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun createNewReservationInfo(newReservationInfo: ReservationInfo, newReservationKey: MutableLiveData<String>?) {
        val key =
            (
                if (newReservationKey != null)
                    reservations.push().key
                else
                    reservationKey
            ) ?: return

        reservations.child(key).child("info").setValue(newReservationInfo).addOnSuccessListener {
            Log.i("firebase-push", "예약 데이터 추가에 성공했습니다.")
            newReservationKey?.postValue(key)

        }.addOnFailureListener {
            Log.e("firebase-push", "예약 데이터 추가에 실패했습니다.")
        }
    }

    fun deleteReservation() {
        val dbRef = reservations.child(reservationKey)

        dbRef.removeValue().addOnSuccessListener {
            Log.i("firebase-delete", "예약 삭제에 성공했습니다.")
        }.addOnFailureListener {
            Log.e("firebase-delete", "예약 삭제에 실패했습니다.")
        }
    }
}