package com.example.escrowhub.ui.screens

import android.graphics.Outline
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
fun RegisterScreen(navController: NavController,
                   authViewModel: AuthViewModel =
                       viewModel()){
    // data for the register screen i.e. state
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("")}
    var passwordVisible by remember {
        mutableStateOf(false) }
    // auth view model references
    val authState by authViewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

    // when a user registers successfully take to the
    // dashboard
    LaunchedEffect(authState
    ) {
        if(authState is AuthState.Success){
            navController.navigate(Screen.Home.route){
                popUpTo(Screen.Register.route) {inclusive = true}
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize().background(
        MaterialTheme.colorScheme.background
    )
    ){
        Column(modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(text="Create Account",
                style= MaterialTheme.typography.displayMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color=MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(text="Join EscrowHub Today",
                style= MaterialTheme.typography.bodyLarge,
                color=MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                ))
            Spacer(Modifier.height(32.dp))
            // form inputs for registration
            OutlinedTextField(
                value = fullname,
                onValueChange = {fullname = it},
                label = {Text("Full Name")},
                leadingIcon = {
                    Icon(Icons.Default.Person
                        ,null)},
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = {Text("Email")},
                leadingIcon = {
                    Icon(Icons.Default.Email
                        ,null)},
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )
            Spacer(Modifier.height(12.dp))
            //password
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = {Text("Password")},
                leadingIcon = {
                    Icon(Icons.Default.Lock
                        ,null)},
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
                    PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            // confirm password
            OutlinedTextField(
                value=confirmPassword,
                onValueChange = {confirmPassword = it},
                leadingIcon = {
                    Icon(Icons.Default.Lock,
                        null)},
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(28.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Register Button
            Button(
                onClick = {
                    if(password == confirmPassword){
                        authViewModel.register(fullname,email,password,
                            "User")
                    }
                },
                modifier= Modifier.fillMaxWidth().height(52.dp),
                shape=RoundedCornerShape(12.dp),
                enabled= fullname.isNotBlank() && email.isNotBlank()
                        && password.isNotBlank() &&
                        password == confirmPassword && !isLoading
            ) {
                if(isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Account",style=MaterialTheme.typography
                        .bodyLarge)
                }
            }
//            Button(
//                onClick={},
//                modifier=Modifier.fillMaxWidth().height(52.dp),
//                shape = RoundedCornerShape(12.dp)
//            ){  Text("Create Account",
//                style = MaterialTheme.typography.bodyLarge)}
            Spacer(Modifier.height(16.dp))
            // to link to the register screen
            TextButton(
                onClick = {
                    navController.navigate(
                        Screen.Login.route){
                        popUpTo(Screen.Register.route) {inclusive = true}
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Already have an account? Sign In",
                    color=MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview(){
    EscrowHubTheme {
        RegisterScreen(rememberNavController())}
}