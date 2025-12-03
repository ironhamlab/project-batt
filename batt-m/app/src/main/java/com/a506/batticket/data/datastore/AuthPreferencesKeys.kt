package com.a506.batticket.data.datastore

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AuthPreferencesKeys {
    val JWT_TOKEN = stringPreferencesKey("jwt_token")
    val MEMBER_ID = longPreferencesKey("member_id")
}