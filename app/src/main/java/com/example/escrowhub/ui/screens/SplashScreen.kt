package com.example.escrowhub.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.navigation.NavController
import com.example.escrowhub.R
import com.example.escrowhub.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run animations in parallel
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                )
            )
        }
        launch {
            alpha.animateTo(1f, animationSpec = tween(1000))
        }
        
        delay(3.seconds)
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    // Dark Grey Background (Modern Fintech look)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) 
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .graphicsLayer(alpha = alpha.value)
        ) {
            // Logo inside a circular "Badge" for a premium look
            Surface(
                modifier = Modifier.size(240.dp), // Increased size for impact
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "EscrowHub Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            }
            
            Spacer(Modifier.height(32.dp))
            
            Text(
                text = "EscrowHub",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
        }
    }
}
