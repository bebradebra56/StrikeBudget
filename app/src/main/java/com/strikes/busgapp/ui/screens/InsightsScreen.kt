package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.entity.Transaction
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.data.model.TransactionType
import com.strikes.busgapp.ui.theme.*
import com.strikes.busgapp.utils.DateUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    weekTransactions: List<Transaction>,
    monthTransactions: List<Transaction>,
    currency: String,
    onBack: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("Week") }
    
    val transactions = if (selectedPeriod == "Week") weekTransactions else monthTransactions
    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
    val income = transactions.filter { it.type == TransactionType.INCOME }
    
    // Debug info
    val hasData = transactions.isNotEmpty()
    
    val totalExpenses = expenses.sumOf { it.amount }
    val totalIncome = income.sumOf { it.amount }
    val balance = totalIncome - totalExpenses
    
    val biggestExpense = expenses.maxByOrNull { it.amount }
    val mostUsedCategory = expenses.groupBy { it.category }
        .maxByOrNull { it.value.size }?.key
    
    val avgPerDay = if (expenses.isNotEmpty()) {
        expenses.sumOf { it.amount } / if (selectedPeriod == "Week") 7 else 30
    } else 0.0
    
    val categoryBreakdown = expenses.groupBy { it.category }
        .map { (category, trans) -> 
            category to trans.sumOf { it.amount }
        }
        .sortedByDescending { it.second }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Insights",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StrikeBackground
                )
            )
        },
        containerColor = StrikeBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period Selector
            item {
                PeriodSelector(
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { selectedPeriod = it }
                )
            }
            
            // Debug/Empty state
            if (!hasData) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = StrikeBluePale
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No data available",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StrikeTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add some transactions to see insights",
                                fontSize = 14.sp,
                                color = StrikeTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Debug: Week=${weekTransactions.size}, Month=${monthTransactions.size}",
                                fontSize = 12.sp,
                                color = StrikeTextSecondary
                            )
                        }
                    }
                }
            }

            // Overview Card
            if (hasData) {
                item {
                    OverviewCard(
                        totalExpenses = totalExpenses,
                        totalIncome = totalIncome,
                        balance = balance,
                        currency = currency
                    )
                }

                // Key Metrics
                item {
                    Text(
                        text = "Key Metrics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StrikeTextPrimary
                    )
                }
            }

            // Biggest Expense
            if (hasData) {
                biggestExpense?.let { transaction ->
                    item {
                        InsightCard(
                            title = "Biggest Expense",
                            value = "$currency%.2f".format(transaction.amount),
                            subtitle = "${transaction.category.displayName} • ${DateUtils.formatDate(transaction.timestamp, "MMM dd")}",
                            icon = transaction.category.icon
                        )
                    }
                }

                // Most Used Category
                mostUsedCategory?.let { category ->
                    item {
                        InsightCard(
                            title = "Most Used Category",
                            value = category.displayName,
                            subtitle = "${expenses.count { it.category == category }} transactions",
                            icon = category.icon
                        )
                    }
                }

                // Average per Day
                item {
                    InsightCard(
                        title = "Average Per Day",
                        value = "$currency%.2f".format(avgPerDay),
                        subtitle = "Based on ${if (selectedPeriod == "Week") "7" else "30"} days",
                        icon = "📊"
                    )
                }
            }

            // Category Breakdown
            if (hasData && categoryBreakdown.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Category Breakdown",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StrikeTextPrimary
                    )
                }

                item {
                    CategoryBreakdownCard(
                        categories = categoryBreakdown,
                        totalExpenses = totalExpenses,
                        currency = currency
                    )
                }
            }

            // Daily Spending Chart
            if (hasData && expenses.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Daily Spending",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StrikeTextPrimary
                    )
                }

                item {
                    DailySpendingChart(
                        transactions = expenses,
                        currency = currency,
                        days = if (selectedPeriod == "Week") 7 else 30
                    )
                }
            }

            // Transaction Count
            if (hasData) {
                item {
                    TransactionCountCard(
                        expenseCount = expenses.size,
                        incomeCount = income.size
                    )
                }
            }
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PeriodButton(
            text = "Week",
            selected = selectedPeriod == "Week",
            onClick = { onPeriodSelected("Week") },
            modifier = Modifier.weight(1f)
        )
        PeriodButton(
            text = "Month",
            selected = selectedPeriod == "Month",
            onClick = { onPeriodSelected("Month") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PeriodButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) StrikeBlue else StrikeBackground,
            contentColor = if (selected) Color.White else StrikeTextSecondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun OverviewCard(
    totalExpenses: Double,
    totalIncome: Double,
    balance: Double,
    currency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrikeBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Overview",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Expenses",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$currency%.2f".format(totalExpenses),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StrikeError
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Income",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$currency%.2f".format(totalIncome),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StrikeSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Balance",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "$currency%.2f".format(balance),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) StrikeGold else StrikeError
                )
            }
        }
    }
}

