package com.a506.batticket.data.api

import android.content.Context
import com.a506.batticket.data.datastore.AuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService(private val context: Context) {
    private lateinit var retrofit: Retrofit

    init {
        initializeRetrofit()
    }

    private fun initializeRetrofit(){
        val authRepository = AuthRepository(context)
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(TokenInterceptor(authRepository))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://i13a506.p.ssafy.io/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getRetrofit() : Retrofit { return retrofit }
}