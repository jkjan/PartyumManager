package com.partyum.partyummanager.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

abstract class BaseRepository {
    val dbRef: FirebaseDatabase = FirebaseDatabase.getInstance("https://partyummanager-5eabb-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val reservations = dbRef.getReference("reservations")
    val storage = Firebase.storage("gs://partyummanager-5eabb.appspot.com")
    lateinit var dbFailed: MutableLiveData<Boolean>

    val aMEGABYTE: Long = 1024 * 1024

    lateinit var reservationKey: String
    lateinit var documentKey: String

    inner class Listener(private val logTag: String, private val taskTitle: String) {
        inner class SuccessListener(private val methodOnSuccess: ((Any?) -> Unit) = {}): OnSuccessListener<Any> {
            override fun onSuccess(p0: Any?) {
                methodOnSuccess(p0)
                dbFailed.postValue(false)
                Log.i(logTag, "${taskTitle}에 성공했습니다.")
            }
        }

        inner class FailureListener(private val methodOnFailure: ((Exception)->Unit) = {}): OnFailureListener {
            override fun onFailure(p0: Exception) {
                methodOnFailure(p0)
                dbFailed.postValue(true)
                Log.e(logTag, "${taskTitle}에 실패했습니다.")
            }
        }

        inner class DataChangeListener(private val methodOnDataChange: ((DataSnapshot) -> Unit) = {}, private val methodOnCancelled: ((DatabaseError) -> Unit) = {}): ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                methodOnDataChange(snapshot)
                dbFailed.postValue(false)
                Log.i(logTag, "${taskTitle}에 성공했습니다.")
            }

            override fun onCancelled(error: DatabaseError) {
                methodOnCancelled(error)
                dbFailed.postValue(true)
                Log.e(logTag, "${taskTitle}에 실패했습니다.")
            }
        }
    }
}
