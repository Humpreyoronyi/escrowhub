package com.example.escrowhub.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.escrowhub.navigation.Screen
import com.example.escrowhub.viewmodel.AuthState
import com.example.escrowhub.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // Animation states
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(30f) }
    
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        // Run animations in parallel
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1200)
            )
        }
        launch {
            translateY.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearOutSlowInEasing
                )
            )
        }
        
        delay(3.seconds)
        
        if (authState is AuthState.Success) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    // Modern Deep Gradient Background
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000000), // Pure Black
                        Color(0xFF212121)  // Dark Gray
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Glowing Logo Circle
            Surface(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
                    .graphicsLayer(alpha = alpha.value),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "EscrowHub Logo",
                        modifier = Modifier
                            .size(100.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(Modifier.height(48.dp))
            
            // Premium Typography
            Text(
                text = "ESCROWHUB",
                style = MaterialTheme.typography.displaySmall.copy(
                    letterSpacing = 10.sp,
                    fontWeight = FontWeight.ExtraLight,
                    fontSize = 32.sp
                ),
                color = Color.White,
                modifier = Modifier.graphicsLayer(
                    alpha = alpha.value,
                    translationY = translateY.value
                )
            )
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                text = "Secure Transactions. Ultimate Trust.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.graphicsLayer(
                    alpha = alpha.value,
                    translationY = translateY.value * 0.5f // Moves slower for parallax effect
                )
            )
        }
    }
}
