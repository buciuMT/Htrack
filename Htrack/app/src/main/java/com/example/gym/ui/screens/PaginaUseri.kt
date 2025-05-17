package com.example.gym.ui.screens

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
@Composable
fun ContPage() {
    Column(Modifier.padding(16.dp)) {
        Text("Pagina de cont")
    }
}

@Composable
fun IstoricPage() {
    Column(Modifier.padding(16.dp)) {
        Text("Pagina de istoric")
    }
}

@Composable
fun ConversatiiPage() {
    Column(Modifier.padding(16.dp)) {
        Text("Pagina de conversații")
    }
}

@Composable
fun UserHomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedItem by remember { mutableStateOf("Cont") }
    val items = listOf("Cont", "Istoric", "Conversații")

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
        @OptIn(ExperimentalMaterial3Api::class)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedItem) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (selectedItem) {
                    "Cont" -> ContPage()
                    "Istoric" -> IstoricPage()
                    "Conversații" -> ConversatiiPage()
                }
            }
        }
    }
}
