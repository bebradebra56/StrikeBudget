package com.strikes.busgapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.strikes.busgapp.navigation.NavGraph
import com.strikes.busgapp.navigation.Screen
import com.strikes.busgapp.ui.theme.StrikeBudgetTheme
import com.strikes.busgapp.viewmodel.StrikeBudgetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StrikeBudgetTheme {
                val viewModel: StrikeBudgetViewModel = viewModel()
                val navController = rememberNavController()
                val onboardingCompleted by viewModel.preferencesManager.onboardingCompleted
                    .collectAsState(initial = null)

                // Wait for preferences to load before deciding start destination
                onboardingCompleted?.let { completed ->
                    val startDestination = if (completed) {
                        Screen.Dashboard.route
                    } else {
                        Screen.Onboarding.route
                    }

                    NavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
