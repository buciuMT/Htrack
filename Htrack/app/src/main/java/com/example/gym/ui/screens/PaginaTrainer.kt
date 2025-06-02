package com.example.gym.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gym.data.AbonamentAction
import com.example.gym.data.AbonamentRequest
import com.example.gym.data.DezactivareRequest
import com.example.gym.data.AbonamentResponse
import com.example.gym.data.NotificareRequest
import com.example.gym.data.PollResponse
import com.example.gym.data.VoteResponse
import com.example.gym.repository.PollRepository
import com.example.gym.viewmodel.TrainerChatViewModel
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
    val items = listOf("Cont", "Useri", "Adaugă Poll","Conversații", "Deconectează-te")

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
                    val isLogout = item == "Deconectează-te"
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item,
                                color = if (isLogout) Color.Red else LocalContentColor.current
                            )
                        },
                        selected = item == selectedItem && !isLogout,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (isLogout) {
                                navController.navigate("home") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                selectedItem = item
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ){
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
                    "Adaugă Poll" -> TrainerCreatePollPage(trainerId)
                    "Conversații" -> TrainerConversationsPage(trainerId)
                }
            }
        }
    }
}
@Composable
fun TrainerConversationsPage(trainerId: Int, viewModel: TrainerChatViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var mesaj by remember { mutableStateOf("") }

    LaunchedEffect(trainerId) {
        viewModel.loadUsersForTrainer(trainerId)
    }

    if (selectedUserId == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Selectează un utilizator pentru a începe conversația", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.userList) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedUserId = user.id_user
                                scope.launch {
                                    viewModel.startChat(user.id_user, trainerId)
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = user.username ?: "User: ${user.username}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Button(onClick = { selectedUserId = null }) {
                Text("Înapoi la lista de utilizatori")
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).padding(bottom = 8.dp),
                reverseLayout = true
            ) {
                items(viewModel.messages.reversed()) { msg ->
                    val isTrainer = msg.id_sender == trainerId
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        horizontalArrangement = if (isTrainer) Arrangement.End else Arrangement.Start
                    ) {
                        Text(
                            text = msg.mesaj,
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    if (isTrainer) Color(0xFF4CAF50) else Color(0xFF2196F3),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = mesaj,
                    onValueChange = { mesaj = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Trimite mesaj...") }
                )
                Button(onClick = {
                    scope.launch {
                        if (mesaj.isNotBlank()) {
                            viewModel.sendMessage(trainerId, mesaj)
                            mesaj = ""
                        }
                    }
                }) {
                    Text("Trimite")
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
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onResponse(call: Call<Abonament>, response: Response<Abonament>) {
                                if (response.isSuccessful) {
                                    val notificare = NotificareRequestFactory.creeazaPentruAbonare(
                                        userId = action.userId,
                                        tipAbonament = request.tipAbonament
                                    )


                                    RetrofitClient.apiService.addNotificare(notificare)
                                        .enqueue(object : Callback<ResponseBody> {
                                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                                Log.d("API", "Notificare trimisă.")
                                            }

                                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                                Log.e("API", "Eroare notificare: ${t.localizedMessage}")
                                            }
                                        })
                                }
                            }

                            override fun onFailure(call: Call<Abonament>, t: Throwable) {
                                Log.e("API", "Eroare la abonare: ${t.localizedMessage}")
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
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.isSuccessful) {
                                    val notificare = NotificareRequestFactory.creeazaPentruDezabonare(action.userId)

                                    RetrofitClient.apiService.addNotificare(notificare)
                                        .enqueue(object : Callback<ResponseBody> {
                                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                                Log.d("API", "Notificare trimisă după dezabonare.")
                                            }

                                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                                Log.e("API", "Eroare notificare: ${t.localizedMessage}")
                                            }
                                        })
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e("API", "Eroare la dezabonare: ${t.localizedMessage}")
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
@Composable
fun TrainerCreatePollPage(trainerId: Int) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { PollRepository(RetrofitClient.apiService) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedVote by remember { mutableStateOf<VoteResponse?>(null) }
    var newHour by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var activePoll by remember { mutableStateOf<Poll?>(null) }
    var votes by remember { mutableStateOf<List<VoteResponse>>(emptyList()) }

    fun loadPollAndVotes() {
        scope.launch {
            try {
                activePoll = repo.getActivePollForTrainer(trainerId)
                activePoll?.let { poll ->
                    votes = repo.getVotesForPoll(poll.id)
                }
            } catch (e: Exception) {
                errorMessage = "Eroare la încărcarea pollului: ${e.message}"
                activePoll = null
                votes = emptyList()
            }
        }
    }

    LaunchedEffect(true) {
        loadPollAndVotes()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (activePoll == null) {
            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        try {
                            repo.createPoll(trainerId)
                            successMessage = "Poll creat cu succes!"
                            errorMessage = null
                            loadPollAndVotes()
                        } catch (e: Exception) {
                            errorMessage = "Eroare la creare: ${e.message}"
                            successMessage = null
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                Text("Creează Poll")
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        successMessage?.let { Text(it, color = Color.Green) }
        errorMessage?.let { Text(it, color = Color.Red) }

        activePoll?.let { poll ->
            Spacer(Modifier.height(16.dp))
            Text("Poll activ: #${poll.id}", style = MaterialTheme.typography.titleMedium)

            if (votes.isEmpty()) {
                Text("Niciun vot înregistrat.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Voturi:", fontWeight = FontWeight.Bold)

                    votes.forEach { vote ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Username: ${vote.username}", style = MaterialTheme.typography.bodyLarge)
                                    Text("Ora selectată: ${vote.ora}", style = MaterialTheme.typography.bodyMedium)
                                }

                                IconButton(onClick = {
                                    selectedVote = vote
                                    newHour = vote.ora.toString()
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Modifică ora")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                scope.launch {
                    try {
                        repo.deactivatePoll(poll.id)
                        successMessage = "Poll dezactivat cu succes!"
                        errorMessage = null
                        activePoll = null
                        votes = emptyList()
                    } catch (e: Exception) {
                        errorMessage = "Eroare la dezactivare: ${e.message}"
                    }
                }
            }) {
                Text("Dezactivează Poll")
            }
        }

        if (showDialog && selectedVote != null) {
            val hours = (8..20).toList()
            var expanded by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Modifică ora pentru ${selectedVote!!.username}") },
                text = {
                    Column {
                        Text("Selectează noua oră:")

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = if (newHour.isEmpty()) "Selectează ora" else "$newHour:00",
                                onValueChange = {},
                                label = { Text("Ora") },
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = true }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                hours.forEach { hour ->
                                    DropdownMenuItem(
                                        onClick = {
                                            newHour = hour.toString()
                                            expanded = false
                                        },
                                        text = { Text("$hour:00") }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val hour = newHour.toIntOrNull()
                        if (hour != null && activePoll != null) {
                            scope.launch {
                                val success = repo.updateVoteHour(
                                    pollId = activePoll!!.id,
                                    userId = selectedVote!!.id_user,
                                    hour = hour
                                )
                                if (success) {
                                    successMessage = "Ora a fost actualizată"
                                    errorMessage = null
                                    loadPollAndVotes()
                                } else {
                                    errorMessage = "Eroare la actualizare oră"
                                }
                                showDialog = false
                            }
                        }
                    }) {
                        Text("Salvează")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Anulează")
                    }
                }
            )
        }


    }
}


