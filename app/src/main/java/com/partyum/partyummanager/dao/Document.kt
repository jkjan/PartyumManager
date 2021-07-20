package com.partyum.partyummanager.dao

import java.io.Serializable

// 문서
data class Document(
    // 이름
    var name: String = "",

    // 유형
    val type: String = "",

    // xml 레이아웃
    val xml: String = "",

    // 생성일
    val createdDateTime: String = "",

    // 수정일
    var modifiedDateTime: String = ""
): Serializable