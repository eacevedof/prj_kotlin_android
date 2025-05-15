package com.example.poc_android

import java.util.Date
import java.util.UUID

data class User(
    val uuid: String = UUID.randomUUID().toString(),
    var name: String,
    var birthDate: Date,
    var email: String,
    var phone: String,
    var username: String,
    var accessPassword: String
)