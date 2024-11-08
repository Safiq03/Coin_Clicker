package com.nistech.coin_clicker2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nistech.coin_clicker2.manager.AdvertisingManager
import com.nistech.coin_clicker2.manager.FacebookManager
import com.nistech.coin_clicker2.manager.UserIdManager
import com.nistech.coin_clicker2.storage.StorageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LaunchActivity : AppCompatActivity(R.layout.activity_launch) {
    private var isRun: Boolean = false
    private var runIterator: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (StorageManager.backendUrl.isNotEmpty()) {
            startMain()
        } else {
            progressLoader()
        }
    }

    private fun progressLoader() {
        UserIdManager(StorageManager)
        val facebookManager = FacebookManager(this, StorageManager)
        facebookManager.getDeeplinkData()

        getAdvertisingId()
        CoroutineScope(Dispatchers.Default).launch {
            while (!isRun) {
                if (
                    runIterator >= 100 || StorageManager.deeplink.isNotEmpty()
                ) {
                    isRun = true
                    runOnUiThread {
                        startMain()
                    }
                    break
                }
                runIterator++
                kotlinx.coroutines.delay(110)
            }
        }
    }

    private fun getAdvertisingId() {
        CoroutineScope(Dispatchers.IO).launch {
            StorageManager.gadId =
                AdvertisingManager().getAdvertising(this@LaunchActivity) ?: ""
        }
    }

    private fun startMain() {
        startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
        finish()
    }
}