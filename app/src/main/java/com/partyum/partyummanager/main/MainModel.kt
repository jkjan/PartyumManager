package com.partyum.partyummanager.main

class MainModel {
    fun isValidNumber(regex: Regex, phoneNumber: String): Boolean {
        return regex.matchEntire(phoneNumber) != null
    }
}