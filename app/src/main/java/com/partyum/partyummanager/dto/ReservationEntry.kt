package com.partyum.partyummanager.dto

data class ReservationEntry(
    var groomName: String = "",
    var brideName: String = "",

    // 수정일
    var modifiedDateTime: String = "",
)