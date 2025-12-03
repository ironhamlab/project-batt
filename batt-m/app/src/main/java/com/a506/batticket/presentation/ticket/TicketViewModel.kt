package com.a506.batticket.presentation.ticket

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a506.batticket.data.api.RetrofitService
import com.a506.batticket.data.api.TicketApiService
import com.a506.batticket.data.model.TicketListResponseDto
import com.a506.batticket.data.model.TicketResponseDto
import com.a506.batticket.domain.model.Booking
import com.a506.batticket.domain.model.Performance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TicketViewModel(application : Application) : AndroidViewModel(application) {
    private val retrofitService = RetrofitService(application).getRetrofit().create(TicketApiService::class.java)

    private val _ticketList = MutableLiveData<List<Booking>>()
    val ticketList: LiveData<List<Booking>> get() = _ticketList

    private val _ticketDetail = MutableLiveData<Performance>()
    val ticketDetail: LiveData<Performance> get() = _ticketDetail

    fun getTicketList(memberId : Long) {
        retrofitService.getTicketList(memberId).enqueue(object : Callback<TicketListResponseDto> {
            override fun onResponse(
                p0: Call<TicketListResponseDto?>,
                p1: Response<TicketListResponseDto?>
            ) {
                Log.d("test", "${p0.request()}")
                if (p1.isSuccessful) {
                    Log.d("test", "${p1.body()}")
                    _ticketList.value = p1.body()?.bookings?.map { it.toModel() }
                } else {
                    Log.e("test", "${p1.errorBody()?.string()}")
                }
            }

            override fun onFailure(p0: Call<TicketListResponseDto?>, p1: Throwable) {
                Log.i("test", "${p0.request()}")
                Log.e("test", p1.message.toString())
            }
        })
    }

    fun getTicketDetail(memberId: Long, bookingId: Long) {
        retrofitService.getTicket(memberId, bookingId).enqueue(object :
            Callback<TicketResponseDto> {
            override fun onResponse(
                p0: Call<TicketResponseDto?>,
                p1: Response<TicketResponseDto?>
            ) {
                Log.d("ticket", "${p0.request()}")
                if (p1.isSuccessful) {
                    Log.d("ticket", "${p1.body()}")
                    _ticketDetail.value = p1.body()?.toModel(bookingId)
                } else {
                    Log.e("ticket", "${p1.errorBody()?.string()}")
                }
            }

            override fun onFailure(p0: Call<TicketResponseDto?>, p1: Throwable) {
                Log.i("ticket", "${p0.request()}")
                Log.e("ticket", p1.message.toString())
            }
        })
    }
}