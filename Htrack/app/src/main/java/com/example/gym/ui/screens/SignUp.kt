package com.example.gym.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.gym.service.AuthService
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym.R

@Composable
fun SignUpWithSubscriptionScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedSubscription by remember { mutableStateOf("Standard") }
    val subscriptionOptions = listOf("Standard", "Gold", "Premium")
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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

            Text("Înregistrează-te", fontSize = 24.sp, color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

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

            Spacer(modifier = Modifier.height(10.dp))

            // Dropdown pentru abonament
           /* Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopStart)) {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedSubscription, color = Color.White)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    subscriptionOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedSubscription = option
                                expanded = false
                            }
                        )
                    }
                }
            }*/

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val authService = AuthService()
                    coroutineScope.launch {
                        authService.registerUser(email, username, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Înregistrează-te")
            }



            Spacer(modifier = Modifier.height(10.dp))

            ClickableText(
                text = AnnotatedString("Ai deja cont? Loghează-te"),
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(),
                style = LocalTextStyle.current.copy(color = Color.White)
            )
        }
    }
}
