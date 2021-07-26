package com.partyum.partyummanager.document

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.partyum.partyummanager.base.BaseRepository
import com.partyum.partyummanager.dao.Document


class DocumentRepository: BaseRepository() {
    fun getDocumentFromDB(reservationKey: String, documentKey: String, document: MutableLiveData<Document>) {
        initRepository()

        val query = db.getReference("reservations").child(reservationKey).child("docs").child(documentKey)

        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("firebase-get", "Got value: ${snapshot.value}")

                if (snapshot.value != null) {
                    document.postValue(snapshot.getValue<Document>())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebase-get", "문서 데이터를 가져오지 못했습니다.")
            }

        })
    }
}
