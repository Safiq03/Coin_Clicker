package com.nistech.coin_clicker2.storage

import android.content.Context
import android.content.SharedPreferences
import com.nistech.coin_clicker2.MyApplication

object StorageManager {

    private fun getInstance(): SharedPreferences? {
        return MyApplication.getInstance().getSharedPreferences(
            MyApplication.getInstance().packageName,
            Context.MODE_PRIVATE
        )
    }


    private const val USER_ID_KEY = "USER_ID_KEY"
    var userId: String
        get() = getInstance()?.getString(USER_ID_KEY, "")!!
        set(value) = getInstance()?.edit()!!.putString(USER_ID_KEY, value).apply()

    private const val DEEPLINK_KEY = "DEEPLINK_KEY"
    var deeplink: String
        get() = getInstance()?.getString(DEEPLINK_KEY, "")!!
        set(value) = getInstance()?.edit()!!.putString(DEEPLINK_KEY, value).apply()

    private const val GAD_ID_KEY = "GADID_KEY"
    var gadId: String
        get() = getInstance()?.getString(GAD_ID_KEY, "")!!
        set(value) = getInstance()?.edit()!!.putString(GAD_ID_KEY, value).apply()

    private const val BACKEND_URL = "BACKEND_URL"
    var backendUrl: String
        get() = getInstance()?.getString(BACKEND_URL, "")!!
        set(value) = getInstance()?.edit()!!.putString(BACKEND_URL, value).apply()
}