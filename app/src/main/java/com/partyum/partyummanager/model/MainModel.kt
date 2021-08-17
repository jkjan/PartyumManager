package com.partyum.partyummanager.model

import java.text.SimpleDateFormat
import java.util.*

class MainModel {
    companion object {
        const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
        private val PHONE_NUMBER_REGEX = Regex("^\\d{3}-\\d{3,4}-\\d{4}$")
        private val DATE_TIME_REGEX = Regex("^(20[1-9][0-9])-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) ([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])\$")

        fun isValidNumber(phoneNumber: String): Boolean {
            return PHONE_NUMBER_REGEX.matchEntire(phoneNumber) != null
        }

        fun getNowToString(format: String): String {
            val sdf = SimpleDateFormat(format, Locale.KOREA)
            return sdf.format(Date())
        }

        fun isValidDateTime(dateTime: String): Boolean {
            return DATE_TIME_REGEX.matchEntire(dateTime) != null
        }
    }
}