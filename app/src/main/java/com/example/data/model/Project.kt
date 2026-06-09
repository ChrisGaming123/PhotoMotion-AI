package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val photoBase64: String, // Hold original image base64
    val promptText: String,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "done", // "processing", "done", "failed"
    val stage1Description: String = "",
    val stage2Description: String = "",
    val stage3Description: String = "",
    val aiSummary: String = ""
)
