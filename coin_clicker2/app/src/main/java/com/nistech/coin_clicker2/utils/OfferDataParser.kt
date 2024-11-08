package com.nistech.coin_clicker2.utils

import android.content.Context
import com.nistech.coin_clicker2.storage.StorageManager

class OfferDataParser(
    private val context: Context,
    private val preferences: StorageManager
) {
    private val params = listOf(
        "dp1", "dp2", "dp3", "dp4", "dp5", "dp6", "dp7"
    )

    fun splitString(naming: String, link: String): String {
        var url = link

        if (naming.isNotEmpty() && naming.isNotEmpty()) {
            val array = naming.split("_").toTypedArray()

            for (i in array.indices) {
                val param = params.getOrNull(i)
                if (param != null && url.contains(param)) {
                    url = url.replace(param, array[i])
                }
            }
        }

        url = replaceStaticParams(url)

        return url
    }

    private fun replaceStaticParams(
        url: String
    ): String {
        val params = mapOf(
            ADV_ID to preferences.gadId,
            BUNDLE to context.packageName,
            APPS_ID to preferences.userId
        )

        var subUrl = url
        for ((placeholder, value) in params) {
            subUrl = subUrl.replace(placeholder, value)
        }

        return subUrl
    }

    companion object {
        private const val BUNDLE = "{bundle}"
        private const val APPS_ID = "{userId}"
        private const val ADV_ID = "{gadId}"
    }
}

