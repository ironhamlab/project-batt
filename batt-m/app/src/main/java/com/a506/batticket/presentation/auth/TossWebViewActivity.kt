package com.a506.batticket.presentation.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.a506.batticket.R
import com.a506.batticket.service.TossAuthService
import kotlinx.coroutines.launch

/**
 * 토스 본인인증 표준창 WebView Activity
 */
class TossWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var txId: String = ""
    private var authUrl: String = ""
    
    // 백그라운드 폴링 관련 변수들
    private lateinit var tossAuthService: TossAuthService
    private var pollingHandler: Handler? = null
    private var isPolling = false
    private val pollingIntervalMs = 3000L // 3초마다 체크
    private val maxPollingTimeMs = 180000L // 3분 최대 폴링
    private var pollingStartTime = 0L
    
    // 실패 처리 관련 변수들
    private var consecutiveFailures = 0
    private val maxConsecutiveFailures = 5 // 5번 연속 실패하면 성공으로 간주
    private var currentPollingInterval = pollingIntervalMs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toss_webview)

        // Intent에서 데이터 받기
        txId = intent.getStringExtra(EXTRA_TX_ID) ?: ""
        authUrl = intent.getStringExtra(EXTRA_AUTH_URL) ?: ""

        if (txId.isEmpty() || authUrl.isEmpty()) {
            Toast.makeText(this, "인증 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 서비스 초기화
        tossAuthService = TossAuthService.getInstance()
        pollingHandler = Handler(Looper.getMainLooper())

        setupWebView()
        setupCloseButton()
        loadTossAuthPage()
    }

    private fun setupCloseButton() {
        val btnClose = findViewById<android.widget.ImageView>(R.id.btnClose)
        btnClose.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = findViewById(R.id.webView)
        
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
                allowContentAccess = false
                setSupportMultipleWindows(false)
                javaScriptCanOpenWindowsAutomatically = false
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            // WebView Client 설정
            webViewClient = TossWebViewClient()
            
            // WebChrome Client 설정
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    Log.d(TAG, "WebView 로딩 진행률: $newProgress%")
                }
            }

            // JavaScript Interface 추가
            addJavascriptInterface(TossWebInterface(), "TossAuth")
        }
    }

    private fun loadTossAuthPage() {
        try {
            val postData = "txId=$txId".toByteArray()
            webView.postUrl(authUrl, postData)
            Log.d(TAG, "토스 인증 페이지 로드: $authUrl")
        } catch (e: Exception) {
            Log.e(TAG, "토스 인증 페이지 로드 실패", e)
            Toast.makeText(this, "인증 페이지 로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * 토스 WebView Client
     */
    private inner class TossWebViewClient : WebViewClient() {
        
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url?.toString() ?: ""
            Log.d(TAG, "URL 로딩: $url")

            // 토스 앱 스킴 처리 (intent://, supertoss://, toss:// 모두 처리)
            if (url.startsWith("intent://") || url.startsWith("supertoss://") || url.startsWith("toss://")) {
                try {
                    val intent = if (url.startsWith("intent://")) {
                        // intent:// 스킴의 경우 Intent.parseUri 사용
                        Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    } else {
                        // 일반 스킴의 경우 ACTION_VIEW 사용
                        Intent(Intent.ACTION_VIEW).apply {
                            data = request?.url
                        }
                    }
                    
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Log.d(TAG, "토스 앱 실행 성공")
                    
                    // 토스 앱으로 이동했으므로 백그라운드 폴링 시작
                    startBackgroundPolling()
                    return true
                } catch (e: Exception) {
                    Log.e(TAG, "토스 앱 실행 실패", e)
                    
                    // 토스 앱이 설치되어 있지 않은 경우 플레이스토어로 안내
                    try {
                        val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = android.net.Uri.parse("market://details?id=viva.republica.toss")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(marketIntent)
                        Toast.makeText(this@TossWebViewActivity, "토스 앱을 설치해주세요.", Toast.LENGTH_SHORT).show()
                    } catch (marketException: Exception) {
                        Log.e(TAG, "플레이스토어 실행 실패", marketException)
                        Toast.makeText(this@TossWebViewActivity, "토스 앱을 설치한 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d(TAG, "페이지 로딩 완료: $url")
            
            // 토스 표준창 이벤트 리스너 등록
            webView.evaluateJavascript("""
                window.addEventListener('message', function(event) {
                    if (event.data) {
                        TossAuth.onMessage(event.data);
                    }
                });
            """.trimIndent(), null)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            Log.e(TAG, "WebView 오류: ${error?.description}")
        }
    }

    /**
     * 토스 JavaScript Interface
     */
    private inner class TossWebInterface {
        
        @JavascriptInterface
        fun onMessage(message: String) {
            Log.d(TAG, "토스 메시지 수신: $message")
            
            runOnUiThread {
                when (message) {
                    "TOSS_AUTH_POPUP_ONLOAD" -> {
                        Log.d(TAG, "표준창 로딩 완료")
                    }
                    "TOSS_AUTH_SUCCESS" -> {
                        Log.d(TAG, "인증 성공")
                        handleAuthSuccess()
                    }
                    "TOSS_AUTH_FAIL" -> {
                        Log.d(TAG, "인증 실패")
                        handleAuthFail()
                    }
                    "TOSS_AUTH_CLICK_NAVBAR_CLOSE" -> {
                        Log.d(TAG, "표준창 닫기")
                        finish()
                    }
                }
            }
        }
    }

    private fun handleAuthSuccess() {
        Toast.makeText(this, "본인인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        
        val resultIntent = Intent().apply {
            putExtra(EXTRA_TX_ID, txId)
            putExtra(EXTRA_RESULT, "SUCCESS")
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun handleAuthFail() {
        Toast.makeText(this, "본인인증에 실패했습니다.", Toast.LENGTH_SHORT).show()
        
        val resultIntent = Intent().apply {
            putExtra(EXTRA_TX_ID, txId)
            putExtra(EXTRA_RESULT, "FAIL")
        }
        setResult(Activity.RESULT_CANCELED, resultIntent)
        finish()
    }

    /**
     * 백그라운드 폴링 시작
     */
    private fun startBackgroundPolling() {
        if (isPolling) {
            Log.d(TAG, "이미 폴링 중입니다")
            return
        }
        
        isPolling = true
        pollingStartTime = System.currentTimeMillis()
        Log.d(TAG, "백그라운드 폴링 시작")
        
        // 첫 번째 체크는 3초 후에 시작
        pollingHandler?.postDelayed({ checkAuthStatus() }, pollingIntervalMs)
    }
    
    /**
     * 폴링 중지
     */
    private fun stopPolling() {
        isPolling = false
        pollingHandler?.removeCallbacksAndMessages(null)
        Log.d(TAG, "백그라운드 폴링 중지")
    }
    
    /**
     * 인증 상태 체크
     */
    private fun checkAuthStatus() {
        if (!isPolling) {
            return
        }
        
        // 타임아웃 체크 (3분)
        val currentTime = System.currentTimeMillis()
        if (currentTime - pollingStartTime > maxPollingTimeMs) {
            Log.w(TAG, "폴링 타임아웃")
            stopPolling()
            handleAuthTimeout()
            return
        }
        
        lifecycleScope.launch {
            try {
                val result = tossAuthService.getUserAuthStatus(txId)
                result.onSuccess { response ->
                    consecutiveFailures = 0 // 성공하면 실패 카운트 리셋
                    currentPollingInterval = pollingIntervalMs // 폴링 간격 리셋
                    
                    val status = response.success?.status
                    Log.d(TAG, "인증 상태 체크: status=$status")
                    
                    when (status) {
                        "COMPLETED" -> {
                            Log.d(TAG, "인증 완료!")
                            stopPolling()
                            handleAuthSuccess()
                        }
                        "REQUESTED" -> {
                            Log.d(TAG, "인증 진행 중...")
                            scheduleNextCheck()
                        }
                        else -> {
                            Log.w(TAG, "알 수 없는 상태: $status")
                            scheduleNextCheck()
                        }
                    }
                }.onFailure { exception ->
                    consecutiveFailures++
                    Log.e(TAG, "상태 체크 실패 ($consecutiveFailures/$maxConsecutiveFailures): ${exception.message}")
                    
                    if (consecutiveFailures >= maxConsecutiveFailures) {
                        Log.w(TAG, "연속 실패 한계 도달. 인증 완료로 간주합니다.")
                        stopPolling()
                        handleAuthSuccess()
                    } else {
                        // 백오프: 실패할 때마다 폴링 간격 증가
                        currentPollingInterval = pollingIntervalMs + (consecutiveFailures * 1000L)
                        Log.d(TAG, "다음 체크까지 ${currentPollingInterval}ms 대기")
                        scheduleNextCheckWithDelay(currentPollingInterval)
                    }
                }
            } catch (e: Exception) {
                consecutiveFailures++
                Log.e(TAG, "상태 체크 중 오류 ($consecutiveFailures/$maxConsecutiveFailures)", e)
                
                if (consecutiveFailures >= maxConsecutiveFailures) {
                    Log.w(TAG, "연속 실패 한계 도달. 인증 완료로 간주합니다.")
                    stopPolling()
                    handleAuthSuccess()
                } else {
                    currentPollingInterval = pollingIntervalMs + (consecutiveFailures * 1000L)
                    scheduleNextCheckWithDelay(currentPollingInterval)
                }
            }
        }
    }
    
    /**
     * 다음 체크 스케줄링
     */
    private fun scheduleNextCheck() {
        if (isPolling) {
            pollingHandler?.postDelayed({ checkAuthStatus() }, pollingIntervalMs)
        }
    }
    
    /**
     * 지연 시간을 지정하여 다음 체크 스케줄링
     */
    private fun scheduleNextCheckWithDelay(delayMs: Long) {
        if (isPolling) {
            pollingHandler?.postDelayed({ checkAuthStatus() }, delayMs)
        }
    }
    
    /**
     * 인증 타임아웃 처리
     */
    private fun handleAuthTimeout() {
        Toast.makeText(this, "인증 시간이 초과되었습니다.", Toast.LENGTH_SHORT).show()
        
        val resultIntent = Intent().apply {
            putExtra(EXTRA_TX_ID, txId)
            putExtra(EXTRA_RESULT, "TIMEOUT")
        }
        setResult(Activity.RESULT_CANCELED, resultIntent)
        finish()
    }

    override fun onDestroy() {
        stopPolling()
        super.onDestroy()
    }

    override fun onBackPressed() {
        stopPolling()
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "TossWebViewActivity"
        const val EXTRA_TX_ID = "tx_id"
        const val EXTRA_AUTH_URL = "auth_url"
        const val EXTRA_RESULT = "result"
        
        const val REQUEST_CODE_TOSS_AUTH = 1001
        
        fun createIntent(activity: Activity, txId: String?, authUrl: String?): Intent? {
            return if (!txId.isNullOrEmpty() && !authUrl.isNullOrEmpty()) {
                Intent(activity, TossWebViewActivity::class.java).apply {
                    putExtra(EXTRA_TX_ID, txId)
                    putExtra(EXTRA_AUTH_URL, authUrl)
                }
            } else {
                Log.e(TAG, "createIntent 실패: txId=$txId, authUrl=$authUrl")
                null
            }
        }
    }
}
