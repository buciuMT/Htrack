package com.example.gym.data

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String)
data class PingRequest(val success:Boolean, val message:String);

interface ApiService {
    @POST("login") // Endpoint-ul serverului
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    fun ping(@Body request: PingRequest):Call<PingRequest>
}
