package com.example.gym.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.gym.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.draw.clip
import com.example.gym.viewmodel.JurnalAlimentarViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodJournalScreen(
    userId: Int,
    navController: NavController,
    viewModel: JurnalAlimentarViewModel = viewModel(factory = JurnalAlimentarViewModelFactory(userId))
) {
    val showSearchInputDialog by viewModel.showSearchInputDialog
    val showSearchResultsDialog by viewModel.showSearchResultsDialog
    val searchQuery by viewModel.searchQuery
    val searchResults by viewModel.searchResults
    val showAddFoodDialog by viewModel.showAddFoodDialog
    val selectedAlimentToAdd by viewModel.selectedAlimentToAdd
    val quantityInput by viewModel.quantityInput
    val dailyCalories by viewModel.dailyCalories
    val currentDate by viewModel.currentDate

    val mealCategories = viewModel.mealCategories
    val selectedMealCategory by viewModel.selectedMealCategory
    val journalEntries = viewModel.journalEntries

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val initialPageIndex = selectedMealCategory?.let { mealCategories.indexOf(it) } ?: 0
    val pagerState = rememberPagerState(initialPage = initialPageIndex) { mealCategories.size }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onMealCategorySelected(mealCategories[pagerState.currentPage])
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.gymbg),
                contentScale = ContentScale.Crop
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.4f))
                .graphicsLayer {
                    renderEffect = BlurEffect(
                        radiusX = with(density) { 10.dp.toPx() },
                        radiusY = with(density) { 10.dp.toPx() },
                        edgeTreatment = TileMode.Clamp
                    )
                }
        )

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Jurnalul Alimentar",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onSearchInputToggle(true) }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Cauta aliment",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Black.copy(alpha = 0.8f),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Calorii zilnice: $dailyCalories kcal", color = Color.White)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { viewModel.onPreviousDayClick() }) {
                            Text("Ziua anterioara")
                        }
                        Text(
                            text = currentDate,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { viewModel.onNextDayClick() }) {
                            Text("Ziua urmatoare")
                        }
                    }
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(mealCategories.size) { index ->
                            val category = mealCategories[index]
                            val isSelected = index == pagerState.currentPage
                            val backgroundColor =
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            val textColor =
                                if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSecondary

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    modifier = Modifier.height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
                                ) {
                                    Text(text = category, color = textColor)
                                }

                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .height(3.dp)
                                            .width(24.dp)
                                            .background(textColor, RoundedCornerShape(1.dp))
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(7.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) { pageIndex ->
                        val currentMealCategory = mealCategories[pageIndex]
                        val currentMealEntries = journalEntries.filter { it.tip_masa == currentMealCategory }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Detalii ${mealCategories[pageIndex]}:",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                if (currentMealEntries.isEmpty()) {
                                    Text(
                                        "Nu există alimente adăugate.",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                    )
                                } else {
                                    currentMealEntries.forEach { entry ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${entry.aliment.nume} (${entry.cantitate}g)",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                val calculatedCalories = (entry.aliment.calorii * entry.cantitate / 100).toInt()
                                                Text(
                                                    text = "Calorii: $calculatedCalories kcal",
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            IconButton(onClick = { viewModel.removeAlimentFromJournal(entry) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Sterge aliment",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Keep dreaming! Keep motivating yourself! Keep growing!",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = androidx.compose.ui.geometry.Offset(4f, 4f),
                                blurRadius = 8f
                            )
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (showSearchInputDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onSearchInputToggle(false) },
                title = { Text("Cauta aliment") },
                text = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        label = { Text("Nume aliment") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = { viewModel.searchAlimente() }) {
                        Text("Cauta")
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.onSearchInputToggle(false) }) {
                        Text("Anuleaza")
                    }
                }
            )
        }

        if (showSearchResultsDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onSearchResultsDialogToggle(false) },
                title = { Text("Rezultate cautare") },
                text = {
                    if (searchResults.isEmpty()) {
                        Text("Nu s-au găsit alimente pentru '${searchQuery}'.")
                    } else {
                        LazyColumn {
                            items(searchResults) { aliment ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            viewModel.onSelectedAlimentChange(aliment)
                                            viewModel.onSearchResultsDialogToggle(false)
                                            viewModel.onAddFoodDialogToggle(true)
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(text = aliment.nume, style = MaterialTheme.typography.titleMedium)
                                        Text(text = "Calorii: ${aliment.calorii}kcal", style = MaterialTheme.typography.bodySmall)
                                        Text(text = "P: ${aliment.proteine}g, C: ${aliment.carbohidrati}g, G: ${aliment.grasimi_saturate + aliment.grasimi_nesaturate}g", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.onSearchResultsDialogToggle(false) }) {
                        Text("Inchide")
                    }
                }
            )
        }

        if (showAddFoodDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onAddFoodDialogToggle(false) },
                title = { Text("Adauga aliment in jurnal") },
                text = {
                    Column {
                        selectedAlimentToAdd?.let { aliment ->
                            Text("Aliment: ${aliment.nume}")
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = quantityInput,
                                onValueChange = { viewModel.onQuantityInputChange(it) },
                                label = { Text("Cantitate (g/ml)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                TextField(
                                    value = selectedMealCategory ?: "Selecteaza masa",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Tip masa") },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    mealCategories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category) },
                                            onClick = {
                                                viewModel.onMealCategorySelected(category)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        } ?: Text("Selecteaza un aliment mai intai.")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.addAlimentToJournal() },
                        enabled = selectedAlimentToAdd != null && quantityInput.toIntOrNull() != null && selectedMealCategory != null
                    ) {
                        Text("Adauga")
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.onAddFoodDialogToggle(false) }) {
                        Text("Anuleaza")
                    }
                }
            )
        }
    }
}

class JurnalAlimentarViewModelFactory(private val userId: Int) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JurnalAlimentarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JurnalAlimentarViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}