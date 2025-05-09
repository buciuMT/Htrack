package com.example.gym.data
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.Call
import com.example.gym.model.User
import com.example.gym.model.Trainer
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String)
data class PingRequest(val success:Boolean, val message:String);

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("ping")
    fun ping(@Body request: PingRequest): Call<PingRequest>

    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("trainers")
    suspend fun getTrainers(): List<Trainer>

    @POST("trainers")
    suspend fun addTrainer(@Body trainer: Trainer)
}