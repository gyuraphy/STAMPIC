package com.googleplay.stampic

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@HiltAndroidApp
class STAMPICApplication : Application() {
    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
        const val MIN_TIME_BETWEEN_UPDATES: Long = 1000 // 1초
        const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10미터
    }
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
