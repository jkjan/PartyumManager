package com.partyum.partyummanager.dao

import com.partyum.partyummanager.dto.ReservationEntry

data class ReservationInfo(
    // 고유 코드
    val code: String = "",

    var groomNumber: String = "",
    var brideNumber: String = "",

    var groomName: String = "",
    var brideName: String = "",

    // 생성일
    var createdDateTime: String = "",

    // 수정일
    var modifiedDateTime: String = "",
)