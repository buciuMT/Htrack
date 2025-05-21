package com.example.gym.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.navigation.NavController
import com.example.gym.data.RetrofitClient
import com.example.gym.model.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gym.data.AbonamentAction
import com.example.gym.data.AbonamentRequest
import com.example.gym.data.DezactivareRequest
import com.example.gym.data.AbonamentResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerHomeScreen(navController: NavController, username: String, trainerId: Int) {
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
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when (selectedItem) {
                    "Cont" -> TrainerContPage(username, trainerId)
                    "Useri" -> TrainerUserListPage(trainerId = trainerId)
                }
            }
        }
    }
}

@Composable
fun TrainerContPage(username: String, id: Int) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Date cont", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Username: $username")
        Text("Rol: Trainer")
    }
}

@Composable
fun TrainerUserListPage(trainerId: Int) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showAbonamentDialog by remember { mutableStateOf(false) }
    var showDezactivareDialog by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var abonamentAction by remember { mutableStateOf<AbonamentAction?>(null) }

    LaunchedEffect(trainerId) {
        val call = RetrofitClient.apiService.getAssignedUsers(trainerId)
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                isLoading = false
                if (response.isSuccessful) {
                    users = response.body() ?: emptyList()
                } else {
                    errorMessage = "Eroare la încărcare: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                isLoading = false
                errorMessage = "Eroare de rețea: ${t.localizedMessage}"
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Userii tăi",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            errorMessage != null -> Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            users.isEmpty() -> Text("Nu ai useri atribuiți.", color = MaterialTheme.colorScheme.onBackground)
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(users) { user ->
                        var expanded by remember { mutableStateOf(false) }

                        Card(
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    Text(
                                        text = "👤 ${user.username}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Email: ${user.email ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Box {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = "Mai multe",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Detalii abonament") },
                                            onClick = {
                                                expanded = false
                                                val call = RetrofitClient.apiService.getAbonament(user.id_user)
                                                call.enqueue(object : Callback<AbonamentResponse> {
                                                    override fun onResponse(call: Call<AbonamentResponse>, response: Response<AbonamentResponse>) {
                                                        val abonament = response.body()

                                                        if (!abonament?.tip_abonament.isNullOrEmpty() && abonament.tip_abonament.uppercase() != "NEACTIV") {
                                                            Log.d("API", "Răspuns dezactivare: ${response.code()}")
                                                            abonamentAction = AbonamentAction(userId = user.id_user, showDezabonare = true)

                                                        } else {

                                                            abonamentAction = AbonamentAction(userId = user.id_user, showAbonare = true)
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<AbonamentResponse>, t: Throwable) {
                                                        Log.e("API", "Eroare la obținere abonament: ${t.localizedMessage}")
                                                    }
                                                })
                                            }

                                        )

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    abonamentAction?.let { action ->
        if (action.showAbonare) {
            AbonamentDialog(
                onDismiss = { abonamentAction = null },
                onConfirm = { tipAbonament ->
                    abonamentAction = null
                    val request = AbonamentRequest(action.userId, tipAbonament)
                    RetrofitClient.apiService.addAbonament(request)
                        .enqueue(object : Callback<Abonament> {
                            override fun onResponse(call: Call<Abonament>, response: Response<Abonament>) {
                                // Poți adăuga un snackbar sau refresh
                            }

                            override fun onFailure(call: Call<Abonament>, t: Throwable) {
                                // Log
                            }
                        })
                }
            )
        }

        if (action.showDezabonare) {
            ConfirmDezactivareDialog(
                onDismiss = { abonamentAction = null },
                onConfirm = {
                    abonamentAction = null
                    val request = DezactivareRequest(action.userId)
                    RetrofitClient.apiService.dezactiveazaAbonament(request)
                        .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Log.d("API", "Abonament dezactivat: ${response.code()}")
                            } else {
                                Log.e("API", "Eroare la dezactivare: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("API", "Eroare rețea: ${t.localizedMessage}")
                        }
                    })
                }
            )
        }
    }

}

@Composable
fun AbonamentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selected by remember { mutableStateOf("standard") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onConfirm(selected) }) {
                Text("Confirmă")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Anulează")
            }
        },
        title = { Text("Alege tipul de abonament") },
        text = {
            Column {
                RadioButtonWithLabel("Standard (12 ședințe)", "standard", selected) { selected = it }
                RadioButtonWithLabel("Gold (13 ședințe)", "gold", selected) { selected = it }
                RadioButtonWithLabel("Premium (15 ședințe)", "premium", selected) { selected = it }
            }
        }
    )
}

@Composable
fun RadioButtonWithLabel(label: String, value: String, selectedValue: String, onSelect: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        RadioButton(
            selected = selectedValue == value,
            onClick = { onSelect(value) }
        )
        Text(label, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun ConfirmDezactivareDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmare") },
        text = { Text("Sigur vrei să dezactivezi abonamentul?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Da")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Nu")
            }
        }
    )
}
