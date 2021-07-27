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


class DocumentRepository: BaseRepository() {
    fun getDocumentFromDB(reservationKey: String, documentKey: String, document: MutableLiveData<Document>) {
        val query = db.getReference("reservations").child(reservationKey).child("docs").child(documentKey)

        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("firebase-get", "Got document value: ${snapshot.value}")

                if (snapshot.value != null) {
                    document.postValue(snapshot.getValue<Document>())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebase-get", "문서 데이터를 가져오지 못했습니다.")
            }
        })
    }

    fun saveDocumentToDB(reservationKey: String, documentKey: String, documentElem: HashMap<String, String>) {
        val ref = db.getReference("reservations").child(reservationKey).child("docs").child(documentKey)
        val query = ref.child("elem").setValue(documentElem)

        query.addOnSuccessListener {
            Log.i("firebase-push", "문서 데이터 추가에 성공했습니다.")
        }.addOnFailureListener {
            Log.e("firebase-push", "문서 데이터 추가에 실패했습니다.")
        }
    }

    fun getDocs(key: String, docs: MutableLiveData<List<Pair<String, Document>>>) {

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

    fun createNewDocument(newDocument: Document, reservationKey: String, newDocumentKey: MutableLiveData<String>) {
        val dbRef = db.getReference("reservations").child(reservationKey).child("docs")

        val newKey = dbRef.push().key

        dbRef.child(newKey!!).setValue(newDocument).addOnSuccessListener {
            Log.i("firebase-push", "추가 성공: $newDocument")
            newDocumentKey.postValue(newKey)
        }.addOnFailureListener {
            Log.e("firebase-push", "추가 실패: $newDocument")
        }
    }
}
