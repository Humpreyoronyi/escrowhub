package com.example.escrowhub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.escrowhub.navigation.Screen
import com.example.escrowhub.ui.theme.EscrowHubTheme
import com.example.escrowhub.viewmodel.AuthState
import com.example.escrowhub.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController,
                authViewModel: AuthViewModel = viewModel()){
    // data to be maintained in the composable : state
    // login info : email X password
    // mutableStateOf = declares that the data can be changed
    var email by remember { mutableStateOf("") }
    var password by remember {mutableStateOf("")}
    // auth view model references
    val authState by authViewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

    // when a user logins successfully take to the
    // dashboard
    LaunchedEffect(authState
    ) {
        if(authState is AuthState.Success){
            navController.navigate(Screen.Home.route){
                popUpTo(Screen.Login.route) {inclusive = true}
            }
        }
    }

    // controlling visibility of password showcase
    var passwordVisible by remember { mutableStateOf(false) }
    Box(modifier=Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center){
        Column(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(text="Welcome Back",
                style= MaterialTheme.typography.displayMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color=MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(text="Sign In to continue with EscrowHub",
                style= MaterialTheme.typography.bodyLarge,
                color=MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                ))
            Spacer(Modifier.height(48.dp))
            // form inputs
            // onValueCHange = captures the current input the user
            // enters
            OutlinedTextField(
                value= email, onValueChange = {email = it},
                label={Text("Email")},
                leadingIcon = {Icon(
                    Icons.Default.Email,
                    null)},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            // form inputs
            OutlinedTextField(
                value= password,
                onValueChange = {password = it},
                label={Text("Password")},
                shape = RoundedCornerShape(12.dp),
                singleLine = true ,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {Icon(
                    Icons.Default.Lock,
                    null
                )},
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(
                            if(passwordVisible)
                                Icons.Default.VisibilityOff else
                                Icons.Default.Visibility,
                            null
                        )
                    }
                },
                visualTransformation = if(passwordVisible)
                    VisualTransformation.None else
                    PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // link to forgot password screen
            TextButton(
                onClick = {
                    navController.navigate(
                        Screen.ForgotPassword.route)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?",
                    color=MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(20.dp))
            
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    authViewModel.login(email,password)
                },
                modifier= Modifier.fillMaxWidth().height(52.dp),
                shape=RoundedCornerShape(12.dp),
                enabled= email.isNotBlank() && password.isNotBlank()
                        && !isLoading
            ) {
                if(isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign In",style=MaterialTheme.typography
                        .bodyLarge)
                }
            }
//            Button(
//                onClick={},
//                modifier=Modifier.fillMaxWidth().height(52.dp),
//                shape = RoundedCornerShape(12.dp)
//            ){  Text("Sign In",
//                style = MaterialTheme.typography.bodyLarge)}
            Spacer(Modifier.height(16.dp))
            // to link to the register screen
            TextButton(
                onClick = {
                    authViewModel.clearState()
                    navController.navigate(
                        Screen.Register.route)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Don't have an account? Register",
                    color=MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview(){
    EscrowHubTheme {
        LoginScreen(rememberNavController())
    }
}