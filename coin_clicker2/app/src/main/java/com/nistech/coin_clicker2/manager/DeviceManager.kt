package com.nistech.coin_clicker2.manager

import android.app.Activity
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity

class DeviceManager {

    fun getCountryCode(activity: Activity): String =
        (activity.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager).simCountryIso
}