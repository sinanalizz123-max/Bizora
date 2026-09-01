package com.bizmanager.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bizmanager.MainViewModel
import com.bizmanager.data.entity.BusinessEntity
import com.bizmanager.ui.onboarding.templates.BusinessTemplates
import kotlinx.coroutines.launch

@Composable
fun FinishStep(
    vm: MainViewModel,
    businessName: String,
    currency: String,
    businessType: String,
    sell: Boolean,
    inv: Boolean,
    cust: Boolean,
    exp: Boolean
) {
    val scope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You're all set!", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text(
            if (businessName.isNotBlank()) "Welcome, $businessName!" else "Welcome!",
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Your business is ready. Let's get started.",
            textAlign = TextAlign.Center,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = {
                scope.launch {
                    saving = true
                    val current = vm.businessRepository.getBusinessOnce()
                    vm.businessRepository.save(
                        current?.copy(
                            name = businessName,
                            currency = currency,
                            businessType = businessType,
                            businessTypeLabel = BusinessTemplates.labelFor(businessType)
                        ) ?: BusinessEntity(
                            name = businessName,
                            currency = currency,
                            businessType = businessType,
                            businessTypeLabel = BusinessTemplates.labelFor(businessType)
                        )
                    )
                    vm.settings.setSelling(sell)
                    vm.settings.setInventory(inv)
                    vm.settings.setCustomers(cust)
                    vm.settings.setExpenses(exp)
                    vm.settings.setOnboardingDone(true)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (saving) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Start Using Business Manager")
            }
        }
    }
}
