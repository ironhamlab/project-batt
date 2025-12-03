package com.a506.batticket.presentation.ticket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a506.batticket.R
import com.a506.batticket.data.datastore.AuthRepository
import com.a506.batticket.databinding.ActivityTicketListBinding
import com.a506.batticket.presentation.ticket.TicketViewModel
import com.a506.batticket.domain.model.Booking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TicketListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketListBinding
    private lateinit var ticketViewModel: TicketViewModel
    private lateinit var ticketRecyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var ticketCountText: TextView
    private lateinit var authRepository : AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository(applicationContext)

        initViews()
        observeLiveData()
        loadTickets()
    }
    
    private fun initViews() {
        ticketViewModel = ViewModelProvider(this)[TicketViewModel::class.java]

        ticketRecyclerView = findViewById(R.id.ticketRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        ticketCountText = findViewById(R.id.ticketCountText)
    }
    private fun observeLiveData() {
        ticketViewModel.ticketList.observe(this) {
            updateUI(it)
        }
    }

    private fun loadTickets() {
        lifecycleScope.launch {
            var memberId : Long? = authRepository.userIdFlow.first()
            Log.d("member id", memberId.toString())
            ticketViewModel.getTicketList(memberId ?: 1)
        }
    }
    
    private fun setupRecyclerView(recyclerView: RecyclerView, ticketList: List<Booking>) {
        recyclerView.adapter = PerformanceAdapter(ticketList, this)
        ticketRecyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun updateUI(ticketList: List<Booking>) {
        val totalTickets = ticketList.sumOf { it.seatCount }
        
        if (ticketList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            ticketRecyclerView.visibility = View.GONE
            ticketCountText.text = "티켓 없음"
        } else {
            emptyView.visibility = View.GONE
            ticketRecyclerView.visibility = View.VISIBLE
            setupRecyclerView(binding.ticketRecyclerView, ticketList)
            ticketCountText.text = "총 ${totalTickets}매"
        }
    }
}