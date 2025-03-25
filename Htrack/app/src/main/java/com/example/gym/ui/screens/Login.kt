package com.example.gym.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym.R

import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gym.GymScreen
import com.example.gym.PricesScreen
import com.example.gym.ui.theme.GymTheme

@Composable
fun SignUpScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gymbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Înapoi")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Conectează-te", fontSize = 24.sp, color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = Color.White) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Parolă", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { /* TODO: Adaugă logica de înregistrare */ },
                modifier = Modifier.fillMaxWidth()) {
                Text("Conectare")
            }

            Spacer(modifier = Modifier.height(10.dp))

            ClickableText(
                text = AnnotatedString("Nu ai cont? Înregistrează-te!"),
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth(),
                style = LocalTextStyle.current.copy(color = Color.White)
            )
        }
    }
}

