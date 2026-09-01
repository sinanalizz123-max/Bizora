package com.bizmanager.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bizmanager.MainViewModel
import com.bizmanager.ui.Routes
import java.util.Calendar

@Composable
fun DashboardScreen(navController: NavHostController) {
    // Dashboard accesses shared state via MainViewModel; this lightweight version
    // showcases navigation and module-aware entry points. A richer live summary
    // is computed in the Selling screen's current session data.
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = { navController.navigate(Routes.SELLING) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Quick Sale")
        }
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Today's Overview", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Track today's sales from the Selling and Sales screens.")
            }
        }
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Getting around", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Use the bottom bar to switch between your enabled modules. " +
                        "Manage features in Settings."
                )
            }
        }
    }
}
