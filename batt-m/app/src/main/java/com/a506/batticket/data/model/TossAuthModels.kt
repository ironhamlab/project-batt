package com.a506.batticket.data.model

import com.google.gson.annotations.SerializedName

/**
 * Access Token 응답
 */
data class AccessTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("scope")
    val scope: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Long
)

/**
 * 본인확인 요청
 */
data class UserAuthRequest(
    @SerializedName("requestType")
    val requestType: String = "USER_NONE",
    @SerializedName("requestUrl")
    val requestUrl: String? = null
)

/**
 * 본인확인 상태조회 요청
 */
data class UserAuthStatusRequest(
    @SerializedName("txId")
    val txId: String
)

/**
 * 본인확인 요청 응답
 */
data class UserAuthResponse(
    @SerializedName("resultType")
    val resultType: String,
    @SerializedName("success")
    val success: UserAuthSuccessData?
)

/**
 * 본인확인 성공 데이터
 */
data class UserAuthSuccessData(
    @SerializedName("txId")
    val txId: String?,
    @SerializedName("appScheme")
    val appScheme: String?,
    @SerializedName("androidAppUri")
    val androidAppUri: String?,
    @SerializedName("iosAppUri")
    val iosAppUri: String?,
    @SerializedName("requestedDt")
    val requestedDt: String?,
    @SerializedName("authUrl")
    val authUrl: String?
)

/**
 * 본인확인 상태조회 응답
 */
data class UserAuthStatusResponse(
    @SerializedName("resultType")
    val resultType: String,
    @SerializedName("success")
    val success: UserAuthStatusData?
)

/**
 * 본인확인 상태 데이터
 */
data class UserAuthStatusData(
    @SerializedName("txId")
    val txId: String?,
    @SerializedName("status")
    val status: String?, // REQUESTED, COMPLETED 등
    @SerializedName("requestedDt")
    val requestedDt: String?
)

/**
 * 본인확인 결과조회 응답
 */
data class UserAuthResultResponse(
    @SerializedName("resultType")
    val resultType: String,
    @SerializedName("txId")
    val txId: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("personalData")
    val personalData: PersonalData?
)

/**
 * 개인정보 데이터
 */
data class PersonalData(
    @SerializedName("ci")
    val ci: String?,
    @SerializedName("di")
    val di: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("birthday")
    val birthday: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("phone")
    val phone: String?
)
