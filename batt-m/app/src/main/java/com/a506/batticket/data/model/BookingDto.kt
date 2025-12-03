package com.a506.batticket.data.model

import com.a506.batticket.domain.model.Booking

data class BookingDto(
    val bookingId: Long,
    val orderId: String,
    val seatCount: Int,
    val performanceDate: String,
    val performanceTime: String,
    val status: String,
    val title: String,
    val venueName: String
) {
    fun toModel() : Booking {
        return Booking(
            bookingNumber = orderId,
            bookingId,
            seatCount,
            showDateTime= "$performanceDate $performanceTime",
            status,
            title,
            venueName
        )
    }
}
