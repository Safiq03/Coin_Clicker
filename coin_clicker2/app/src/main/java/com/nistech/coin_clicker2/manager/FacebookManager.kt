package com.nistech.coin_clicker2.manager

import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.nistech.coin_clicker2.utils.removeScheme
import com.nistech.coin_clicker2.storage.StorageManager

class FacebookManager(
    private val context: Context,
    private val preferences: StorageManager
    ) {

    fun getDeeplinkData() {
        FacebookSdk.setApplicationId(FACEBOOK_KEY)
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        AppLinkData.fetchDeferredAppLinkData(context) { appLinkData: AppLinkData? ->
            appLinkData?.let { data ->
                val targetUri = modifyString(data.targetUri.toString()) { naming ->
                    naming.removeScheme()
                }
                preferences.deeplink = targetUri
            }
        }
    }

    private fun modifyString(deeplink: String, ignoredModify: (String) -> String): String {
        return ignoredModify(deeplink)
    }

    companion object {
        private const val FACEBOOK_KEY = "848323463469539"
    }

}