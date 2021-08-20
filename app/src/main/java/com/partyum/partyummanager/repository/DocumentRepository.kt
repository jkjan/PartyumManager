package com.partyum.partyummanager.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.partyum.partyummanager.base.BaseRepository
import com.partyum.partyummanager.base.Strings
import com.partyum.partyummanager.dao.Document
import com.partyum.partyummanager.dao.DocumentInfo
import com.partyum.partyummanager.model.MainModel


class DocumentRepository: BaseRepository() {
    private fun updateModifiedDateTime(now: String) {
        val ref = reservations.child(reservationKey).child("docs").child(documentKey).child("info").child("modifiedDateTime")
        val query = ref.setValue(now)

        val listener = Listener(Strings.DB_PATCH.str, "생성일 수정")

        query.addOnSuccessListener(listener.SuccessListener())
        query.addOnFailureListener(listener.FailureListener())
    }

    fun modifyDocumentValues(tag: String, previousValue: Any?, modifiedValue: Any, valueType: String) {
        val ref = reservations.child(reservationKey).child("docs").child(documentKey).child(valueType).child(tag)
        val query = ref.setValue(modifiedValue)

        val listener = Listener(Strings.DB_PATCH.str, "문서 데이터 수정")

        val methodOnSuccess = { _: Any? ->
            val message = when (valueType) {
                "editable" -> {
                    "$tag: " +
                            if (previousValue == null)
                                "$modifiedValue 추가됨"
                            else
                                "$previousValue -> $modifiedValue 로 변경됨"
                }
                "selectable" -> {
                    "$tag: " +
                            if (modifiedValue as Boolean)
                                "선택됨"
                            else
                                "해제됨"
                }
                "image-url" -> {
                    "$tag 서명됨"
                }
                else -> null
            }

            if (message != null) {
                dbFailed.postValue(false)
                addHistory(message)
            }
        }

        query.addOnSuccessListener(listener.SuccessListener(methodOnSuccess))
        query.addOnFailureListener(listener.FailureListener())
    }

    private fun addHistory(message: String) {
        val now = MainModel.getNowToString(MainModel.DATE_TIME_FORMAT)
        val ref = reservations.child(reservationKey).child("docs").child(documentKey).child("history")
        val query = ref.child(now).setValue(message)

        val listener = Listener(Strings.DB_POST.str, "히스토리 추가")
        val methodOnSuccess = { _: Any?->
                updateModifiedDateTime(now)
        }

        query.addOnSuccessListener(listener.SuccessListener(methodOnSuccess))
        query.addOnFailureListener(listener.FailureListener())
    }

    fun getDocumentEntries(docs: MutableLiveData<List<Pair<String, DocumentInfo>>>) {
        Log.i("firebase-sync", "${reservationKey}의 변경을 감지합니다.")

        val query = reservations.child(reservationKey).child("docs")

        val listener = Listener(Strings.DB_GET.str, "예약 키 ${reservationKey}의 문서 정보 얻기")

        val methodOnDataChange = {snapshot: DataSnapshot->
            val temp = arrayListOf<Pair<String, DocumentInfo>>()

            if (snapshot.exists()) {
                snapshot.children.forEach { child->
                    val documentEntry = child.child("info").getValue<DocumentInfo>()
                    if (documentEntry != null && child.key != null) {
                        temp.add(Pair(child.key!!, documentEntry))
                        Log.i("시발련아", Pair(child.key!!, documentEntry).toString())
                    }
                }

                docs.postValue(temp.sortedByDescending { doc ->
                    doc.second.modifiedDateTime
                })
            }
            else {
                docs.postValue(null)
            }
        }

        query.addValueEventListener(listener.DataChangeListener(methodOnDataChange))
    }

    fun createNewDocumentInfo(newDocumentInfo: DocumentInfo, newDocumentKey: MutableLiveData<String>?) {
        val dbRef = reservations.child(reservationKey).child("docs")

        val key = if (newDocumentKey != null)
            dbRef.push().key
        else
            documentKey

        if (key == null)
            return

        dbRef.child(key).child("info").setValue(newDocumentInfo).addOnSuccessListener {
            Log.i("firebase-push", "추가 성공: $newDocumentInfo")
            newDocumentKey?.postValue(key)
        }.addOnFailureListener {
            Log.e("firebase-push", "추가 실패: $newDocumentInfo")
        }
    }

    fun deleteDocument() {
        val dbRef = reservations.child(reservationKey).child("docs").child(documentKey)

        dbRef.removeValue().addOnSuccessListener {
            Log.i("firebase-delete", "삭제에 성공하였습니다.")
        }.addOnFailureListener {
            Log.e("firebase-delete", "삭제에 실패하였습니다.")
        }
    }

    fun getDocumentInfo(
        documentInfo: MutableLiveData<DocumentInfo>
    ) {
        val dbRef = reservations.child(reservationKey).child("docs").child(documentKey).child("info")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val retrievedDocumentInfo = snapshot.getValue<DocumentInfo>()
                    if (retrievedDocumentInfo != null) {
                        documentInfo.postValue(retrievedDocumentInfo)
                        Log.i("firebase-get", "문서 정보 얻기에 성공하였습니다.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebase-get", "문서 정보 얻기에 실패하였습니다. $error")
            }
        })
    }

    fun <T: Any?>getDocumentValues(documentValues: MutableLiveData<HashMap<String, T>>, type: String) {
        val dbRef = reservations.child(reservationKey).child("docs").child(documentKey).child(type)

        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val retrieved = snapshot.getValue<HashMap<String, T>>()
                if (retrieved != null) {
                    documentValues.postValue(retrieved)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getImageURLs(document: MutableLiveData<Document>) {
        val dbRef = reservations.child(reservationKey).child("docs").child(documentKey).child("image-url")
        dbRef.get().addOnSuccessListener {
            if (it.value != null) {
                document.value!!.imageURLs = it.getValue<HashMap<String, String>>()!!
            }
        }
    }
}
