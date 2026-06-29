package com.example.escrowhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.escrowhub.navigation.EscrowHubNavGraph
import com.example.escrowhub.ui.theme.EscrowHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EscrowHubTheme {
                val navController = rememberNavController()
                EscrowHubNavGraph(navController)
            }
        }
    }
}
