package com.a506.batticket.presentation.login

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.a506.batticket.VerificationActivity
import com.a506.batticket.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var naverLoginLauncher: ActivityResultLauncher<Intent>
    private lateinit var memberViewModel: MemberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memberViewModel = ViewModelProvider(this)[MemberViewModel::class.java]

        memberViewModel.jwtToken.observe(this) { token ->
            if (token != null) {
                Log.d("token", "저장된 토큰: $token")
                openVerificationScreen()
            }
        }
        // 카카오 로그인 버튼 클릭 시
        binding.kakaoLoginButton.setOnClickListener {
            // (실제로는 카카오 SDK 로그인 로직 호출 후 성공 시 화면 전환)
            kakaoLogin(this)

        }

        // 네이버 로그인 버튼 클릭 시
        binding.naverLoginButton.setOnClickListener {
            // (실제로는 네이버 SDK 로그인 로직 호출 후 성공 시 화면 전환)
            naverLogin()
        }
    }
    private fun initActivity() {
        naverLoginLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d("naver", result.toString())
            when (result.resultCode) {
                RESULT_OK -> {
                    val accessToken = NaverIdLoginSDK.getAccessToken()
                    val refresshToken = NaverIdLoginSDK.getRefreshToken()
                    val expiresAt = NaverIdLoginSDK.getExpiresAt()
                    val tokenType= NaverIdLoginSDK.getTokenType()
                    val state = NaverIdLoginSDK.getState()
                    Log.d("naver login", "accessToken=$accessToken")
                    memberViewModel.getJwtToken("naver", accessToken.toString())

                }
                RESULT_CANCELED -> {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Log.e("naver login", "errorCode: $errorCode, errorDesc:$errorDescription")
                }
            }
        }
    }

    private fun kakaoLogin(context: Context) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("kakao", "로그인 실패", error)
            }
            else if (token != null) {
                Log.i("kakao", "로그인 성공 ${token.accessToken}")
                memberViewModel.getJwtToken("kakao", token.accessToken)
            }

        }
        if (UserApiClient.Companion.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.Companion.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.Companion.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i(ContentValues.TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    memberViewModel.getJwtToken("kakao", token.accessToken)

                }
            }
        } else {
            UserApiClient.Companion.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }
    private fun naverLogin() {
        NaverIdLoginSDK.authenticate(this, naverLoginLauncher)
    }
    // 다음 화면으로 넘어가는 함수
    private val openVerificationScreen = {
        // Intent: 다른 컴포넌트(액티비티)를 실행하기 위한 메시지 객체
        val intent = Intent(this, VerificationActivity::class.java)
        startActivity(intent)
    }

}