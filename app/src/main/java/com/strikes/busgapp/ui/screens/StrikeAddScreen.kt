package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.entity.Transaction
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.data.model.TransactionType
import com.strikes.busgapp.ui.theme.*
import com.strikes.busgapp.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrikeAddScreen(
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    initialAmount: Double? = null,
    initialCategory: Category? = null,
    initialType: TransactionType? = null
) {
    var amount by remember { mutableStateOf(initialAmount?.toString() ?: "") }
    var selectedType by remember { mutableStateOf(initialType ?: TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf(initialCategory ?: Category.FOOD) }
    var note by remember { mutableStateOf("") }
    var showNoteField by remember { mutableStateOf(false) }
    var selectedWalletId by remember { mutableStateOf(1L) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = StrikeSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ Strike Add",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StrikeTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = StrikeTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = StrikeBluePale,
                    unfocusedContainerColor = StrikeBackground,
                    focusedIndicatorColor = StrikeBlue,
                    unfocusedIndicatorColor = StrikeTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Type Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TypeButton(
                    text = "Expense",
                    icon = "💸",
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = { selectedType = TransactionType.EXPENSE },
                    modifier = Modifier.weight(1f)
                )
                TypeButton(
                    text = "Income",
                    icon = "💰",
                    selected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category Selection
            Text(
                text = "Category",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = StrikeTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Category.values().toList()) { category ->
                    CategoryChip(
                        category = category,
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Note Field Toggle
            if (!showNoteField) {
                TextButton(onClick = { showNoteField = true }) {
                    Text(
                        text = "+ Add note",
                        color = StrikeBlue,
                        fontSize = 14.sp
                    )
                }
            } else {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note") },
                    maxLines = 3,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = StrikeBlue,
                        unfocusedIndicatorColor = StrikeTextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        val transaction = Transaction(
                            amount = amountValue,
                            type = selectedType,
                            category = selectedCategory,
                            note = note,
                            walletId = selectedWalletId
                        )
                        onSave(transaction)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StrikeGold,
                    contentColor = StrikeBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "⚡ Save",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TypeButton(
    text: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) StrikeBlue else StrikeBackground,
            contentColor = if (selected) Color.White else StrikeTextSecondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CategoryChip(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(
                color = if (selected) StrikeBlue else StrikeBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.icon,
                fontSize = 28.sp
            )
            Text(
                text = category.displayName.take(5),
                fontSize = 10.sp,
                color = if (selected) Color.White else StrikeTextSecondary,
                maxLines = 1
            )
        }
    }
}

