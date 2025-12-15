package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.entity.DailyLimit
import com.strikes.busgapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitsScreen(
    limits: List<DailyLimit>,
    todayExpenses: Double,
    currency: String,
    onAddLimit: () -> Unit,
    onEditLimit: (Long) -> Unit,
    onToggleLimitEnabled: (DailyLimit, Boolean) -> Unit
) {
    val globalLimit = limits.firstOrNull { it.category == null && !it.isMonthly }
    val categoryLimits = limits.filter { it.category != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StrikeBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Limits",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = StrikeBackground
            ),
            actions = {
                IconButton(onClick = onAddLimit) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Limit",
                        tint = StrikeBlue
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Charge Level Card
            globalLimit?.let { limit ->
                item {
                    ChargeLevelCard(
                        limit = limit,
                        todayExpenses = todayExpenses,
                        currency = currency,
                        onClick = { onEditLimit(limit.id) },
                        onToggleEnabled = { enabled ->
                            onToggleLimitEnabled(limit, enabled)
                        }
                    )
                }
            }
            
            // Add Global Limit button if not exists
            if (globalLimit == null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onAddLimit),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = StrikeBluePale
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Global Limit",
                                tint = StrikeBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Set Daily Limit",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StrikeBlue
                            )
                        }
                    }
                }
            }

            // Category Limits Section
            if (categoryLimits.isNotEmpty()) {
                item {
                    Text(
                        text = "Category Limits",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StrikeTextPrimary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(categoryLimits) { limit ->
                    CategoryLimitCard(
                        limit = limit,
                        currency = currency,
                        onClick = { onEditLimit(limit.id) },
                        onToggleEnabled = { enabled ->
                            onToggleLimitEnabled(limit, enabled)
                        }
                    )
                }
            }

            // Empty State
            if (limits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎯", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No limits set",
                                fontSize = 18.sp,
                                color = StrikeTextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = onAddLimit) {
                                Text("Add your first limit")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChargeLevelCard(
    limit: DailyLimit,
    todayExpenses: Double,
    currency: String,
    onClick: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    text = "⚡ Daily Limit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Switch(
                    checked = limit.enabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = StrikeGold,
                        checkedTrackColor = StrikeGoldLight
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val remaining = limit.amount - todayExpenses
            val progress = (todayExpenses / limit.amount).coerceIn(0.0, 1.0).toFloat()

            Text(
                text = "Daily Limit: $currency%.2f".format(limit.amount),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Remaining: $currency%.2f".format(remaining.coerceAtLeast(0.0)),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (remaining >= 0) StrikeGold else StrikeError
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = when {
                    progress < 0.7f -> StrikeSuccess
                    progress < 0.9f -> StrikeWarning
                    else -> StrikeError
                },
                trackColor = Color.White.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(progress * 100).toInt()}% used",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CategoryLimitCard(
    limit: DailyLimit,
    currency: String,
    onClick: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = limit.category?.icon ?: "📌",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = limit.category?.displayName ?: "Other",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = StrikeTextPrimary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (limit.isMonthly) "Monthly" else "Daily",
                            fontSize = 13.sp,
                            color = StrikeTextSecondary
                        )
                        Text(text = "•", fontSize = 13.sp, color = StrikeTextSecondary)
                        Text(
                            text = "$currency%.2f".format(limit.amount),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StrikeBlue
                        )
                    }
                }
            }
            Switch(
                checked = limit.enabled,
                onCheckedChange = onToggleEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = StrikeGold,
                    checkedTrackColor = StrikeGoldLight
                )
            )
        }
    }
}

