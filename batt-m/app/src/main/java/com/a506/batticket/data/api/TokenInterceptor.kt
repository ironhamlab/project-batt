package com.a506.batticket.data.api

import com.a506.batticket.data.datastore.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor (
    private val authRepository: AuthRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val accessToken : String? = authRepository.jwtTokenFlow.first()
            val originalRequest = chain.request()

            val url = originalRequest.url.encodedPath
            if (url.startsWith("/api/v1/token/mobile-login")) {
                return@runBlocking chain.proceed(originalRequest)
            }

            val newRequest = if (accessToken != null) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            } else {
                originalRequest
            }
            chain.proceed(newRequest)
        }
    }
}