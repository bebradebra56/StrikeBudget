package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.entity.DailyLimit
import com.strikes.busgapp.data.entity.Transaction
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.ui.theme.*

@Composable
fun MainScreenWithBottomNav(
    allTransactions: List<Transaction>,
    todayTransactions: List<Transaction>,
    todayExpenses: Double,
    todayIncome: Double,
    weekExpenses: Double,
    monthExpenses: Double,
    globalDailyLimit: DailyLimit?,
    topCategories: List<Pair<Category, Double>>,
    allLimits: List<DailyLimit>,
    currency: String,
    onStrikeAddClick: () -> Unit,
    onInsightsClick: () -> Unit,
    onTransactionClick: (Long) -> Unit,
    onAddLimit: () -> Unit,
    onEditLimit: (Long) -> Unit,
    onToggleLimitEnabled: (DailyLimit, Boolean) -> Unit,
    onExport: () -> Unit,
    onTemplates: () -> Unit,
    onRecurring: () -> Unit,
    onWallets: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onResetData: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val showFab = selectedTab == 0 || selectedTab == 1 // Show FAB only on Dashboard and Activity

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = StrikeSurface,
                contentColor = StrikeBlue
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Dashboard"
                        )
                    },
//                    label = { Text("Dashboard") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StrikeBlue,
                        selectedTextColor = StrikeBlue,
                        indicatorColor = StrikeBluePale,
                        unselectedIconColor = StrikeTextSecondary,
                        unselectedTextColor = StrikeTextSecondary
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Activity"
                        )
                    },
//                    label = { Text("Activity") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StrikeBlue,
                        selectedTextColor = StrikeBlue,
                        indicatorColor = StrikeBluePale,
                        unselectedIconColor = StrikeTextSecondary,
                        unselectedTextColor = StrikeTextSecondary
                    )
                )

                FloatingActionButton(
                    onClick = onStrikeAddClick,
                    containerColor = StrikeBlue,
                    contentColor = StrikeBlue,
                    shape = CircleShape,
                    modifier = Modifier.size(70.dp)
                ) {
                    Text(text = "⚡", fontSize = 28.sp)
                }


                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Limits"
                        )
                    },
//                    label = { Text("Limits") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StrikeBlue,
                        selectedTextColor = StrikeBlue,
                        indicatorColor = StrikeBluePale,
                        unselectedIconColor = StrikeTextSecondary,
                        unselectedTextColor = StrikeTextSecondary
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    },
//                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StrikeBlue,
                        selectedTextColor = StrikeBlue,
                        indicatorColor = StrikeBluePale,
                        unselectedIconColor = StrikeTextSecondary,
                        unselectedTextColor = StrikeTextSecondary
                    )
                )
            }
        },
//        floatingActionButton = {
//            if (showFab) {
//                FloatingActionButton(
//                    onClick = onStrikeAddClick,
//                    containerColor = StrikeBlue,
//                    contentColor = StrikeBlue,
//                    shape = CircleShape,
//                    modifier = Modifier.size(70.dp)
//                ) {
//                    Text(text = "⚡", fontSize = 28.sp)
//                }
//            }
//        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = StrikeBackground
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> {
                    DashboardScreen(
                        todayExpenses = todayExpenses,
                        todayIncome = todayIncome,
                        weekExpenses = weekExpenses,
                        monthExpenses = monthExpenses,
                        dailyLimit = globalDailyLimit?.amount,
                        topCategories = topCategories,
                        todayTransactions = todayTransactions,
                        allLimits = allLimits,
                        currency = currency,
                        onStrikeAddClick = onStrikeAddClick,
                        onInsightsClick = onInsightsClick,
                        onNavigateToActivity = { selectedTab = 1 },
                        onNavigateToLimits = { selectedTab = 2 },
                        onNavigateToSettings = { selectedTab = 3 },
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
                1 -> {
                    ActivityScreen(
                        transactions = allTransactions,
                        currency = currency,
                        onTransactionClick = onTransactionClick,
                        onFilterClick = { }
                    )
                }
                2 -> {
                    LimitsScreen(
                        limits = allLimits,
                        todayExpenses = todayExpenses,
                        currency = currency,
                        onAddLimit = onAddLimit,
                        onEditLimit = onEditLimit,
                        onToggleLimitEnabled = onToggleLimitEnabled
                    )
                }
                3 -> {
                    SettingsScreen(
                        currency = currency,
                        onCurrencyChange = onCurrencyChange,
                        onExport = onExport,
                        onTemplates = onTemplates,
                        onRecurring = onRecurring,
                        onWallets = onWallets,
                        onResetData = onResetData
                    )
                }
            }
        }
    }
}

