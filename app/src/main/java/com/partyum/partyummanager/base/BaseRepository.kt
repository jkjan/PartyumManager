package com.partyum.partyummanager.base

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

abstract class BaseRepository {
    var db: FirebaseDatabase = Firebase.database
}