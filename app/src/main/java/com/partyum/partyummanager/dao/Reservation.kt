package com.partyum.partyummanager.dao

import java.io.Serializable

// 예약
data class Reservation(
    // 정보
    var info: Info = Info(),

    // 문서
    var docs: HashMap<String, Document> = hashMapOf(),

    ): Serializable