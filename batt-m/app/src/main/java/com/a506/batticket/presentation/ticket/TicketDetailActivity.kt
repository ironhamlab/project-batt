package com.a506.batticket.presentation.ticket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.a506.batticket.R
import com.a506.batticket.data.datastore.AuthRepository
import com.a506.batticket.databinding.ActivityTicketDetailBinding
import com.a506.batticket.presentation.ticket.TicketViewModel
import com.a506.batticket.domain.model.Performance
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TicketDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketDetailBinding
    private lateinit var viewModel: TicketViewModel
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var performanceTitleText: TextView
    private lateinit var performanceInfoText: TextView
    private lateinit var authRepository: AuthRepository
    private var bookingId: Long = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authRepository = AuthRepository(applicationContext)

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE
//        )

        initViews()
        observeLiveData()
        Log.d("bookingId", bookingId.toString())
        loadTicket()
    }
    
    private fun initViews() {
        viewModel = ViewModelProvider(this)[TicketViewModel::class.java]
        bookingId = intent.getLongExtra("bookingId", 0)
        Log.d("bookingId", bookingId.toString())
        viewPager = findViewById(R.id.ticketViewPager)
        tabLayout = findViewById(R.id.ticketTabLayout)
        performanceTitleText = findViewById(R.id.performanceTitleText)
        performanceInfoText = findViewById(R.id.performanceInfoText)
    }

    private fun observeLiveData() {
        viewModel.ticketDetail.observe(this) {
            setupData(it)
        }
    }
    private fun loadTicket() {
        lifecycleScope.launch {
            var memberId : Long? = authRepository.userIdFlow.first()
            viewModel.getTicketDetail(memberId ?: 1, bookingId)
        }
    }
    
    private fun setupData(performance: Performance) {
        // Intent에서 공연 데이터 받아오기 (실제로는 Serializable이나 Parcelable 사용)
//        val performanceId = intent.getStringExtra("performanceId") ?: ""
        
        // 목데이터에서 해당 공연 찾기
//        val performance = getMockPerformances().find { it.id == performanceId }
        binding.performanceTitleText.text = performance.title
        binding.performanceInfoText.text = "${performance.showDateTime} | ${performance.venue}"

        binding.ticketViewPager.adapter = TicketPagerAdapter(performance.tickets)
//        performance?.let {
//            performanceTitleText.text = it.title
//            performanceInfoText.text = "${it.date} ${it.time} | ${it.venue}"
//
//            // ViewPager2 어댑터 설정
//            val adapter = TicketPagerAdapter(it.tickets)
//            viewPager.adapter = adapter
//
            // TabLayout과 ViewPager2 연결
        TabLayoutMediator(binding.ticketTabLayout, binding.ticketViewPager) { tab, position ->
            tab.text = "${position + 1}번째 티켓"
        }.attach()
//        }
    }
    
//    private fun getMockPerformances(): List<Performance> {
//        // 실제로는 TicketListActivity에서 전달받거나 공통 데이터 소스 사용
//        return listOf(
//            Performance(
//                id = "perf_001",
//                title = "뮤지컬 라이온킹",
//                date = "2024.12.25 (수)",
//                time = "19:30",
//                venue = "샤롯데씨어터",
//                tickets = listOf(
//                    Ticket("T240101001", "뮤지컬 라이온킹", "2024.12.25 (수)", "19:30",
//                           "샤롯데씨어터", "VIP석 A열 5번", "150,000원", "예매완료", "QR001"),
//                    Ticket("T240101002", "뮤지컬 라이온킹", "2024.12.25 (수)", "19:30",
//                           "샤롯데씨어터", "VIP석 A열 6번", "150,000원", "예매완료", "QR002")
//                )
//            ),
//            Performance(
//                id = "perf_002",
//                title = "콘서트 아이유",
//                date = "2024.12.31 (화)",
//                time = "20:00",
//                venue = "올림픽체조경기장",
//                tickets = listOf(
//                    Ticket("T240101003", "콘서트 아이유", "2024.12.31 (화)", "20:00",
//                           "올림픽체조경기장", "스탠딩 A구역", "132,000원", "예매완료", "QR003")
//                )
//            )
//        )
//    }
}