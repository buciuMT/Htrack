package com.example.gym.service

import com.example.gym.data.LoginRequest
import com.example.gym.data.LoginResponse
import com.example.gym.database.Database
import android.util.Log
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
    suspend fun registerUser(email: String, username: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val json = JSONObject()
                json.put("email", email)
                json.put("username", username)
                json.put("password", password)
                json.put("tip_user", "TRAINER") // AICI fixăm să fie mereu "TRAINER"

                val mediaType = "application/json".toMediaTypeOrNull()
                val body = json.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/register") // sau IP-ul serverului tău
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("Register", "User registered successfully")
                } else {
                    Log.e("Register", "Failed to register: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("Register", "Exception: ${e.message}")
            }
        }
    }
}
