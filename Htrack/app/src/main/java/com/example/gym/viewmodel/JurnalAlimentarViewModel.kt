package com.example.gym.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.data.AddAlimentRequest
import com.example.gym.data.RemoveAlimentRequest
import com.example.gym.data.RetrofitClient
import com.example.gym.model.Aliment
import com.example.gym.model.JurnalAlimentarEntry
import com.example.gym.data.FoodJournalRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JurnalAlimentarViewModel(private val userId: Int) : ViewModel() {

    private val _showSearchInputDialog = mutableStateOf(false)
    val showSearchInputDialog: State<Boolean> = _showSearchInputDialog

    private val _showSearchResultsDialog = mutableStateOf(false)
    val showSearchResultsDialog: State<Boolean> = _showSearchResultsDialog

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _searchResults = mutableStateOf<List<Aliment>>(emptyList())
    val searchResults: State<List<Aliment>> = _searchResults

    private val _showAddFoodDialog = mutableStateOf(false)
    val showAddFoodDialog: State<Boolean> = _showAddFoodDialog

    private val _selectedAlimentToAdd = mutableStateOf<Aliment?>(null)
    val selectedAlimentToAdd: State<Aliment?> = _selectedAlimentToAdd

    private val _quantityInput = mutableStateOf("100")
    val quantityInput: State<String> = _quantityInput

    private val _dailyCalories = mutableIntStateOf(0)
    val dailyCalories: State<Int> = _dailyCalories

    private val _currentDate = mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val currentDate: State<String> = _currentDate

    val mealCategories = listOf("Mic Dejun", "Pranz", "Cina", "Snacks", "Apa")

    private val _selectedMealCategory = mutableStateOf<String?>(mealCategories.first())
    val selectedMealCategory: State<String?> = _selectedMealCategory

    val journalEntries = mutableStateListOf<JurnalAlimentarEntry>()

    init {
        loadDailyCalories()
        loadJournalEntries()
    }

    fun onSearchInputToggle(show: Boolean) {
        _showSearchInputDialog.value = show
    }

    fun onSearchResultsDialogToggle(show: Boolean) {
        _showSearchResultsDialog.value = show
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onAddFoodDialogToggle(show: Boolean) {
        _showAddFoodDialog.value = show
    }

    fun onSelectedAlimentChange(aliment: Aliment?) {
        _selectedAlimentToAdd.value = aliment
    }

    fun onQuantityInputChange(quantity: String) {
        _quantityInput.value = quantity
    }

    fun onMealCategorySelected(category: String) {
        _selectedMealCategory.value = category
        loadJournalEntries()
    }

    fun onPreviousDayClick() {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(_currentDate.value)
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        _currentDate.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        loadDailyCalories()
        loadJournalEntries()
    }

    fun onNextDayClick() {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(_currentDate.value)
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        _currentDate.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        loadDailyCalories()
        loadJournalEntries()
    }

    fun searchAlimente() {
        viewModelScope.launch {
            try {
                _searchResults.value = FoodJournalRepository.searchAlimente(_searchQuery.value)
                _showSearchInputDialog.value = false
                _showSearchResultsDialog.value = true
            } catch (e: Exception) {
                println("Error searching food: ${e.message}")
            }
        }
    }

    fun addAlimentToJournal() {
        viewModelScope.launch {
            _selectedAlimentToAdd.value?.let { aliment ->
                val quantity = _quantityInput.value.toIntOrNull()
                val mealCategory = _selectedMealCategory.value

                Log.d("FoodJournalVM", "Attempting to add food:")
                Log.d("FoodJournalVM", "Aliment: ${aliment.nume}, ID: ${aliment.id_aliment}")
                Log.d("FoodJournalVM", "Quantity: $quantity")
                Log.d("FoodJournalVM", "Meal Category: $mealCategory")
                Log.d("FoodJournalVM", "Current Date: ${_currentDate.value}")

                if (quantity != null && quantity > 0 && mealCategory != null) {
                    try {
                        val success = FoodJournalRepository.addAlimentToJournal(
                            userId,
                            aliment.id_aliment,
                            mealCategory,
                            quantity,
                            _currentDate.value
                        )
                        if (success) {
                            Log.d("FoodJournalVM", "Food added successfully!")
                            _showAddFoodDialog.value = false
                            loadJournalEntries()
                            loadDailyCalories()
                        } else {
                            Log.e("FoodJournalVM", "Failed to add food to journal: API call returned false")
                        }
                    } catch (e: Exception) {
                        Log.e("FoodJournalVM", "Error adding food to journal: ${e.message}", e) // Log și stack trace
                    }
                } else {
                    Log.w("FoodJournalVM", "Add food conditions not met: quantity=$quantity, mealCategory=$mealCategory")
                }
            } ?: Log.w("FoodJournalVM", "No aliment selected to add.")
        }
    }

    fun removeAlimentFromJournal(entry: JurnalAlimentarEntry) {
        viewModelScope.launch {
            try {
                if (FoodJournalRepository.removeAlimentFromJournal(entry.id_jurnal_alimentar)) {
                    journalEntries.remove(entry)
                    loadDailyCalories()
                }
            } catch (e: Exception) {
                println("Error removing food: ${e.message}")
            }
        }
    }

    fun loadJournalEntries() {
        viewModelScope.launch {
            _selectedMealCategory.value?.let { meal ->
                try {
                    val entries = FoodJournalRepository.getJournalEntriesByDateAndMeal(userId, _currentDate.value, meal)
                    journalEntries.clear()
                    journalEntries.addAll(entries)
                } catch (e: Exception) {
                    println("Error loading journal entries: ${e.message}")
                }
            }
        }
    }

    fun loadDailyCalories() {
        viewModelScope.launch {
            try {
                _dailyCalories.intValue = FoodJournalRepository.getDailyCalories(userId, _currentDate.value)
            } catch (e: Exception) {
                println("Error loading daily calories: ${e.message}")
            }
        }
    }

}