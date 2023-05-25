package com.googleplay.stampic.domain.model

import android.graphics.drawable.Drawable

data class OnBoardData(
    val imgDrawable: Drawable,
    val title: String,
    val content: String
)