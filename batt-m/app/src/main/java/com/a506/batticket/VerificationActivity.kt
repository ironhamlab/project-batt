package com.a506.batticket

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.a506.batticket.presentation.auth.TossWebViewActivity
import com.a506.batticket.presentation.ticket.TicketListActivity
import com.a506.batticket.service.TossAuthService
import kotlinx.coroutines.launch

class VerificationActivity : AppCompatActivity() {
    
    private lateinit var tossAuthService: TossAuthService
    private var currentTxId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        tossAuthService = TossAuthService.getInstance()
        
        val verifyButton: Button = findViewById(R.id.verifyButton)

        // 본인인증 하러가기 버튼
        verifyButton.setOnClickListener {
            startTossVerification()
        }
    }
    
    /**
     * 토스 본인인증 시작
     */
    private fun startTossVerification() {
        lifecycleScope.launch {
            try {
                // 로딩 상태 표시
                Toast.makeText(this@VerificationActivity, "본인인증을 준비 중입니다...", Toast.LENGTH_SHORT).show()
                
                // 토스 본인확인 요청
                val result = tossAuthService.requestUserAuth()
                
                result.onSuccess { response ->
                    val txId = response.success?.txId
                    val authUrl = response.success?.authUrl
                    
                    Log.d(TAG, "토스 인증 요청 성공: txId=$txId")
                    
                    // txId와 authUrl 유효성 검증
                    if (txId.isNullOrEmpty() || authUrl.isNullOrEmpty()) {
                        Log.e(TAG, "토스 API 응답 데이터 오류: txId=$txId, authUrl=$authUrl")
                        Toast.makeText(this@VerificationActivity, 
                            "본인인증 서비스에 일시적인 문제가 있습니다. 잠시 후 다시 시도해주세요.", 
                            Toast.LENGTH_LONG).show()
                        return@onSuccess
                    }
                    
                    currentTxId = txId
                    
                    // 토스 표준창 WebView 실행
                    val intent = TossWebViewActivity.createIntent(
                        this@VerificationActivity,
                        txId,
                        authUrl
                    )
                    
                    if (intent != null) {
                        startActivityForResult(intent, TossWebViewActivity.REQUEST_CODE_TOSS_AUTH)
                    } else {
                        Toast.makeText(this@VerificationActivity, 
                            "본인인증 화면을 열 수 없습니다. 다시 시도해주세요.", 
                            Toast.LENGTH_SHORT).show()
                    }
                    
                }.onFailure { exception ->
                    Log.e(TAG, "토스 인증 요청 실패", exception)
                    Toast.makeText(this@VerificationActivity, 
                        "본인인증 요청에 실패했습니다: ${exception.message}", 
                        Toast.LENGTH_LONG).show()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "본인인증 시작 중 오류", e)
                Toast.makeText(this@VerificationActivity, 
                    "본인인증 시작 중 오류가 발생했습니다.", 
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == TossWebViewActivity.REQUEST_CODE_TOSS_AUTH) {
            val txId = data?.getStringExtra(TossWebViewActivity.EXTRA_TX_ID) ?: ""
            val result = data?.getStringExtra(TossWebViewActivity.EXTRA_RESULT) ?: ""
            
            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (result == "SUCCESS") {
                        Log.d(TAG, "토스 본인인증 성공: txId=$txId")
                        handleAuthSuccess(txId)
                    } else {
                        Log.d(TAG, "토스 본인인증 실패: txId=$txId")
                        handleAuthFail("인증에 실패했습니다.")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    when (result) {
                        "TIMEOUT" -> {
                            Log.d(TAG, "토스 본인인증 타임아웃")
                            handleAuthFail("인증 시간이 초과되었습니다. 다시 시도해주세요.")
                        }
                        else -> {
                            Log.d(TAG, "토스 본인인증 취소됨")
                            handleAuthFail("인증이 취소되었습니다.")
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 인증 성공 처리
     */
    private fun handleAuthSuccess(txId: String) {
        Log.d(TAG, "본인인증 완료: txId=$txId")
        
        // 선택사항: 개인정보가 필요한 경우에만 결과 조회 API 호출
        // 현재는 바로 성공 처리하여 안정성 확보
        proceedToNextScreen()
        
        /*
        // 만약 개인정보 데이터가 꼭 필요하다면 아래 코드 활성화
        lifecycleScope.launch {
            try {
                val result = tossAuthService.getUserAuthResult(txId)
                result.onSuccess { response ->
                    // 개인정보 처리 로직
                    Log.d(TAG, "개인정보 조회 성공: ${response.personalData?.name}")
                }.onFailure { exception ->
                    Log.w(TAG, "개인정보 조회 실패하지만 인증은 완료된 상태", exception)
                }
            } catch (e: Exception) {
                Log.w(TAG, "개인정보 조회 중 오류하지만 인증은 완료된 상태", e)
            }
            // 어떤 경우든 성공 처리
            proceedToNextScreen()
        }
        */
    }
    
    /**
     * 다음 화면으로 이동
     */
    private fun proceedToNextScreen() {
        Toast.makeText(this@VerificationActivity, 
            "본인인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this@VerificationActivity, TicketListActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    /**
     * 인증 실패 처리
     */
    private fun handleAuthFail(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.w(TAG, "본인인증 실패: $message")
    }
    
    companion object {
        private const val TAG = "VerificationActivity"
    }
}