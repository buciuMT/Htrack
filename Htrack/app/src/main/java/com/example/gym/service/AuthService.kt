package com.example.gym.service

import com.example.gym.data.LoginRequest
import com.example.gym.data.LoginResponse
import com.example.gym.database.Database

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

}
