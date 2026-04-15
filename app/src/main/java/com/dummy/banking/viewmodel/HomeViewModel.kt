package com.dummy.banking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dummy.banking.model.Transaction
import com.dummy.banking.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val transactions: List<Transaction>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchRecentTransactions()
    }

    private fun fetchRecentTransactions() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val transactions = transactionRepository.getTransactions().take(5)
                _uiState.value = HomeUiState.Success(transactions)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load transactions")
            }
        }
    }
}
