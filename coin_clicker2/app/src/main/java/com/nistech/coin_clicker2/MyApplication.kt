package com.nistech.coin_clicker2

import android.app.Application


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        sInstance = this
    }


    companion object {
        private lateinit var sInstance: MyApplication

        fun getInstance(): MyApplication {
            return sInstance
        }
    }

}