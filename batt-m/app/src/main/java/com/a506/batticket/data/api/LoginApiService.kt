package com.a506.batticket.data.api

import com.a506.batticket.data.model.JwtResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequestDto (
    val provider: String,
    val accessToken: String,
)
interface LoginApiService {
    @POST("/api/v1/token/mobile-login")
    fun getJwtToken(@Body request: LoginRequestDto): Call<JwtResponseDto>
}