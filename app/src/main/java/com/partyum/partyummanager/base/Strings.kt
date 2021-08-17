package com.partyum.partyummanager.base

enum class Strings(var str: String) {
    DB_NAME("firebase"),
    DB_POST("${DB_NAME.str}_post"),
    DB_PATCH("${DB_NAME.str}_patch"),
    DB_GET("${DB_NAME.str}_get"),
    DB_PUT("${DB_NAME.str}_put"),
    DB_DELETE("${DB_NAME.str}_delete"),


}