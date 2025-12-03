package com.a506.batticket.data.model

import com.a506.batticket.domain.model.Performance

data class TicketResponseDto(
    val bookingCreatedAt: String,
    val cancellationDeadline: Any,
    val memberName: String,
    val orderId: String,
    val paymentMethod: String,
    val performanceDate: String,
    val performanceTime: String,
    val posterImageUrl: String,
    val seatCount: Int,
    val seatDetails: List<SeatDetail>,
    val status: String,
    val title: String,
    val totalAmount: Int,
    val venueName: String
) {
    fun toModel(id: Long): Performance = Performance(
        id = id,
        orderId = orderId,
        seatCount = seatCount,
        showDateTime = performanceDate,
        status = status,
        title = title,
        venue = venueName,
        tickets = seatDetails.map { it.toModel() }
    )
}