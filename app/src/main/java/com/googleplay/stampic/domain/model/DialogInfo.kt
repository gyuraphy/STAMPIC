package com.googleplay.stampic.domain.model

data class DialogInfo(
    val title: String? = null,
    val text: String = "",
    val btnNegative: String? = null,
    val btnPositive: String = ""
)