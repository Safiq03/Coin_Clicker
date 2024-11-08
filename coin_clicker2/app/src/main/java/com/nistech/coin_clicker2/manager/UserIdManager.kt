package com.nistech.coin_clicker2.manager

import com.nistech.coin_clicker2.storage.StorageManager
import java.util.UUID

class UserIdManager(
    private val preferences: StorageManager
) {
    init {
        if (StorageManager.userId.isEmpty()  ) {
            StorageManager.userId = UUID.randomUUID().toString()
        }
    }

}