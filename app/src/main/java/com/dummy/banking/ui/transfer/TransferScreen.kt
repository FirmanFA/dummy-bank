package com.dummy.banking.ui.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dummy.banking.viewmodel.TransferUiState
import com.dummy.banking.viewmodel.TransferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    onBack: () -> Unit,
    viewModel: TransferViewModel = hiltViewModel()
) {
    var recipient by remember { mutableStateOf("") }
    var amountString by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is TransferUiState.Success) {
            snackbarHostState.showSnackbar((uiState as TransferUiState.Success).message)
            viewModel.resetState()
            recipient = ""
            amountString = ""
        } else if (uiState is TransferUiState.Error) {
            snackbarHostState.showSnackbar((uiState as TransferUiState.Error).message)
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Transfer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = recipient,
                onValueChange = { if (it.all { char -> char.isDigit() }) recipient = it },
                label = { Text("Nomor Rekening Tujuan") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Min. 10 digit") }
            )

            OutlinedTextField(
                value = amountString,
                onValueChange = { if (it.all { char -> char.isDigit() }) amountString = it },
                label = { Text("Nominal Transfer") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("Rp ") }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = recipient.length >= 10 && (amountString.toLongOrNull() ?: 0L) > 0 && uiState !is TransferUiState.Loading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState is TransferUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Kirim")
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Transfer") },
            text = {
                Text("Apakah Anda yakin ingin mengirim Rp $amountString ke rekening $recipient?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.transfer(recipient, amountString.toLong())
                    }
                ) {
                    Text("Ya, Kirim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
