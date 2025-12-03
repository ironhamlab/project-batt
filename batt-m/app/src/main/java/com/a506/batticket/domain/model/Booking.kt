package com.a506.batticket.domain.model

data class Booking(
    val bookingNumber: String,
    val bookingId: Long,
    val seatCount: Int,
    val showDateTime: String,
    val status: String,
    val title: String,
    val venueName: String
) {
//    fun toModel(): Performance = Performance(
//        id = id,

//    )
}