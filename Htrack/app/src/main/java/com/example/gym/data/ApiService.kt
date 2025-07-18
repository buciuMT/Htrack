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
data class VoteResponse(
    val id_user: Int,
    val ora: Int,
    val data_vot: String,
    val username: String? = null
)
data class PollResponse(
    val idPoll: Int,
    val idTrainer: Int,
    val isActive: Boolean
)

data class StartChatRequest(
    @SerializedName("id_user")
    val id_user: Int,

    @SerializedName("id_trainer")
    val id_trainer: Int
)
data class AddAlimentRequest(
    val id_user: Int,
    val id_aliment: Int,
    val tip_masa: String,
    val cantitate: Int,
    val data: String
)

data class RemoveAlimentRequest(
    val id_jurnal_alimentar: Int
)

data class CaloriesResponse(
    @SerializedName("total_calorii")
    val total_calorii: Double
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

    @POST("poll")
    suspend fun createPoll(@Body body: Map<String, Int>): Response<Poll>

    @POST("poll/dezactivare/{id_poll}")
    suspend fun deactivatePoll(@Path("id_poll") pollId: Int): Response<Unit>

    @GET("poll/trainer/{id_trainer}")
    suspend fun getActivePoll(@Path("id_trainer") trainerId: Int): Response<Poll>

    @POST("vote")
    suspend fun vote(@Body vote: Map<String, Int>): Response<Vote>

    @GET("poll/user/{userId}")
    suspend fun getPollActivByUserId(@Path("userId") userId: Int): Response<Poll>

    @GET("votes/{poll_id}/{user_id}")
    suspend fun getVoteByUserAndPoll(
        @Path("poll_id") pollId: Int,
        @Path("user_id") userId: Int
    ): Response<Vote>

    @GET("poll/{id_poll}/votes")
    suspend fun getVotesForPoll(@Path("id_poll") pollId: Int): Response<List<VoteResponse>>

    @GET("poll/trainer/{id_trainer}")
    suspend fun getPollForTrainer(@Path("id_trainer") trainerId: Int): Response<PollResponse>

    @GET("polls/votate/{id_user}")
    suspend fun getPollsVotate(@Path("id_user") userId: Int): List<PollVotat>

    @POST("vote/update")
    suspend fun updateVoteHour(@Body body: Map<String, Int>): Response<Unit>

    @GET("conversations/user/{id_user}")
    suspend fun getUserConversations(@Path("id_user") userId: Int): List<Conversatie>

    @GET("users/{id_user}/trainer")
    suspend fun getAntrenorId(@Path("id_user") userId: Int): TrainerResponse

    data class TrainerResponse(
        @SerializedName("antrenor_id")
        val id_trainer: Int
    )

    @POST("chat/start")
    suspend fun startChat(@Body req: StartChatRequest): StartChatResponse

    @GET("messages/{id_conversation}")
    suspend fun getMessages(@Path("id_conversation") convId: Int): List<MessageDto>

    @POST("messages")
    suspend fun sendMessage(@Body req: SendMessageRequest)

    @GET("trainer/users/{trainerId}")
    suspend fun getUsersForTrainer(
        @Path("trainerId") trainerId: Int
    ): List<User>

    @GET("/alimente/search")
    suspend fun searchAlimente(@Query("query") query: String): List<Aliment>

    @POST("/jurnal-alimentar/add")
    suspend fun addAlimentToJournal(@Body request: AddAlimentRequest): Response<Unit>

    @POST("/jurnal-alimentar/remove")
    suspend fun removeAlimentFromJournal(@Body request: RemoveAlimentRequest): Response<Unit>

    @GET("/jurnal-alimentar/{id_user}/{date}/{tip_masa}") // Corectez numele parametrilor Path
    suspend fun getJournalEntriesByDateAndMeal(
        @Path("id_user") userId: Int,
        @Path("date") date: String,
        @Path("tip_masa") tipMasa: String
    ): List<JurnalAlimentarEntry>

    @GET("/jurnal-alimentar/calorii/{id_user}/{date}") // Corectez numele parametrilor Path
    suspend fun getDailyCalories(
        @Path("id_user") userId: Int,
        @Path("date") date: String
    ): CaloriesResponse
}
