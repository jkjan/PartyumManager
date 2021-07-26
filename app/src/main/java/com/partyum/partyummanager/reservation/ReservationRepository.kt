package com.partyum.partyummanager.reservation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.partyum.partyummanager.base.BaseRepository
import com.partyum.partyummanager.dao.Document
import com.partyum.partyummanager.dao.Info

class ReservationRepository : BaseRepository() {
    fun getReservation(
        key: String,
        reservation: MutableLiveData<Info>
    ) {
        initRepository()

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


    fun getDocs(key: String, docs: MutableLiveData<List<Pair<String, Document>>>) {
        initRepository()

        Log.i("firebase-sync", "${key}의 변경을 감지합니다.")

        val query = db.getReference("reservations").child(key).child("docs")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("firebase-sync", "예약 키 ${key}의 데이터가 변경되었습니다.")
                Log.i("firebase-sync", "got value ${snapshot.value}")
                if (snapshot.value != null) {
                    val temp = arrayListOf<Pair<String, Document>>()

                    snapshot.getValue<HashMap<String, Document>?>()!!.forEach {
                        temp.add(Pair(it.key, it.value))
                    }

                    Log.i("firebase-sync", "processed ${temp}")

                    docs.postValue(temp.sortedBy { doc ->
                        doc.second.modifiedDateTime
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("firebase-sync", "변경사항을 가져오지 못하였습니다.")
            }
        })
    }
}