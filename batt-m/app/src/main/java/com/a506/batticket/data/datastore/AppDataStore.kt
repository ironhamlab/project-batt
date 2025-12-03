package com.a506.batticket.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey

class AppDataStore(private val context: Context) {
    companion object {
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
    }

    suspend fun saveAccesstoken(token: String) {

    }



}