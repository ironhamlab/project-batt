package com.a506.batticket

import android.app.Application
import android.content.Context
import android.preference.Preference
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.navercorp.nid.NaverIdLoginSDK
import java.util.prefs.Preferences

class BATTApplication : Application() {
    override fun onCreate() {
        super.onCreate()

//        val dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokens")

        KakaoSdk.init(this, BuildConfig.NATIVE_APP_KEY)
//        NaverIdLoginSDK.init(this)
        NaverIdLoginSDK.initialize(this, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET_KEY, "BATT")
//        var keyHash = Utility.getKeyHash(this)
//        Log.d("key hash", keyHash )
    }
}