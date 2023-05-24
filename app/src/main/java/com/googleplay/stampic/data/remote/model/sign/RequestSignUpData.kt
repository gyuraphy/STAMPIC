package com.googleplay.stampic.data.remote.model.sign

data class RequestSignUpData(
    val name: String,
    val email: String,
    val password: String,
    val nickName: String,
)