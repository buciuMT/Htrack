package com.example.gym.data
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.Call
import com.example.gym.model.*
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Path

import retrofit2.http.Query

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String,val tip_user: String?=null,val id_user: Int?=0)
data class RegisterRequest(val email: String, val username: String, val password: String)
data class RegisterResponse(val success: Boolean, val message: String, val id_user: Int?=null)
data class PingRequest(val success:Boolean, val message:String)
data class AbonamentResponse(
    @SerializedName("TIP_ABONAMENT")
    val tip_abonament: String,

    @SerializedName("NUMAR_SEDINTE")
    val numar_sedinte: Int
)
data class AbonamentRequest(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("tip_abonament") val tipAbonament: String
)
data class DezactivareRequest(
    @SerializedName("id_user")
    val idUser: Int
)

data class AbonamentAction(
    val userId: Int,
    val showAbonare: Boolean = false,
    val showDezabonare: Boolean = false
)
data class NotificareRequest(
    val tip: String,
    val id_user: Int,
    val mesaj: String,
    val data: String
)
data class NotificareResponse(
    @SerializedName("ID_NOTIFICARE") val idNotificare: Int,
    @SerializedName("ID_USER") val idUser: Int,
    val mesaj: String,
    val data: String,
    val citit: Boolean,
    val tip: String
)


interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun registerUser(@Body request: RegisterRequest):Call<RegisterResponse>

    @POST("ping")
    fun ping(@Body request: PingRequest): Call<PingRequest>

    @GET("users")
    suspend fun getUsersByType(@Query("tip") tipUser: String): List<User>
    @GET("/trainers")
    suspend fun getTrainers(): List<User>

    @POST("/users/{id}/transform-trainer")
    suspend fun transformUserToTrainer(@Path("id") id: Int): Response<Unit>

    @POST("users/{userId}/assign-trainer/{trainerId}")
    suspend fun assignTrainerToUser(
        @Path("userId") userId: Int,
        @Path("trainerId") trainerId: Int
    ): Response<Unit>

    @GET("users/fara-antrenor")
    suspend fun getUsersFaraAntrenor(): List<User>

    @GET("users/by-trainer/{trainerId}")
    fun getAssignedUsers(@Path("trainerId") trainerId: Int): Call<List<User>>

    @GET("abonament/{id_user}")
    suspend fun getAbonamentActiv(@Path("id_user") id: Int): Abonament

    @POST("abonament")
    fun addAbonament(@Body abonamentRequest: AbonamentRequest): Call<Abonament>

    @GET("abonament/{id_user}")
    fun getAbonament(@Path("id_user") id: Int): Call<AbonamentResponse>

    @POST("abonament/dezactivare")
    fun dezactiveazaAbonament(@Body request: DezactivareRequest): Call<ResponseBody>

    @GET("istoricAbonamente/{id_user}")
    suspend fun getIstoricAbonamente(@Path("id_user") userId: Int): List<Abonament>
    @POST("notificari")
    fun addNotificare(@Body notificare: NotificareRequest): Call<ResponseBody>

    @GET("notificari/{id_user}")
    suspend fun getNotificariUser(@Path("id_user") userId: Int): List<NotificareResponse>

    @POST("notificari/citit/{id_user}")
    suspend fun marcheazaNotificariCitite(@Path("id_user") userId: Int): Response<ResponseBody>
}
