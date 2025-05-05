package com.example.gym.ui.screens
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gym.viewmodel.AdminViewModel
import com.example.gym.model.User
import com.example.gym.model.Trainer
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.Text
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Button
import androidx.compose.foundation.lazy.items


sealed class Screen(val route: String, val title: String) {
    object Users : Screen("users", "Useri")
    object Trainers : Screen("trainers", "Traineri")
    object AddTrainer : Screen("add_trainer", "Adaugă Trainer")
}

@Composable
fun PaginaAdmin(username: String, viewModel: AdminViewModel = viewModel()) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Salut, $username!") },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        drawerContent = {
            Text("Meniu", modifier = Modifier.padding(16.dp))
            Divider()
            DrawerButton("Vizualizează Useri") {
                navController.navigate(Screen.Users.route)
            }
            DrawerButton("Vizualizează Traineri") {
                navController.navigate(Screen.Trainers.route)
            }
            DrawerButton("Adaugă Traineri") {
                navController.navigate(Screen.AddTrainer.route)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Users.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Users.route) {
                viewModel.fetchUsers()
                UserList(viewModel.users)
            }
            composable(Screen.Trainers.route) {
                viewModel.fetchTrainers()
                TrainerList(viewModel.trainers)
            }
            composable(Screen.AddTrainer.route) {
                AddTrainerForm { name ->
                    viewModel.addTrainer(name)
                }
            }
        }
    }
}

@Composable
fun DrawerButton(text: String, onClick: () -> Unit) {
    Text(
        text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}

@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            Text("👤 ${user.name}", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun TrainerList(trainers: List<Trainer>) {
    LazyColumn {
        items(trainers) { trainer ->
            Text("🏋️ ${trainer.name}", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun AddTrainerForm(onAdd: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nume Trainer") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (name.isNotBlank()) {
                onAdd(name)
                name = ""
            }
        }) {
            Text("Adaugă")
        }
    }
}
