package com.example.gym.controller

import com.example.gym.data.LoginRequest
import com.example.gym.data.RegisterRequest
import com.example.gym.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    post("/login") {
        val loginRequest = call.receive<LoginRequest>()
        val response = authService.login(loginRequest)
        call.respond(response)
    }
    post("/register"){
        val registerRequest = call.receive<RegisterRequest>()
        val response=authService.registerUser(registerRequest)
        call.respond(response);
    }
}
