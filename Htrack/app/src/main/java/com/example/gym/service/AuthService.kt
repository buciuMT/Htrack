package com.example.gym.service

import com.example.gym.data.LoginRequest
import com.example.gym.data.LoginResponse
import com.example.gym.database.Database
import android.util.Log
import com.example.gym.data.RegisterRequest
import com.example.gym.data.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AuthService {
    fun login(request: LoginRequest): LoginResponse {
        val sql = "SELECT * FROM user WHERE username = ? AND parola = ?"
        val connection = Database.connect()
        val statement = connection.prepareStatement(sql)
        statement.setString(1, request.username)
        statement.setString(2, request.password)

        println("Trying to login with: ${request.username}, ${request.password}") // DEBUG

        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            println("Login reușit pentru ${request.username}")
            LoginResponse(true, "Autentificare reușită!")
        } else {
            println("Eșec la login pentru ${request.username}")
            LoginResponse(false, "Username sau parola greșită!")
        }
    }
    suspend fun registerUser(registerRequest: RegisterRequest): RegisterResponse {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val json = JSONObject().apply {
                    put("email", registerRequest.email)
                    put("username", registerRequest.username)
                    put("password", registerRequest.password)
                }

                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = json.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/register")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    // Presupunem că ai un JSON simplu ca răspuns
                    val responseJson = JSONObject(responseBody)
                    val idUser = if (responseJson.has("id_user")) responseJson.getInt("id_user") else null

                    return@withContext RegisterResponse(
                        success = responseJson.getBoolean("success"),
                        message = responseJson.getString("message"),
                        id_user = idUser
                    )
                } else {
                    Log.e("Register", "Failed to register: ${response.code}")
                    return@withContext RegisterResponse(false, "Eroare: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("Register", "Exception: ${e.message}")
                return@withContext RegisterResponse(false, "Excepție: ${e.message}")
            }
        }
    }

}
