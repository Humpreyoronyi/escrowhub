package com.example.escrowhub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.escrowhub.viewmodel.EscrowState
import com.example.escrowhub.viewmodel.EscrowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscrowDetailScreen(escrowId: String,
    navController: NavController,
    escrowViewModel: EscrowViewModel = viewModel()
) {
    // 1. We need a state to hold the specific escrow we find
    val escrowState by escrowViewModel.escrowState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    // 2. The moment this screen opens, fetch the details for this ID
    LaunchedEffect(escrowId) {
        escrowViewModel.fetchEscrowById(escrowId)
    }

    LaunchedEffect(escrowState) {
        if (escrowState is EscrowState.PaymentReleased) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escrow Deal Details") },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { 
        padding ->
        // For loading or Success
        when (val state = escrowState) {
            is EscrowState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is EscrowState.SuccessSingle -> {
                val escrow = state.escrow
                Column(
                    modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Display the "Contract"
                    Text(text = escrow.itemName, style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Amount: Ksh ${escrow.amount}", color = MaterialTheme.colorScheme.primary)
                    Text(text = "Description", style = MaterialTheme.typography.titleMedium)
                    Text(text = escrow.description)
                    Text(text = "Seller Email: ${escrow.sellerEmail}")
                    Text(text = "Seller Phone: ${escrow.sellerPhone}")

                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Action Button
                    if (escrow.status == "PENDING_PAYMENT") {
                        Button(
                            onClick = {
                                // Simulation for demo: Confirming M-Pesa payment
                                escrowViewModel.updateEscrowStatus(escrow.id, "PENDING")
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Confirm M-Pesa Payment Status")
                        }
                    } else if(escrow.status == "PENDING") {
                        Button(
                            onClick = {
                              escrowViewModel.updateEscrowStatus(escrow.id, "COMPLETED")
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("I have Received the Item - Release Funds")
                        }
                    } else {
                        // Show a completed badge if it is already done
                        Text("Transaction Completed", color = Color.Gray, modifier = Modifier.align(
                            Alignment.CenterHorizontally))
                    }
                }
            }
            is EscrowState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {}
        }
    }
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    navController.popBackStack()  // Takes you back after success
                }) {
                    Text("Back to Dashboard")
                }
            },
            title = { Text("Payment Successful!") },
            text = { 
                Text("Your payment has been successfully released and sent to the Seller's M-Pesa.")
                   },
            icon = { Icon(
                Icons.Default.CheckCircle, contentDescription = null, 
                tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp)) }
        )
    }
}
