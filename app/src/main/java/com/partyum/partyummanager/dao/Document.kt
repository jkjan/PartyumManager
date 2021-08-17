package com.partyum.partyummanager.dao

import java.io.Serializable

// 문서
data class Document(
    var info: DocumentInfo? = null,

    var editableValues: HashMap<String, String>? = null,

    var selectableValues: HashMap<String, Boolean>? = null,

    var imageURLs: HashMap<String, String>? = null,
)