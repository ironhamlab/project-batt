package com.a506.batticket.presentation.ticket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a506.batticket.R
import com.a506.batticket.domain.model.TicketModel

class TicketAdapter(private val tickets: List<TicketModel>) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reservationNumberText: TextView = itemView.findViewById(R.id.reservationNumberText)
        val statusText: TextView = itemView.findViewById(R.id.statusText)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val dateTimeText: TextView = itemView.findViewById(R.id.dateTimeText)
        val venueText: TextView = itemView.findViewById(R.id.venueText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        
//        holder.reservationNumberText.text = "예매번호: ${ticket.reservationNumber}"
//        holder.statusText.text = ticket.status
//        holder.titleText.text = ticket.title
//        holder.dateTimeText.text = "${ticket.date} ${ticket.time}"
//        holder.venueText.text = ticket.venue
    }

    override fun getItemCount(): Int = tickets.size
}