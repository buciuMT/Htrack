package com.example.gym.ui.screens
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.Color
import com.example.gym.model.Notificare
import com.example.gym.viewmodel.NotificariViewModel
import com.example.gym.viewmodel.NotificariViewModelFactory
import android.util.Log
import androidx.compose.foundation.border

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
    val notificari by viewModel.notificari.collectAsState(emptyList())
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        try {
            viewModel.marcheazaToateCitite()
        } catch (e: Exception) {
            Log.e("NotificariPage", "Eroare la marcare ca citit", e)
        }
    }


    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notificari) { notificare ->
            NotificareItem(notificare, deduceTip = viewModel::deduceTipNotificare)
        }
    }
}
@Composable
fun NotificareItem(notificare: Notificare, deduceTip: (String) -> String) {
    val tip = deduceTip(notificare.mesaj)
    val borderColor = when {
        notificare.citit -> Color.Transparent
        tip == "abonare" -> Color(0xFF4CAF50)
        tip == "anulare" -> Color(0xFFF44336)
        else -> Color.Gray.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(width = if (!notificare.citit) 2.dp else 0.dp, color = borderColor, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = if (notificare.citit) Color.White else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(navController: NavController, Userid: Int)
{
    val viewModel: UserViewModel = viewModel()
    val notificariViewModel: NotificariViewModel = viewModel(
        factory = NotificariViewModelFactory(Userid)
    )

    LaunchedEffect(Userid) {
        viewModel.loadAbonamentActiv(Userid)
        viewModel.loadIstoricAbonamente(Userid)

    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("Cont") }
    val items = listOf("Cont", "Notificari","Istoric Abonamente", "Conversații")


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Meniu", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (selectedItem) {
                    "Cont" -> ContPage(viewModel)
                    "Notificari" -> NotificariPage(notificariViewModel)
                    "Istoric Abonamente" -> IstoricPage(viewModel)
                    "Conversații" -> ConversatiiPage()
                }
            }
        }
    }
}
