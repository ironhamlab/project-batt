package com.a506.batticket.domain.model

import com.a506.batticket.domain.model.TicketModel

data class Performance(
    val id: Long,
    val orderId: String,
    val title: String,
    val showDateTime: String,
    val venue: String,
    val seatCount: Int,
    val status: String,
    val tickets: List<TicketModel>
) {
    val ticketCount: Int get() = tickets.size
    val totalPrice: String get() {
        val total = tickets.sumOf {
            it.price.replace(",", "").replace("원", "").toIntOrNull() ?: 0
        }
        return "${String.format("%,d", total)}원"
    }
}