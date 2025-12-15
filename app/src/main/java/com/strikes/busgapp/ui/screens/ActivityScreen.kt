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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.entity.Transaction
import com.strikes.busgapp.data.model.TransactionType
import com.strikes.busgapp.ui.theme.*
import com.strikes.busgapp.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    transactions: List<Transaction>,
    currency: String,
    onTransactionClick: (Long) -> Unit,
    onFilterClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredTransactions = remember(transactions, searchQuery) {
        if (searchQuery.isEmpty()) {
            transactions
        } else {
            transactions.filter { transaction ->
                transaction.note.contains(searchQuery, ignoreCase = true) ||
                        transaction.category.displayName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val groupedTransactions = remember(filteredTransactions) {
        filteredTransactions.groupBy { DateUtils.getDayLabel(it.timestamp) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StrikeBackground)
    ) {
        // Search bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StrikeSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = StrikeTextSecondary
                )
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search transactions...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Filter",
                        tint = StrikeBlue
                    )
                }
            }
        }

        if (groupedTransactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "💸",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No transactions yet",
                        fontSize = 18.sp,
                        color = StrikeTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                groupedTransactions.forEach { (date, transactionsForDate) ->
                    item {
                        Text(
                            text = date,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StrikeTextSecondary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    items(transactionsForDate) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            currency = currency,
                            onClick = { onTransactionClick(transaction.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    currency: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StrikeSurface),
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
                        .background(
                            if (transaction.type == TransactionType.EXPENSE) StrikeBluePale else StrikeGoldLight,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.category.icon,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.category.displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = StrikeTextPrimary
                    )
                    if (transaction.note.isNotEmpty()) {
                        Text(
                            text = transaction.note,
                            fontSize = 13.sp,
                            color = StrikeTextSecondary
                        )
                    }
                    Text(
                        text = DateUtils.formatDate(transaction.timestamp, "HH:mm"),
                        fontSize = 12.sp,
                        color = StrikeTextSecondary
                    )
                }
            }

            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}$currency%.2f".format(transaction.amount),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.EXPENSE) StrikeError else StrikeSuccess
            )
        }
    }
}

