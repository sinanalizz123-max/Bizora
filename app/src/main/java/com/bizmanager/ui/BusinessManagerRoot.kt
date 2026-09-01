package com.bizmanager.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bizmanager.MainViewModel
import com.bizmanager.ui.onboarding.OnboardingFlow

@Composable
fun BusinessManagerRoot(viewModel: MainViewModel) {
    val onboardingDone by viewModel.onboardingDone.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.ensureSeed()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (onboardingDone) {
            MainNavHost(viewModel)
        } else {
            OnboardingFlow(viewModel)
        }
    }
}
