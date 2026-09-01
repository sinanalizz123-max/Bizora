package com.bizmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bizmanager.ui.BusinessManagerRoot
import com.bizmanager.ui.theme.BusinessManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BusinessManagerTheme {
                val viewModel: MainViewModel = viewModel()
                BusinessManagerRoot(viewModel)
            }
        }
    }
}
