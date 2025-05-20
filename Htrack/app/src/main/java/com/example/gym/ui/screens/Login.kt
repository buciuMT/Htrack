package com.example.gym.ui.screens
import androidx.compose.material3.*
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
import com.example.gym.data.RetrofitClient
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.gym.data.LoginRequest
import com.example.gym.data.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUpScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginResult by remember { mutableStateOf<String?>(null) }

    fun handleLogin() {
        val call = RetrofitClient.apiService.login(LoginRequest(username, password))
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                   // loginResult = loginResponse?.message

                    if (loginResponse?.success == true) {
                        val id = loginResponse.id_user
                        when (loginResponse.tip_user) {
                            "USER" -> navController.navigate("PaginaUser")
                            "ADMIN" -> navController.navigate("PaginaAdmin/$username")
                            "TRAINER" -> navController.navigate("PaginaTrainer/$username/$id")
                            else -> loginResult = "Tip utilizator necunoscut!"
                        }
                    }
                } else {
                    loginResult = "Eroare de autentificare!"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult = "Eroare de rețea! ${t.localizedMessage}"
                //t.printStackTrace()
            }
        })
    }


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
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Parolă", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { handleLogin() },
                modifier = Modifier.fillMaxWidth()) {
                Text("Conectare")
            }


            loginResult?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(it, color = Color.Red)
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
