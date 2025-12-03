package com.a506.batticket.presentation.ticket

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.a506.batticket.R
import com.a506.batticket.databinding.ItemPerformanceBinding
import com.a506.batticket.domain.model.Booking

//class PerformanceAdapter(
//    private val performances: List<Performance>,
//    private val onPerformanceClick: (Performance) -> Unit
//) : RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder>() {
class PerformanceAdapter(
    private val performances: List<Booking>,
    private val context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class PerformanceViewHolder(val binding: ItemPerformanceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_performance, parent, false)
        return PerformanceViewHolder(
            ItemPerformanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as PerformanceViewHolder).binding
        val performance = performances[position]
        
//        holder.performanceTitleText.text = performance.title
//        holder.ticketCountBadge.text = "${performance.ticketCount}매"
//        holder.performanceDateTimeText.text = "${performance.date} ${performance.time}"
//        holder.performanceVenueText.text = performance.venue
//        holder.totalPriceText.text = performance.totalPrice
//
//        holder.itemView.setOnClickListener {
//            onPerformanceClick(performance)
//        }

        binding.performanceTitleText.text = performance.title
//        binding.ticketCountBadge.text = "${itemCount}매"
        binding.performanceDateTimeText.text = performance.showDateTime
        binding.performanceVenueText.text = performance.venueName
        binding.root.setOnClickListener {
            // TODO: 티켓 상세 페이지로 이동
            val intent = Intent(context, TicketDetailActivity::class.java)
            intent.putExtra("bookingId", performance.bookingId)
            Log.d("bookingId", performance.bookingId.toString())
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = performances.size
}