package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val email: String,
    val theme: String = "dark", // "light", "dark", "system"
    val defaultDurationSeconds: Int = 8,
    val motionIntensity: String = "medium", // "low", "medium", "high"
    val enableCloudBackup: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
