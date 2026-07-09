package com.example.escrowhub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.escrowhub.model.Escrow
import com.example.escrowhub.navigation.Screen
import com.example.escrowhub.viewmodel.AuthState
import com.example.escrowhub.viewmodel.AuthViewModel
import com.example.escrowhub.viewmodel.EscrowState
import com.example.escrowhub.viewmodel.EscrowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    escrowViewModel: EscrowViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val userProfile by authViewModel.currentProfile.collectAsState()
    val escrowState by escrowViewModel.escrowState.collectAsState()

    // Handle navigation when the user logs out
    LaunchedEffect(authState) {
        if (authState is AuthState.Logout) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Dashboard.route) { inclusive = true }
            }
        }
    }

    // Trigger data fetch when user is loaded
    LaunchedEffect(userProfile) {
        userProfile?.let {
            escrowViewModel.fetchUserEscrows(it.uid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EscrowHub Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Wallet.route) }) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet")
                    }
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Escrow.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Escrow")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Welcome, ${userProfile?.fullname ?: "User"}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            when (val state = escrowState) {
                is EscrowState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is EscrowState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is EscrowState.SuccessList -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Your Active Deals",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        items(state.escrows) { escrow ->
                            EscrowItemCard(
                                itemName = escrow.itemName,
                                amount = escrow.amount,
                                status = escrow.status,
                                onClick = {
                                    navController.navigate(Screen.EscrowDetail.createRoute(escrow.id))
                                }
                            )
                        }
                    }
                }
                else -> {
                    // Show a message if there are no deals yet or if in Idle state
                    if (escrowState is EscrowState.Idle) {
                         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Loading your deals...")
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No active deals found.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EscrowItemCard(
    itemName: String,
    amount: Double,
    status: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Ksh $amount",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = when (status) {
                    "PENDING" -> Color(0xFFFF9800)
                    "DISPATCHED" -> Color(0xFF2196F3)
                    else -> Color(0xFF4CAF50)
                }
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}
