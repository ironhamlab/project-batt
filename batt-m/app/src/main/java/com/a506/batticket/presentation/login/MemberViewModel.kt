package com.a506.batticket.presentation.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a506.batticket.BATTApplication
import com.a506.batticket.data.api.LoginApiService
import com.a506.batticket.data.api.LoginRequestDto
import com.a506.batticket.data.api.RetrofitService
import com.a506.batticket.data.datastore.AuthRepository
import com.a506.batticket.data.model.JwtResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MemberViewModel(application : Application) : AndroidViewModel(application) {
    private val retrofitService = RetrofitService(application).getRetrofit().create(LoginApiService::class.java)

//    private val _jwtToken = MutableLiveData<String>()
//    val jwtToken : LiveData<String> get() = _jwtToken

    private val authRepository = AuthRepository(application)
    val jwtToken = authRepository.jwtTokenFlow.asLiveData()
    val memberId = authRepository.userIdFlow.asLiveData()

    fun getJwtToken(provider: String, accessToken: String) {
        retrofitService.getJwtToken(LoginRequestDto(provider, accessToken)).enqueue(object  : Callback<JwtResponseDto> {
            override fun onResponse(
                p0: Call<JwtResponseDto?>,
                p1: Response<JwtResponseDto?>
            ) {
                Log.d("token", "${p0.request()}")
                if (p1.isSuccessful) {
                    Log.d("token", "${p1.body()}")
                    val token = p1.body()?.token
                    val memberId = p1.body()?.memberId

                    if (token != null && memberId != null) {
                        viewModelScope.launch {
                            authRepository.saveUserInfo(token, memberId)
                        }
                    }
                } else {
                    Log.e("token", "${p1.errorBody()?.string()}")
                }
            }

            override fun onFailure(
                p0: Call<JwtResponseDto?>,
                p1: Throwable
            ) {
                Log.i("test", "${p0.request()}")
                Log.e("test", p1.message.toString())
            }
        })
    }

}