package com.example.escrowhub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.escrowhub.model.Escrow
import com.example.escrowhub.viewmodel.AuthViewModel
import com.example.escrowhub.viewmodel.EscrowState
import com.example.escrowhub.viewmodel.EscrowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscrowScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    escrowViewModel: EscrowViewModel = viewModel()
) {
    // 1. State: Variables to hold user input
    var itemName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var buyerPhone by remember { mutableStateOf("") }
    var sellerEmail by remember { mutableStateOf("") }
    var sellerPhone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    // 2. Observer: Watch the ViewModel state
    val escrowState by escrowViewModel.escrowState.collectAsState()

    // 3. Logic: Navigation on success
    LaunchedEffect(escrowState) {
        if (escrowState is EscrowState.Success) {
            navController.popBackStack()
            escrowViewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Escrow") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Form Fields
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Price (Exact Amount)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = buyerPhone,
                onValueChange = { buyerPhone = it },
                label = { Text("Your M-Pesa Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            OutlinedTextField(
                value = sellerEmail,
                onValueChange = { 
                    sellerEmail = it 
                    validationError = null
                },
                label = { Text("Seller's Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = validationError != null
            )

            OutlinedTextField(
                value = sellerPhone,
                onValueChange = { sellerPhone = it },
                label = { Text("Seller's Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Detailed Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    val currentUser = authViewModel.currentProfile.value
                    if (sellerEmail.trim().lowercase() == currentUser?.email?.lowercase()) {
                        validationError = "You cannot be the seller in your own transaction."
                        return@Button
                    }

                    val newEscrow = Escrow(
                        itemName = itemName,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        description = description,
                        sellerEmail = sellerEmail,
                        sellerPhone = sellerPhone,
                        buyerId = currentUser?.uid ?: "",
                        status = "PENDING"
                    )
                    escrowViewModel.createEscrowWithPayment(newEscrow, buyerPhone)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = itemName.isNotBlank() && amount.isNotBlank() && sellerEmail.isNotBlank() && buyerPhone.isNotBlank() && (escrowState !is EscrowState.Loading)
            ) {
                if (escrowState is EscrowState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Deposit Funds & Start New Escrow")
                }
            }

            // Error Display
            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (escrowState is EscrowState.Error) {
                Text(
                    text = (escrowState as EscrowState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
