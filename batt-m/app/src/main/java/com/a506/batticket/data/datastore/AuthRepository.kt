package com.a506.batticket.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context) {
    suspend fun saveUserInfo(token: String, userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[AuthPreferencesKeys.JWT_TOKEN] = token
            prefs[AuthPreferencesKeys.MEMBER_ID] = userId
        }
    }
    val jwtTokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[AuthPreferencesKeys.JWT_TOKEN]
        }

    val userIdFlow: Flow<Long?> = context.dataStore.data
        .map { prefs ->
            prefs[AuthPreferencesKeys.MEMBER_ID]
        }
}