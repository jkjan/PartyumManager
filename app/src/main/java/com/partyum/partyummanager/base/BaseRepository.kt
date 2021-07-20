package com.partyum.partyummanager.base

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

abstract class BaseRepository {
    lateinit var db: FirebaseDatabase

    fun initRepository() {
        // DB 연결
        if (!this::db.isInitialized)
            db = Firebase.database
    }
}