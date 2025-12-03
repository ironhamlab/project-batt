package com.a506.batticket.data.api

import com.a506.batticket.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 토스 본인인증 API 서비스
 */
interface TossAuthApiService {
    
    /**
     * Access Token 발급
     */
    @FormUrlEncoded
    @POST("token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("scope") scope: String = "ca",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Response<AccessTokenResponse>
    
    /**
     * 본인확인 요청
     */
    @POST("api/v2/sign/user/auth/id/request")
    suspend fun requestUserAuth(
        @Header("Authorization") authorization: String,
        @Body request: UserAuthRequest
    ): Response<UserAuthResponse>
    
    /**
     * 본인확인 상태조회
     */
    @POST("api/v2/sign/user/auth/id/status")
    @Headers("Content-Type: application/json")
    suspend fun getUserAuthStatus(
        @Header("Authorization") authorization: String,
        @Body request: UserAuthStatusRequest
    ): Response<UserAuthStatusResponse>
    
    /**
     * 본인확인 결과조회
     */
    @GET("api/v2/sign/user/auth/id/{txId}/result")
    suspend fun getUserAuthResult(
        @Header("Authorization") authorization: String,
        @Path("txId") txId: String
    ): Response<UserAuthResultResponse>
}
