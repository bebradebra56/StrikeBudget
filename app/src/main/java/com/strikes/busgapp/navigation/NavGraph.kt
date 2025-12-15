package com.strikes.busgapp.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.strikes.busgapp.data.entity.Transaction
import com.strikes.busgapp.ui.screens.*
import com.strikes.busgapp.utils.CsvExporter
import com.strikes.busgapp.viewmodel.StrikeBudgetViewModel
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: StrikeBudgetViewModel = viewModel(),
    startDestination: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Collect flows
    val onboardingCompleted by viewModel.preferencesManager.onboardingCompleted.collectAsState(initial = false)
    val currency by viewModel.preferencesManager.currency.collectAsState(initial = "$")
    val allTransactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val todayTransactions by viewModel.todayTransactions.collectAsState(initial = emptyList())
    val weekTransactions by viewModel.weekTransactions.collectAsState(initial = emptyList())
    val monthTransactions by viewModel.monthTransactions.collectAsState(initial = emptyList())
    val allTemplates by viewModel.allTemplates.collectAsState(initial = emptyList())
    val allRecurring by viewModel.allRecurring.collectAsState(initial = emptyList())
    val allLimits by viewModel.allLimits.collectAsState(initial = emptyList())
    val allWallets by viewModel.allWallets.collectAsState(initial = emptyList())
    val globalDailyLimit by viewModel.globalDailyLimit.collectAsState(initial = null)
    val todayExpenses by viewModel.todayExpenses.collectAsState()
    val todayIncome by viewModel.todayIncome.collectAsState()
    val weekExpenses by viewModel.weekExpenses.collectAsState()
    val monthExpenses by viewModel.monthExpenses.collectAsState()
    val topCategories by viewModel.getTopCategoriesForToday().collectAsState()

    // State management - shared across navigation
    var showStrikeAdd by remember { mutableStateOf(false) }
    var strikeAddInitialData by remember { mutableStateOf<Triple<Double?, com.strikes.busgapp.data.model.Category?, com.strikes.busgapp.data.model.TransactionType?>?>(null) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    scope.launch {
                        viewModel.completeOnboarding()
                    }
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Screen with Bottom Navigation
        composable(Screen.Dashboard.route) {
            MainScreenWithBottomNav(
                allTransactions = allTransactions,
                todayTransactions = todayTransactions,
                todayExpenses = todayExpenses,
                todayIncome = todayIncome,
                weekExpenses = weekExpenses,
                monthExpenses = monthExpenses,
                globalDailyLimit = globalDailyLimit,
                topCategories = topCategories,
                allLimits = allLimits,
                currency = currency,
                onStrikeAddClick = {
                    strikeAddInitialData = null
                    showStrikeAdd = true
                },
                onInsightsClick = {
                    navController.navigate(Screen.Insights.route)
                },
                onTransactionClick = { transactionId ->
                    navController.navigate(Screen.OperationDetails.createRoute(transactionId))
                },
                onAddLimit = {
                    navController.navigate(Screen.CreateLimit.route)
                },
                onEditLimit = { limitId ->
                    navController.navigate(Screen.CreateLimit.createRoute(limitId))
                },
                onToggleLimitEnabled = { limit, enabled ->
                    scope.launch {
                        viewModel.updateLimit(limit.copy(enabled = enabled))
                    }
                },
                onExport = {
                    navController.navigate(Screen.Export.route)
                },
                onTemplates = {
                    navController.navigate(Screen.Templates.route)
                },
                onRecurring = {
                    navController.navigate(Screen.Recurring.route)
                },
                onWallets = {
                    navController.navigate(Screen.Wallets.route)
                },
                onCurrencyChange = { newCurrency ->
                    scope.launch {
                        viewModel.setCurrency(newCurrency)
                    }
                },
                onResetData = {
                    scope.launch {
                        viewModel.resetAllData()
                    }
                }
            )

            // Strike Add Bottom Sheet
            if (showStrikeAdd) {
                StrikeAddScreen(
                    onDismiss = { showStrikeAdd = false },
                    onSave = { transaction ->
                        viewModel.addTransaction(transaction)
                    },
                    initialAmount = strikeAddInitialData?.first,
                    initialCategory = strikeAddInitialData?.second,
                    initialType = strikeAddInitialData?.third
                )
            }
        }

        // Operation Details
        composable(
            route = Screen.OperationDetails.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0
            var transaction by remember { mutableStateOf<Transaction?>(null) }

            LaunchedEffect(transactionId) {
                transaction = viewModel.getTransaction(transactionId)
            }

            OperationDetailsScreen(
                transaction = transaction,
                currency = currency,
                onEdit = {
                    transaction?.let {
                        strikeAddInitialData = Triple(it.amount, it.category, it.type)
                        showStrikeAdd = true
                        // Delete old transaction after creating edited one
                        scope.launch {
                            viewModel.deleteTransaction(it)
                        }
                    }
                },
                onDuplicate = {
                    transaction?.let {
                        viewModel.addTransaction(
                            it.copy(id = 0, timestamp = System.currentTimeMillis())
                        )
                        navController.popBackStack()
                    }
                },
                onDelete = {
                    transaction?.let {
                        viewModel.deleteTransaction(it)
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() }
            )

            // Strike Add Bottom Sheet for editing
            if (showStrikeAdd) {
                StrikeAddScreen(
                    onDismiss = { 
                        showStrikeAdd = false
                        strikeAddInitialData = null
                    },
                    onSave = { newTransaction ->
                        viewModel.addTransaction(newTransaction)
                        navController.popBackStack()
                    },
                    initialAmount = strikeAddInitialData?.first,
                    initialCategory = strikeAddInitialData?.second,
                    initialType = strikeAddInitialData?.third
                )
            }
        }

        // Templates
        composable(Screen.Templates.route) {
            TemplatesScreen(
                templates = allTemplates,
                currency = currency,
                onTemplateClick = { template ->
                    strikeAddInitialData = Triple(
                        template.amount,
                        template.category,
                        template.type
                    )
                    showStrikeAdd = true
                    navController.popBackStack()
                },
                onAddTemplate = {
                    navController.navigate(Screen.CreateTemplate.route)
                },
                onEditTemplate = { templateId ->
                    navController.navigate(Screen.CreateTemplate.createRoute(templateId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Create/Edit Template
        composable(
            route = Screen.CreateTemplate.route,
            arguments = listOf(navArgument("templateId") { 
                type = NavType.LongType
                defaultValue = 0L
            })
        ) { backStackEntry ->
            val templateId = backStackEntry.arguments?.getLong("templateId") ?: 0
            var existingTemplate by remember { mutableStateOf(null as com.strikes.busgapp.data.entity.Template?) }

            LaunchedEffect(templateId) {
                if (templateId > 0) {
                    existingTemplate = viewModel.getTemplate(templateId)
                }
            }

            CreateTemplateScreen(
                existingTemplate = existingTemplate,
                onSave = { template ->
                    if (template.id == 0L) {
                        viewModel.addTemplate(template)
                    } else {
                        viewModel.updateTemplate(template)
                    }
                },
                onDelete = if (existingTemplate != null) {
                    { template -> viewModel.deleteTemplate(template) }
                } else null,
                onBack = { navController.popBackStack() }
            )
        }

        // Recurring
        composable(Screen.Recurring.route) {
            RecurringScreen(
                recurring = allRecurring,
                currency = currency,
                onAddRecurring = {
                    navController.navigate(Screen.CreateRecurring.route)
                },
                onEditRecurring = { recurringId ->
                    navController.navigate(Screen.CreateRecurring.createRoute(recurringId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Create/Edit Recurring
        composable(
            route = Screen.CreateRecurring.route,
            arguments = listOf(navArgument("recurringId") { 
                type = NavType.LongType
                defaultValue = 0L
            })
        ) { backStackEntry ->
            val recurringId = backStackEntry.arguments?.getLong("recurringId") ?: 0
            var existingRecurring by remember { mutableStateOf(null as com.strikes.busgapp.data.entity.RecurringTransaction?) }

            LaunchedEffect(recurringId) {
                if (recurringId > 0) {
                    existingRecurring = viewModel.getRecurring(recurringId)
                }
            }

            CreateRecurringScreen(
                existingRecurring = existingRecurring,
                onSave = { recurring ->
                    if (recurring.id == 0L) {
                        viewModel.addRecurring(recurring)
                    } else {
                        viewModel.updateRecurring(recurring)
                    }
                },
                onDelete = if (existingRecurring != null) {
                    { recurring -> viewModel.deleteRecurring(recurring) }
                } else null,
                onBack = { navController.popBackStack() }
            )
        }

        // Create/Edit Limit
        composable(
            route = Screen.CreateLimit.route,
            arguments = listOf(navArgument("limitId") { 
                type = NavType.LongType
                defaultValue = 0L
            })
        ) { backStackEntry ->
            val limitId = backStackEntry.arguments?.getLong("limitId") ?: 0
            var existingLimit by remember { mutableStateOf(null as com.strikes.busgapp.data.entity.DailyLimit?) }

            LaunchedEffect(limitId) {
                if (limitId > 0) {
                    existingLimit = viewModel.getLimit(limitId)
                }
            }

            CreateLimitScreen(
                existingLimit = existingLimit,
                onSave = { limit ->
                    if (limit.id == 0L) {
                        viewModel.addLimit(limit)
                    } else {
                        viewModel.updateLimit(limit)
                    }
                },
                onDelete = if (existingLimit != null) {
                    { limit -> viewModel.deleteLimit(limit) }
                } else null,
                onBack = { navController.popBackStack() }
            )
        }

        // Insights
        composable(Screen.Insights.route) {
            InsightsScreen(
                weekTransactions = weekTransactions,
                monthTransactions = monthTransactions,
                currency = currency,
                onBack = { navController.popBackStack() }
            )
        }

        // Wallets
        composable(Screen.Wallets.route) {
            WalletsScreen(
                wallets = allWallets,
                onBack = { navController.popBackStack() }
            )
        }

        // Export
        composable(Screen.Export.route) {
            ExportScreen(
                onExport = { startDate, endDate ->
                    scope.launch {
                        val transactions = allTransactions.filter { 
                            it.timestamp >= startDate && it.timestamp <= endDate
                        }
                        val file = CsvExporter.exportTransactionsToCsv(
                            context,
                            transactions,
                            startDate,
                            endDate
                        )
                        file?.let {
                            CsvExporter.shareFile(context, it)
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

