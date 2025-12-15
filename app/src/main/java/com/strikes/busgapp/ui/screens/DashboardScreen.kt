package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.ui.theme.*

@Composable
fun DashboardScreen(
    todayExpenses: Double,
    todayIncome: Double,
    weekExpenses: Double,
    monthExpenses: Double,
    dailyLimit: Double?,
    topCategories: List<Pair<Category, Double>>,
    todayTransactions: List<com.strikes.busgapp.data.entity.Transaction>,
    allLimits: List<com.strikes.busgapp.data.entity.DailyLimit>,
    currency: String,
    onStrikeAddClick: () -> Unit,
    onInsightsClick: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToLimits: () -> Unit,
    onNavigateToSettings: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StrikeBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Strike Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = StrikeTextPrimary
                )
            }

            // Balance Today Card
            item {
                BalanceTodayCard(
                    todayExpenses = todayExpenses,
                    todayIncome = todayIncome,
                    dailyLimit = dailyLimit,
                    currency = currency
                )
            }

            // Week & Month Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "This Week",
                        amount = weekExpenses,
                        currency = currency,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "This Month",
                        amount = monthExpenses,
                        currency = currency,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Top Categories
            if (topCategories.isNotEmpty()) {
                item {
                    TopCategoriesSection(
                        categories = topCategories,
                        currency = currency
                    )
                }
            }

            // Category Limits Progress
            val categoryLimits = allLimits.filter { it.category != null && it.enabled }
            if (categoryLimits.isNotEmpty()) {
                item {
                    CategoryLimitsSection(
                        limits = categoryLimits,
                        todayTransactions = todayTransactions,
                        currency = currency,
                        onNavigateToLimits = onNavigateToLimits
                    )
                }
            }

            // Quick Actions
            item {
                QuickActionsSection(
                    onInsightsClick = onInsightsClick
                )
            }
        }
    }
}


@Composable
fun BalanceTodayCard(
    todayExpenses: Double,
    todayIncome: Double,
    dailyLimit: Double?,
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
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Balance Today",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "⚡",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expenses
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "$currency%.2f".format(todayExpenses),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StrikeGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Income
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Income",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "$currency%.2f".format(todayIncome),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StrikeSuccess
                )
            }

            // Daily Limit Progress
            dailyLimit?.let { limit ->
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                val remaining = limit - todayExpenses
                val progress = (todayExpenses / limit).coerceIn(0.0, 1.0).toFloat()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Remaining",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$currency%.2f".format(remaining.coerceAtLeast(0.0)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (remaining >= 0) Color.White else StrikeError
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progress < 0.8f) StrikeGold else StrikeError,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrikeSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = StrikeTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$currency%.2f".format(amount),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = StrikeTextPrimary
            )
        }
    }
}

@Composable
fun TopCategoriesSection(
    categories: List<Pair<Category, Double>>,
    currency: String
) {
    Column {
        Text(
            text = "Top Categories Today",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = StrikeTextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        categories.forEach { (category, amount) ->
            CategoryItem(
                category = category,
                amount = amount,
                currency = currency
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    amount: Double,
    currency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrikeSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.icon,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .size(40.dp)
                        .background(StrikeBluePale, CircleShape)
                        .wrapContentSize(Alignment.Center)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = category.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = StrikeTextPrimary
                )
            }
            Text(
                text = "$currency%.2f".format(amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = StrikeBlue
            )
        }
    }
}

@Composable
fun CategoryLimitsSection(
    limits: List<com.strikes.busgapp.data.entity.DailyLimit>,
    todayTransactions: List<com.strikes.busgapp.data.entity.Transaction>,
    currency: String,
    onNavigateToLimits: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Category Limits",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = StrikeTextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            TextButton(onClick = onNavigateToLimits) {
                Text("See all", color = StrikeBlue)
            }
        }

        limits.take(3).forEach { limit ->
            limit.category?.let { category ->
                val spent = todayTransactions
                    .filter { it.category == category && it.type == com.strikes.busgapp.data.model.TransactionType.EXPENSE }
                    .sumOf { it.amount }
                
                CategoryLimitProgressCard(
                    category = category,
                    spent = spent,
                    limit = limit.amount,
                    currency = currency
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CategoryLimitProgressCard(
    category: Category,
    spent: Double,
    limit: Double,
    currency: String
) {
    val progress = (spent / limit).coerceIn(0.0, 1.0).toFloat()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrikeSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.icon,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = category.displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = StrikeTextPrimary
                    )
                }
                Text(
                    text = "$currency%.2f / $currency%.2f".format(spent, limit),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (progress < 0.9f) StrikeTextSecondary else StrikeError
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    progress < 0.7f -> StrikeSuccess
                    progress < 0.9f -> StrikeWarning
                    else -> StrikeError
                },
                trackColor = StrikeBackground
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    onInsightsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onInsightsClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrikeGoldLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📊", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "View Insights",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StrikeTextPrimary
                    )
                    Text(
                        text = "See your spending patterns",
                        fontSize = 12.sp,
                        color = StrikeTextSecondary
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = StrikeBlue
            )
        }
    }
}


