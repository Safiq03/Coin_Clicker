package com.nistech.coin_clicker2.manager

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdvertisingManager {

    suspend fun getAdvertising(context: Context): String? {
        return withContext(Dispatchers.Default) {
            try {
                AdvertisingIdClient.getAdvertisingIdInfo(context).id
            } catch (exception: Exception) {
                null
            }
        }
    }
}