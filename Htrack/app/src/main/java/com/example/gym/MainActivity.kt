package com.example.gym

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gym.ui.screens.*
import com.example.gym.ui.theme.GymTheme

class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            GymTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { GymScreen(navController) }
                        composable("prices") { PricesScreen(navController) }
                        composable("login") { SignUpScreen(navController) }
                        composable("signup") { SignUpWithSubscriptionScreen(navController) }
                        composable("PaginaAdmin/{username}") { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            PaginaAdmin(username = username, navGlController = navController)
                        }

                        composable("PaginaUser/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                            UserHomeScreen(navController, userId)
                        }


                        composable(
                            "PaginaTrainer/{username}/{id}",
                            arguments = listOf(
                                navArgument("username") { type = NavType.StringType },
                                navArgument("id") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            val id = backStackEntry.arguments?.getInt("id") ?: -1
                            TrainerHomeScreen(navController, username, id)
                        }



                    }
                }
            }
        }
    }
}

@Composable
fun GymScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.gymbg),
            contentDescription = "Gym Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 1.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bara de sus cu logo și buton de prețuri
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .zIndex(1f)
                    .padding(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gymlogo),
                        contentDescription = "Gym Logo",
                        modifier = Modifier.size(60.dp)
                    )

                    Button(
                        onClick = { navController.navigate("prices") }, // Navigare către prețuri
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    ) {
                        Text("Prețuri", color = Color.Black)
                    }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(32.dp)
                ) {
                    Text(
                        text = "TRANSFORMĂ-ȚI CORPUL. ÎNTĂREȘTE-ȚI MINTEA. DEPĂȘEȘTE-ȚI LIMITELE!",
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Intră pe portal pentru a programa un antrenament",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("login") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2)), // Mov deschis
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Accesează portalul!", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                }
            }
        }
    }
}

@Composable
fun PricesScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gymbg), // Fundal de sală
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)) // Suprapunere întunecată pentru vizibilitate
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Buton de întoarcere
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("Înapoi", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("PREȚURI ABONAMENTE", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)

            Spacer(modifier = Modifier.height(16.dp))

            PriceCard(
                title = "STANDARD",
                price = "300 LEI",
                benefits = listOf(
                    "Antrenament în grupuri de maxim 4 persoane",
                    "12 antrenamente pe lună",
                    "Plan de antrenament personalizat"
                ),
                titleColor = Color.White,
                priceColor = Color.Yellow,
                cardColor = Color.DarkGray
            )

            PriceCard(
                title = "GOLD",
                price = "450 LEI",
                benefits = listOf(
                    "Beneficiile abonamentului Standard",
                    "Posibilitatea de anulare a programării fără costuri",
                    "Posibilitatea reprogramării unui antrenament anulat"
                ),
                titleColor = Color.Yellow,
                priceColor = Color.Cyan,
                cardColor = Color.Gray
            )

            PriceCard(
                title = "PREMIUM",
                price = "600 LEI",
                benefits = listOf(
                    "Antrenament individual",
                    "Acces la zona de calcul al caloriilor",
                    "15 antrenamente pe lună",
                    "Posibilitatea de anulare a programării fără costuri",
                    "Posibilitatea reprogramării unui antrenament anulat"
                ),
                titleColor = Color.Red,
                priceColor = Color.Red,
                cardColor = Color.Black,
                borderHighlight = true
            )
        }
    }
}

@Composable
fun PriceCard(
    title: String,
    price: String,
    benefits: List<String>,
    titleColor: Color,
    priceColor: Color,
    cardColor: Color,
    borderHighlight: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if (borderHighlight) Modifier.background(Color.Red.copy(alpha = 0.8f)) else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = titleColor)
            Text(text = price, fontSize = 20.sp, color = priceColor, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            benefits.forEach { benefit ->
                Text(text = "✔ $benefit", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}