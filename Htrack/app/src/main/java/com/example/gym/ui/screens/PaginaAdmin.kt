package com.example.gym.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.gym.viewmodel.AdminViewModel
import com.example.gym.model.User
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String) {
    object ContAdmin : Screen("admin_cont", "Cont")
    object Users : Screen("users", "Useri")
    object Trainers : Screen("trainers", "Traineri")
    object AddTrainer : Screen("add_trainer", "Adaugă Trainer")
    object AssignTrainer : Screen("assign_trainer", "Atribuie Traineri")

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
                title = { Text("Salut, $username!", style = MaterialTheme.typography.h6) },
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
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Meniu", style = MaterialTheme.typography.h6)
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerButton("Cont") { navController.navigate(Screen.ContAdmin.route) }
                DrawerButton("Vizualizează Useri") { navController.navigate(Screen.Users.route) }
                DrawerButton("Vizualizează Traineri") { navController.navigate(Screen.Trainers.route) }
                DrawerButton("Atribuie Traineri") { navController.navigate(Screen.AssignTrainer.route) }


            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ContAdmin.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.ContAdmin.route) {
                AdminAccountPage(username)
            }
            composable(Screen.Users.route) {
                LaunchedEffect(Unit) {
                    viewModel.fetchUsers()
                }
                UserList(viewModel.users) { userId ->
                    viewModel.transformaUserInTrainer(userId)
                }

            }

            composable(Screen.Trainers.route) {
                LaunchedEffect(Unit) {
                    viewModel.fetchTrainers()
                }

                val trainers = viewModel.trainers
                TrainerList(trainers = trainers)
            }

            composable(Screen.AssignTrainer.route) {
                LaunchedEffect(Unit) {
                    viewModel.fetchUsersFaraAntrenor()
                    viewModel.fetchTrainers()
                }

                AssignTrainerScreen(
                    users = viewModel.users.filter { it.tip_user == "USER" && it.antrenor_id == null },
                    trainers = viewModel.trainers,
                    onAssignTrainer = { userId, trainerId ->
                        viewModel.assignTrainer(userId, trainerId)
                    }
                )
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
            .padding(vertical = 12.dp),
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun UserList(users: List<User>, onTransformClick: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(users) { user ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("👤 ${user.username}", style = MaterialTheme.typography.subtitle1)
                        Text("Email: ${user.email}", style = MaterialTheme.typography.body2)
                    }

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }

                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(onClick = {
                                expanded = false
                                onTransformClick(user.id_user) //
                            }) {
                                Text("Transformă în trainer")
                            }

                            DropdownMenuItem(onClick = {
                                expanded = false

                            }) {
                                Text("Dezactivează")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TrainerList(trainers: List<User>, onMenuActionClick: (Int, String) -> Unit = { _, _ -> }) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(trainers) { trainer ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("🏋️ ${trainer.username}", style = MaterialTheme.typography.subtitle1)
                        Text("Email: ${trainer.email}", style = MaterialTheme.typography.body2)
                        Text("Rol: ${trainer.tip_user}", style = MaterialTheme.typography.caption)

                    }

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }

                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(onClick = {
                                expanded = false

                                onMenuActionClick(trainer.id_user, "deactivate")
                            }) {
                                Text("Dezactivează")
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddTrainerForm(onAdd: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)) {
        Text("Adaugă un nou trainer", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nume Trainer") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (name.isNotBlank()) {
                    onAdd(name)
                    name = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Adaugă")
        }
    }
}

@Composable
fun AdminAccountPage(username: String) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {
        Text("Contul meu", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        Text("👤 Nume utilizator: $username", style = MaterialTheme.typography.body1)
        Text("🛡️ Rol: Administrator", style = MaterialTheme.typography.body2)
    }
}
@Composable
fun AssignTrainerScreen(
    users: List<User>,
    trainers: List<User>,
    onAssignTrainer: (Int, Int) -> Unit
) {
    var showDialogForUserId by remember { mutableStateOf<Int?>(null) }

    if (users.isEmpty()) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nu există utilizatori fără antrenor", style = MaterialTheme.typography.body1)

        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(users) { user ->
            var expanded by remember { mutableStateOf(false) }

            Card(elevation = 6.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("👤 ${user.username}", style = MaterialTheme.typography.subtitle1)
                        Text("Email: ${user.email}", style = MaterialTheme.typography.body2)
                    }

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }

                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(onClick = {
                                expanded = false
                                showDialogForUserId = user.id_user
                            }) {
                                Text("Atribuie Trainer")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialogForUserId != null) {
        AlertDialog(
            onDismissRequest = { showDialogForUserId = null },
            title = { Text("Alege un trainer") },
            text = {
                Column {
                    trainers.forEach { trainer ->
                        Text(
                            text = "🏋️ ${trainer.username}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAssignTrainer(showDialogForUserId!!, trainer.id_user)
                                    showDialogForUserId = null
                                }
                                .padding(8.dp)
                        )
                    }
                }
            },
            buttons = {}
        )
    }
}

