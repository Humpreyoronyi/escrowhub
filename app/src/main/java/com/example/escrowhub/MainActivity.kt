package com.example.escrowhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.escrowhub.navigation.MediaHubNavGraph
import com.example.escrowhub.ui.theme.EscrowHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EscrowHubTheme {
                // Stipulate the app to use navgraph
                // For screen load and start destination screen
                val navController = rememberNavController()
                MediaHubNavGraph(navController)
            }
        }
    }
}
