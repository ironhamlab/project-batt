package com.a506.batticket.domain.model

data class TicketModel(
    val bookingNumber: String,
    val grade: String,
//    val title: String,
//    val date: String,
//    val time: String,
//    val venue: String,
    val seat: String,
    val price: String,
//    val status: String = "예매완료",
//    val qrCode: String = "" // QR 코드 데이터
)