package com.a506.batticket.data.api

import com.a506.batticket.data.model.TicketListResponseDto
import com.a506.batticket.data.model.TicketResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketApiService {
    @GET("/api/v1/bookings")
    fun getTicketList(
        @Query("memberId") memberId: Long,
    ): Call<TicketListResponseDto>

    @GET("/api/v1/bookings")
    fun getTicket(
        @Query("memberId") memberId: Long,
        @Query("bookingId") bookingId: Long
    ): Call<TicketResponseDto>

}

