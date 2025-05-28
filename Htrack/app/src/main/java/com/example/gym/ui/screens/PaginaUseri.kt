package com.example.gym.ui.screens
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gym.viewmodel.UserViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import com.example.gym.model.Notificare
import com.example.gym.viewmodel.NotificariViewModel
import com.example.gym.viewmodel.NotificariViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun ContPage(viewModel: UserViewModel) {
    val tipAbonament by remember { viewModel::tipAbonament }
    val numarSedinte by remember { viewModel::numarSedinte }

    Column(Modifier.padding(16.dp)) {
        Text("Pagina de cont")
        Spacer(modifier = Modifier.height(16.dp))

        if (!tipAbonament.isNullOrBlank() && tipAbonament != "NEACTIV") {
            Text("Tip abonament: $tipAbonament")
            Text("Număr ședințe: $numarSedinte")
        } else {
            Text("Nu ai un abonament activ.")
        }
    }
}



@Composable
fun IstoricPage(viewModel: UserViewModel = viewModel()) {
    val abonamente = viewModel.istoricAbonamente

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(items = abonamente) { ab ->
            val isActive = ab.tipAbonament != "NEACTIV"
            val backgroundColor = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
            val dataIncepereFormatted = ab.dataCreare.take(10)
            val dataFinalizareFormatted = ab.dataFinalizare.take(10)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (ab.tipAbonament == "NEACTIV") "Finalizat" else ab.tipAbonament.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Data început: $dataIncepereFormatted")
                    Text("Data finalizare: $dataFinalizareFormatted")
                }
            }
        }
    }
}



@Composable
fun ConversatiiPage() {
    Column(Modifier.padding(16.dp)) {
        Text("Pagina de conversații")
    }
}
@Composable
fun NotificariPage(viewModel: NotificariViewModel) {
    val notificari by viewModel.notificari.collectAsState()

    LaunchedEffect(Unit) {
        delay(2000)
        viewModel.marcheazaToateCitite()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notificari) { notificare ->
            NotificareItem(notificare)
        }
    }
}



@Composable
fun NotificareItem(notificare: Notificare) {
    val isUnread = !notificare.citit
    val tip = when {
        notificare.mesaj.contains("dezactivat", ignoreCase = true) -> "anulare"
        notificare.mesaj.contains("activat", ignoreCase = true) -> "abonare"
        else -> "generala"
    }

    val borderColor = when {
        isUnread && tip == "anulare" -> Color.Red
        isUnread -> Color(0xFF4CAF50)
        else -> Color.Transparent
    }

    val bgColor = if (notificare.citit) Color.White else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(width = if (isUnread) 2.dp else 0.dp, color = borderColor, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = notificare.mesaj, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notificare.data,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProgramareSedintaPage(viewModel: UserViewModel, userId: Int) {
    val poll by viewModel.pollActive.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    var mesaj by remember { mutableStateOf("") }
    var oraSelectata by remember { mutableStateOf<Int?>(null) }
    var hasVoted by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        viewModel.loadPollActiv(userId)
    }

    LaunchedEffect(poll?.id) {
        if (poll != null) {
            loading = true
            hasVoted = viewModel.hasVoted(userId, poll!!.id)
            loading = false
        } else {
            loading = false
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            mesaj.isNotEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = mesaj, modifier = Modifier.padding(24.dp))
                }
            }

            poll == null || hasVoted -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Ai votat deja sau nu există niciun poll activ.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }

            else -> {
                // Afișează poll-ul și butonul de vot
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    Text("Alege o oră pentru antrenament", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))

                    for (ora in 8..20) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { oraSelectata = ora },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (oraSelectata == ora) Color(0xFFBBDEFB) else Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = oraSelectata == ora,
                                    onClick = { oraSelectata = ora }
                                )
                                Text(
                                    text = "Ora ${"%02d:00".format(ora)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (oraSelectata != null && poll != null) {
                                val success = viewModel.voteaza(poll!!.id, userId, oraSelectata!!)
                                if (success) {
                                    mesaj = "Vot înregistrat cu succes pentru ora ${oraSelectata}:00"
                                    hasVoted = true
                                } else {
                                    mesaj = "Eroare la vot."
                                }
                            } else {
                                mesaj = "Te rugăm să selectezi o oră."
                            }
                        }
                    },
                    enabled = !hasVoted,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Votează", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(navController: NavController, Userid: Int) {
    val viewModel: UserViewModel = viewModel()
    val notificariViewModel: NotificariViewModel = viewModel(
        factory = NotificariViewModelFactory(Userid)
    )
    val notificari by notificariViewModel.notificari.collectAsState(emptyList())
    val areNotificariNecitite = notificari.any { !it.citit }

    LaunchedEffect(Userid) {
        viewModel.loadAbonamentActiv(Userid)
        viewModel.loadIstoricAbonamente(Userid)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("Cont") }

    val infiniteTransition = rememberInfiniteTransition(label = "notificari_puls")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (areNotificariNecitite) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "puls_scale"
    )

    val items = listOf("Cont", "Notificari", "Istoric Abonamente", "Programează ședința","Conversații", "Deconectează-te")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Meniu",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                items.forEach { item ->
                    val isLogout = item == "Deconectează-te"
                    val isNotificari = item == "Notificari"

                    val itemModifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .then(
                            if (isNotificari && areNotificariNecitite) Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            } else Modifier
                        )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item,
                                color = when {
                                    isLogout -> Color.Red
                                    isNotificari && areNotificariNecitite -> Color.Red
                                    else -> LocalContentColor.current
                                },
                                style = MaterialTheme.typography.bodyMedium
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
                        modifier = itemModifier
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {
                when (selectedItem) {
                    "Cont" -> ContPage(viewModel)
                    "Notificari" -> NotificariPage(notificariViewModel)
                    "Istoric Abonamente" -> IstoricPage(viewModel)
                    "Programează ședința" -> ProgramareSedintaPage(viewModel, Userid)
                    "Conversații" -> ConversatiiPage()
                }
            }
        }
    }
}

