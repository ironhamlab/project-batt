package com.a506.batticket.service

import android.util.Log
import com.a506.batticket.BuildConfig
import com.a506.batticket.data.api.TossAuthApiService
import com.a506.batticket.data.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * 토스 본인인증 서비스
 */
class TossAuthService {
    
    private val apiService: TossAuthApiService
    
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(TOSS_CERT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        apiService = retrofit.create(TossAuthApiService::class.java)
    }
    
    /**
     * 본인확인 요청 시작
     */
    suspend fun requestUserAuth(): Result<UserAuthResponse> {
        return try {
            val accessToken = BuildConfig.TOSS_CERT_ACCESS_TOKEN
            Log.d(TAG, "토스 인증 토큰: ${accessToken.take(50)}...")
            
            val request = UserAuthRequest(
                requestType = "USER_NONE",
                requestUrl = "https://cert.toss.im" // 테스트용 URL 추가
            )
            
            Log.d(TAG, "본인인증 요청 데이터: $request")
            
            val response = apiService.requestUserAuth(
                authorization = "Bearer $accessToken",
                request = request
            )
            
            Log.d(TAG, "HTTP 응답 코드: ${response.code()}")
            Log.d(TAG, "응답 성공 여부: ${response.isSuccessful}")
            if (!response.isSuccessful) {
                Log.e(TAG, "응답 헤더: ${response.headers()}")
            }
            
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val txId = body.success?.txId
                    val authUrl = body.success?.authUrl
                    
                    Log.d(TAG, "본인확인 요청 응답: resultType=${body.resultType}")
                    Log.d(TAG, "성공 데이터: txId=$txId, authUrl=$authUrl")
                    
                    // txId가 null이거나 빈 문자열인 경우 오류 처리
                    if (txId.isNullOrEmpty() || authUrl.isNullOrEmpty()) {
                        Log.e(TAG, "토스 API 응답에서 필수 데이터가 없습니다: success=${body.success}")
                        Result.failure(Exception("토스 인증 요청에 실패했습니다. 잠시 후 다시 시도해주세요."))
                    } else {
                        Log.d(TAG, "본인확인 요청 성공: txId=$txId")
                        Result.success(body)
                    }
                } ?: run {
                    Log.e(TAG, "응답 body가 null입니다. HTTP Code: ${response.code()}")
                    Result.failure(Exception("서버 응답을 처리할 수 없습니다."))
                }
            } else {
                Log.e(TAG, "본인확인 요청 실패: ${response.code()} - ${response.message()}")
                Log.e(TAG, "응답 내용: ${response.errorBody()?.string()}")
                Result.failure(Exception("본인확인 요청 실패: ${response.message()}"))
            }
        } catch (e: IOException) {
            Log.e(TAG, "네트워크 오류", e)
            Result.failure(Exception("네트워크 연결을 확인해주세요"))
        } catch (e: Exception) {
            Log.e(TAG, "본인확인 요청 중 오류 발생", e)
            Result.failure(e)
        }
    }
    
    /**
     * 본인확인 상태 조회
     */
    suspend fun getUserAuthStatus(txId: String): Result<UserAuthStatusResponse> {
        return try {
            val accessToken = BuildConfig.TOSS_CERT_ACCESS_TOKEN
            Log.d(TAG, "본인확인 상태조회 시작: txId=$txId")
            
            val request = UserAuthStatusRequest(txId = txId)
            
            val response = apiService.getUserAuthStatus(
                authorization = "Bearer $accessToken",
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val status = body.success?.status
                    Log.d(TAG, "본인확인 상태조회 성공: resultType=${body.resultType}, status=$status")
                    Result.success(body)
                } ?: run {
                    Log.e(TAG, "상태조회 응답 body가 null")
                    Result.failure(Exception("상태조회 응답을 처리할 수 없습니다"))
                }
            } else {
                Log.e(TAG, "본인확인 상태조회 실패: ${response.code()} - ${response.message()}")
                Result.failure(Exception("본인확인 상태조회 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "본인확인 상태조회 중 오류 발생", e)
            Result.failure(e)
        }
    }
    
    /**
     * 본인확인 결과 조회
     */
    suspend fun getUserAuthResult(txId: String): Result<UserAuthResultResponse> {
        return try {
            val accessToken = BuildConfig.TOSS_CERT_ACCESS_TOKEN
            
            val response = apiService.getUserAuthResult(
                authorization = "Bearer $accessToken",
                txId = txId
            )
            
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Log.d(TAG, "본인확인 결과조회 성공: success=${body.success}")
                    Result.success(body)
                } ?: Result.failure(Exception("응답 데이터가 없습니다"))
            } else {
                Log.e(TAG, "본인확인 결과조회 실패: ${response.code()} - ${response.message()}")
                Result.failure(Exception("본인확인 결과조회 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "본인확인 결과조회 중 오류 발생", e)
            Result.failure(e)
        }
    }
    
    companion object {
        private const val TAG = "TossAuthService"
        private const val TOSS_CERT_BASE_URL = "https://cert.toss.im/"
        
        @Volatile
        private var INSTANCE: TossAuthService? = null
        
        fun getInstance(): TossAuthService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TossAuthService().also { INSTANCE = it }
            }
        }
    }
}
