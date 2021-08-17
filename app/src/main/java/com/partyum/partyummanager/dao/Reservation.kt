package com.partyum.partyummanager.dao

// 예약
data class Reservation(
    // 정보
    var reservationInfo: ReservationInfo = ReservationInfo(),

    // 문서
    var docs: HashMap<String, Document> = hashMapOf(),
)