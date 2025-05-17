package com.example.gym.data
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.Call
import com.example.gym.model.*

import retrofit2.http.Query

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String,val tip_user: String?=null)
data class RegisterRequest(val email: String, val username: String, val password: String)
data class RegisterResponse(val success: Boolean, val message: String)
data class PingRequest(val success:Boolean, val message:String)

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun registerUser(@Body request: RegisterRequest):Call<RegisterResponse>

    @POST("ping")
    fun ping(@Body request: PingRequest): Call<PingRequest>

    @GET("users")
    suspend fun getUsersByType(@Query("tip") tipUser: String): List<User>
    @GET("trainers")
    suspend fun getTrainers(): List<Trainer>

    @POST("trainers")
    suspend fun addTrainer(@Body trainer: Trainer)
}