@Composable
fun CategoryBreakdownCard(
    categories: List<Pair<Category, Double>>,
    totalExpenses: Double,
    currency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrikeSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            categories.forEach { (category, amount) ->
                val percentage = if (totalExpenses > 0) (amount / totalExpenses * 100) else 0.0
                
                CategoryBreakdownItem(
                    category = category,
                    amount = amount,
                    percentage = percentage,
                    currency = currency
                )
                
                if (category != categories.last().first) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryBreakdownItem(
    category: Category,
    amount: Double,
    percentage: Double,
    currency: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(StrikeBluePale, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.icon,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = category.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = StrikeTextPrimary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$currency%.2f".format(amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = StrikeBlue
                )
                Text(
                    text = "%.1f%%".format(percentage),
                    fontSize = 12.sp,
                    color = StrikeTextSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = (percentage / 100).toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = StrikeBlue,
            trackColor = StrikeBackground
        )
    }
}

@Composable
fun TransactionCountCard(
    expenseCount: Int,
    incomeCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = StrikeSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💸",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$expenseCount",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StrikeTextPrimary
                )
                Text(
                    text = "Expenses",
                    fontSize = 12.sp,
                    color = StrikeTextSecondary
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = StrikeSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💰",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$incomeCount",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StrikeTextPrimary
                )
                Text(
                    text = "Income",
                    fontSize = 12.sp,
                    color = StrikeTextSecondary
                )
            }
        }
    }
}

@Composable
fun InsightCard(
    title: String,
    value: String,
    subtitle: String,
    icon: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrikeSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = StrikeTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StrikeTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = StrikeTextSecondary
                )
            }
            Text(
                text = icon,
                fontSize = 48.sp
            )
        }
    }
}

@Composable
fun DailySpendingChart(
    transactions: List<Transaction>,
    currency: String,
    days: Int
) {
    val calendar = Calendar.getInstance()
    val today = calendar.timeInMillis
    
    // For month view, group by weeks instead of days
    val chartData = if (days == 30) {
        // Create 4 weeks
        val weeksList = mutableListOf<Pair<String, Double>>()
        for (week in 1..4) {
            val weekStart = today - ((4 - week + 1) * 7 * 24 * 60 * 60 * 1000L)
            val weekEnd = today - ((4 - week) * 7 * 24 * 60 * 60 * 1000L)
            
            val weekAmount = transactions
                .filter { it.timestamp in weekStart..weekEnd }
                .sumOf { it.amount }
            
            weeksList.add("W$week" to weekAmount)
        }
        weeksList
    } else {
        // Create list of last 7 days
        val daysList = mutableListOf<Pair<String, Double>>()
        for (i in days - 1 downTo 0) {
            calendar.timeInMillis = today - (i * 24 * 60 * 60 * 1000L)
            val dayLabel = DateUtils.formatDate(calendar.timeInMillis, "EEE")
            val startOfDay = DateUtils.getStartOfDay(calendar.timeInMillis)
            val endOfDay = DateUtils.getEndOfDay(calendar.timeInMillis)
            
            val dayAmount = transactions
                .filter { it.timestamp in startOfDay..endOfDay }
                .sumOf { it.amount }
            
            daysList.add(dayLabel to dayAmount)
        }
        daysList
    }
    
    val maxAmount = chartData.maxOfOrNull { it.second } ?: 1.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrikeSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (days == 7) "Last 7 Days" else "Last 4 Weeks",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StrikeTextPrimary
                )
                Text(
                    text = "Max: $currency%.2f".format(maxAmount),
                    fontSize = 12.sp,
                    color = StrikeTextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach { (label, amount) ->
                    val height = if (maxAmount > 0) (amount / maxAmount * 140).dp else 0.dp
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (amount > 0) {
                            Text(
                                text = "$currency%.0f".format(amount),
                                fontSize = 10.sp,
                                color = StrikeTextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Box(
                            modifier = Modifier
                                .width(if (days == 7) 40.dp else 60.dp)
                                .height(height.coerceAtLeast(4.dp))
                                .background(
                                    color = when {
                                        amount == 0.0 -> StrikeBackground
                                        amount < maxAmount * 0.5 -> StrikeSuccess
                                        amount < maxAmount * 0.8 -> StrikeWarning
                                        else -> StrikeError
                                    },
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            color = StrikeTextSecondary,
                            fontWeight = if (amount > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

