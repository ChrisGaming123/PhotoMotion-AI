package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE email = :email LIMIT 1")
    fun getPreferences(email: String): Flow<UserPreferences?>

    @Query("SELECT * FROM user_preferences WHERE email = :email LIMIT 1")
    suspend fun getPreferencesOnce(email: String): UserPreferences?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preferences: UserPreferences)
}
