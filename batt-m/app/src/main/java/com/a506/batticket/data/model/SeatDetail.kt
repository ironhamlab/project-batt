package com.a506.batticket.data.model

import com.a506.batticket.domain.model.TicketModel

data class SeatDetail(
    val grade: String,
    val seatNumber: String,
    val bookingNumber: String,
    val price: Int
) {
    fun toModel() : TicketModel = TicketModel(
        bookingNumber = bookingNumber,
        seat = seatNumber,
        price = "${price}원",
        grade = grade
    )
}