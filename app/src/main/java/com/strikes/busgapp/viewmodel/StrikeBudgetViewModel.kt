package com.strikes.busgapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.strikes.busgapp.data.StrikeBudgetDatabase
import com.strikes.busgapp.data.entity.*
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.data.preferences.PreferencesManager
import com.strikes.busgapp.data.repository.*
import com.strikes.busgapp.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StrikeBudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val database = StrikeBudgetDatabase.getDatabase(application)
    
    private val transactionRepository = TransactionRepository(database.transactionDao())
    private val templateRepository = TemplateRepository(database.templateDao())
    private val recurringRepository = RecurringTransactionRepository(database.recurringTransactionDao())
    private val limitRepository = DailyLimitRepository(database.dailyLimitDao())
    private val walletRepository = WalletRepository(database.walletDao())
    
    val preferencesManager = PreferencesManager(application)

    // Flows
    val allTransactions = transactionRepository.getAllTransactions()
    val allTemplates = templateRepository.getAllTemplates()
    val allRecurring = recurringRepository.getAllRecurringTransactions()
    val allLimits = limitRepository.getAllLimits()
    val allWallets = walletRepository.getAllWallets()
    val globalDailyLimit = limitRepository.getGlobalDailyLimit()

    // Today's transactions
    val todayTransactions = transactionRepository.getTransactionsByDateRange(
        DateUtils.getStartOfDay(),
        DateUtils.getEndOfDay()
    )

    // This week's transactions (last 7 days)
    val weekTransactions = transactionRepository.getTransactionsByDateRange(
        System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L), // 7 days ago
        DateUtils.getEndOfToday()
    )

    // This month's transactions (last 30 days)
    val monthTransactions = transactionRepository.getTransactionsByDateRange(
        System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L), // 30 days ago
        DateUtils.getEndOfToday()
    )

    // Computed values
    val todayExpenses = todayTransactions.map { transactions ->
        transactions.filter { it.type == com.strikes.busgapp.data.model.TransactionType.EXPENSE }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayIncome = todayTransactions.map { transactions ->
        transactions.filter { it.type == com.strikes.busgapp.data.model.TransactionType.INCOME }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val weekExpenses = weekTransactions.map { transactions ->
        transactions.filter { it.type == com.strikes.busgapp.data.model.TransactionType.EXPENSE }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthExpenses = monthTransactions.map { transactions ->
        transactions.filter { it.type == com.strikes.busgapp.data.model.TransactionType.EXPENSE }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Transaction operations
    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.insertTransaction(transaction)
    }

    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.updateTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.deleteTransaction(transaction)
    }

    suspend fun getTransaction(id: Long): Transaction? {
        return transactionRepository.getTransactionById(id)
    }

    // Template operations
    fun addTemplate(template: Template) = viewModelScope.launch {
        templateRepository.insertTemplate(template)
    }

    fun updateTemplate(template: Template) = viewModelScope.launch {
        templateRepository.updateTemplate(template)
    }

    fun deleteTemplate(template: Template) = viewModelScope.launch {
        templateRepository.deleteTemplate(template)
    }

    suspend fun getTemplate(id: Long): Template? {
        return templateRepository.getTemplateById(id)
    }

    // Recurring operations
    fun addRecurring(recurring: RecurringTransaction) = viewModelScope.launch {
        recurringRepository.insertRecurringTransaction(recurring)
    }

    fun updateRecurring(recurring: RecurringTransaction) = viewModelScope.launch {
        recurringRepository.updateRecurringTransaction(recurring)
    }

    fun deleteRecurring(recurring: RecurringTransaction) = viewModelScope.launch {
        recurringRepository.deleteRecurringTransaction(recurring)
    }

    suspend fun getRecurring(id: Long): RecurringTransaction? {
        return recurringRepository.getRecurringTransactionById(id)
    }

    // Limit operations
    fun addLimit(limit: DailyLimit) = viewModelScope.launch {
        limitRepository.insertLimit(limit)
    }

    fun updateLimit(limit: DailyLimit) = viewModelScope.launch {
        limitRepository.updateLimit(limit)
    }

    fun deleteLimit(limit: DailyLimit) = viewModelScope.launch {
        limitRepository.deleteLimit(limit)
    }

    suspend fun getLimit(id: Long): DailyLimit? {
        return limitRepository.getLimitById(id)
    }

    // Wallet operations
    fun addWallet(wallet: Wallet) = viewModelScope.launch {
        walletRepository.insertWallet(wallet)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch {
        walletRepository.updateWallet(wallet)
    }

    fun deleteWallet(wallet: Wallet) = viewModelScope.launch {
        walletRepository.deleteWallet(wallet)
    }

    // Preferences
    fun completeOnboarding() = viewModelScope.launch {
        preferencesManager.setOnboardingCompleted(true)
    }

    fun setCurrency(currency: String) = viewModelScope.launch {
        preferencesManager.setCurrency(currency)
    }

    fun setTheme(theme: String) = viewModelScope.launch {
        preferencesManager.setTheme(theme)
    }

    fun resetAllData() = viewModelScope.launch {
        transactionRepository.deleteAllTransactions()
        preferencesManager.resetAll()
    }

    // Initialize default data
    init {
        viewModelScope.launch {
            // Add default wallets if none exist
            allWallets.first().let { wallets ->
                if (wallets.isEmpty()) {
                    walletRepository.insertWallet(Wallet(name = "Main Wallet", isDefault = true))
                    walletRepository.insertWallet(Wallet(name = "Cash", isDefault = false))
                    walletRepository.insertWallet(Wallet(name = "Card", isDefault = false))
                }
            }
        }
    }

    // Get top categories for today
    fun getTopCategoriesForToday(): StateFlow<List<Pair<Category, Double>>> {
        return todayTransactions.map { transactions ->
            transactions.filter { it.type == com.strikes.busgapp.data.model.TransactionType.EXPENSE }
                .groupBy { it.category }
                .map { (category, trans) -> category to trans.sumOf { it.amount } }
                .sortedByDescending { it.second }
                .take(3)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}

