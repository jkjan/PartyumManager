package com.partyum.partyummanager.dao

data class Info(
    // 고유 코드
    val code: String = "",

    // 신랑 이름
    val groomName: String = "",

    // 신랑 전화번호
    val groomNumber: String = "",

    // 신부 이름
    val brideName: String = "",

    // 신부 전화번호
    val brideNumber: String = "",

    // 생성일
    var createdDateTime: String = "",

    // 수정일
    var modifiedDateTime: String = "",
)