package com.a506.batticket

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("my_token", Context.MODE_PRIVATE)
    fun getToken(key: String, defValue: String) : String {
        return preferences.getString(key, defValue).toString()
    }
    fun setToken(key: String, defValue: String){
        preferences.edit().putString(key, defValue).apply()
    }
}