package com.example.escrowhub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.escrowhub.navigation.Screen
import com.example.escrowhub.viewmodel.AuthState
import com.example.escrowhub.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController,
                         authViewModel: AuthViewModel = viewModel()
){
    var email by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading
    val isResetSent = authState is AuthState.PasswordReset
    val errorMessage = (authState as? AuthState.Error)?.message

    DisposableEffect(Unit) {
        onDispose { authViewModel.clearState() }
    }
    
    var sent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter){
            Column(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("Enter your registered email address to receive a password reset link.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color= MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.6f))
                
                Spacer(Modifier.height(40.dp))
                
                if(isResetSent && sent){
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Reset link sent successfully! Please check your email inbox.",
                            modifier = Modifier.padding(20.dp),
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }  else {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {email = it},
                        label = {Text("Email Address")},
                        leadingIcon = {
                            Icon(Icons.Default.Email, null)},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        )
                    )
                    
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            sent = true
                            authViewModel.sendPasswordReset(email)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape= RoundedCornerShape(16.dp),
                        enabled = email.isNotBlank() && !isLoading
                    ){
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send Reset Link", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
