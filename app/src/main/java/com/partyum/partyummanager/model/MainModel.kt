package com.partyum.partyummanager.model

import java.text.SimpleDateFormat
import java.util.*

class MainModel {
    companion object {
        fun isValidNumber(regex: Regex, phoneNumber: String): Boolean {
            return regex.matchEntire(phoneNumber) != null
        }

        fun getNowToString(format: String): String {
            val sdf = SimpleDateFormat(format, Locale.KOREA)
            return sdf.format(Date())
        }
    }
}