package com.example.data.repository

import com.example.data.database.UserPreferencesDao
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

class PreferenceRepository(private val dao: UserPreferencesDao) {
    fun getPreferences(email: String): Flow<UserPreferences?> = dao.getPreferences(email)
    suspend fun getPreferencesOnce(email: String): UserPreferences? = dao.getPreferencesOnce(email)
    suspend fun savePreferences(preferences: UserPreferences) = dao.savePreferences(preferences)
}
