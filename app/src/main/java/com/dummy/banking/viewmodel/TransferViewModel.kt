package com.dummy.banking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dummy.banking.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TransferUiState {
    object Idle : TransferUiState()
    object Loading : TransferUiState()
    data class Success(val message: String) : TransferUiState()
    data class Error(val message: String) : TransferUiState()
}

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransferUiState>(TransferUiState.Idle)
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    fun transfer(recipient: String, amount: Long) {
        viewModelScope.launch {
            _uiState.value = TransferUiState.Loading
            val result = transactionRepository.transfer(recipient, amount)
            result.onSuccess {
                _uiState.value = TransferUiState.Success("Transfer to $recipient of $amount was successful!")
            }.onFailure {
                _uiState.value = TransferUiState.Error(it.message ?: "Transfer failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = TransferUiState.Idle
    }
}
