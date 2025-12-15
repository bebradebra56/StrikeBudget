package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.entity.RecurringTransaction
import com.strikes.busgapp.data.model.RecurringFrequency
import com.strikes.busgapp.ui.theme.*
import com.strikes.busgapp.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(
    recurring: List<RecurringTransaction>,
    currency: String,
    onAddRecurring: () -> Unit,
    onEditRecurring: (Long) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recurring Bills",
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
                actions = {
                    IconButton(onClick = onAddRecurring) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Recurring",
                            tint = StrikeBlue
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
        if (recurring.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🔄", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No recurring bills",
                        fontSize = 18.sp,
                        color = StrikeTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onAddRecurring) {
                        Text("Add your first recurring bill")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Upcoming Section
                val upcoming = recurring.filter { it.nextPaymentDate > System.currentTimeMillis() }
                    .sortedBy { it.nextPaymentDate }

                if (upcoming.isNotEmpty()) {
                    item {
                        Text(
                            text = "Upcoming",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StrikeTextPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(upcoming) { item ->
                        RecurringCard(
                            recurring = item,
                            currency = currency,
                            onClick = { onEditRecurring(item.id) }
                        )
                    }
                }

                // All Items
                item {
                    Text(
                        text = "All Recurring",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StrikeTextPrimary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(recurring.sortedBy { it.nextPaymentDate }) { item ->
                    RecurringCard(
                        recurring = item,
                        currency = currency,
                        onClick = { onEditRecurring(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecurringCard(
    recurring: RecurringTransaction,
    currency: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(StrikeBluePale, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recurring.category.icon,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = recurring.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = StrikeTextPrimary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (recurring.frequency) {
                                RecurringFrequency.WEEKLY -> "Weekly"
                                RecurringFrequency.MONTHLY -> "Monthly"
                            },
                            fontSize = 12.sp,
                            color = StrikeTextSecondary
                        )
                        Text(text = "•", fontSize = 12.sp, color = StrikeTextSecondary)
                        Text(
                            text = DateUtils.formatDate(recurring.nextPaymentDate),
                            fontSize = 12.sp,
                            color = StrikeTextSecondary
                        )
                    }
                    if (recurring.notificationEnabled) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔔",
                                fontSize = 12.sp
                            )
                            Text(
                                text = " Notifications on",
                                fontSize = 11.sp,
                                color = StrikeSuccess
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$currency%.2f".format(recurring.amount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = StrikeBlue
                )
            }
        }
    }
}

