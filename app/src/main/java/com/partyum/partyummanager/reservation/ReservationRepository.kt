package com.partyum.partyummanager.reservation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.partyum.partyummanager.base.BaseRepository
import com.partyum.partyummanager.dao.Reservation

class ReservationRepository : BaseRepository() {
    fun getReservation(
        code: LiveData<String>,
        reservation: MutableLiveData<Reservation>
    ) {
        initRepository()

        if (code.value != null) {
            Log.i("firebase-sync", "${code.value}의 변경을 감지합니다.")

            val query = db.getReference("reservations").orderByChild("code").equalTo(code.value)

            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("firebase-sync", "예약코드 ${code.value}의 데이터가 변경되었습니다.")
                    if (snapshot.value != null) {
                        val temp = arrayListOf<Reservation>()
                        Log.i("firebase-sync", snapshot.value.toString())

                        snapshot.getValue<List<Reservation?>>()!!.forEach {
                            if (it != null) {
                                temp.add(it)
                            }
                        }

                        reservation.postValue(temp[0])
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("firebase-sync", "변경사항을 가져오지 못하였습니다.")
                }
            })
        }
    }
}