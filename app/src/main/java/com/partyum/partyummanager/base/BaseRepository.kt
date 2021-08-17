package com.partyum.partyummanager.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
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

    inner class Listener(private val logTag: String, private val taskTitle: String, private val taskAfterSuccess: ()->Unit, private val taskAfterFailure: ()->Unit) {
        inner class SuccessListener: OnSuccessListener<Any> {
            override fun onSuccess(p0: Any?) {
                taskAfterSuccess()
                dbFailed.postValue(false)
                Log.i(logTag, "${taskTitle}에 성공했습니다.")
            }
        }

        inner class FailureListener: OnFailureListener {
            override fun onFailure(p0: Exception) {
                taskAfterFailure()
                dbFailed.postValue(true)
                Log.e(logTag, "${taskTitle}에 실패했습니다.")
            }
        }
    }
}
