package com.example.gym.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.navigation.NavController
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerHomeScreen(navController: NavController, username: String)
{
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedItem by remember { mutableStateOf("Cont") }
    val items = listOf("Cont", "Useri", "Adaugă Poll")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Meniu",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                items.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item) },
                        selected = item == selectedItem,
                        onClick = {
                            selectedItem = item
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedItem) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {

                when (selectedItem) {
                    "Cont" -> TrainerContPage(username)
                    //"Useri" -> TrainerUserListPage()
                    //"Adaugă Poll" -> TrainerAddPollPage()
                }
            }
        }
    }
}

@Composable
fun TrainerContPage(username: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Date cont", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Username: $username")
        Text("Rol: Trainer")

    }
}