package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.entity.RecurringTransaction
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.data.model.RecurringFrequency
import com.strikes.busgapp.ui.theme.*
import com.strikes.busgapp.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecurringScreen(
    existingRecurring: RecurringTransaction? = null,
    onSave: (RecurringTransaction) -> Unit,
    onDelete: ((RecurringTransaction) -> Unit)? = null,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(existingRecurring?.name ?: "") }
    var amount by remember { mutableStateOf(existingRecurring?.amount?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(existingRecurring?.category ?: Category.BILLS) }
    var selectedFrequency by remember { mutableStateOf(existingRecurring?.frequency ?: RecurringFrequency.MONTHLY) }
    var notificationEnabled by remember { mutableStateOf(existingRecurring?.notificationEnabled ?: true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingRecurring == null) "Create Recurring" else "Edit Recurring",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                placeholder = { Text("e.g., Netflix Subscription") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = StrikeBlue,
                    unfocusedIndicatorColor = StrikeTextSecondary
                )
            )

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = StrikeBlue,
                    unfocusedIndicatorColor = StrikeTextSecondary
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Category Selection
            Text(
                text = "Category",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = StrikeTextPrimary
            )

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

            // Frequency Selection
            Text(
                text = "Frequency",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = StrikeTextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TypeButton(
                    text = "Weekly",
                    icon = "📅",
                    selected = selectedFrequency == RecurringFrequency.WEEKLY,
                    onClick = { selectedFrequency = RecurringFrequency.WEEKLY },
                    modifier = Modifier.weight(1f)
                )
                TypeButton(
                    text = "Monthly",
                    icon = "📆",
                    selected = selectedFrequency == RecurringFrequency.MONTHLY,
                    onClick = { selectedFrequency = RecurringFrequency.MONTHLY },
                    modifier = Modifier.weight(1f)
                )
            }

            // Notification Toggle
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
                    Column {
                        Text(
                            text = "Enable Notifications",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = StrikeTextPrimary
                        )
                        Text(
                            text = "Remind me before payment",
                            fontSize = 12.sp,
                            color = StrikeTextSecondary
                        )
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { notificationEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StrikeGold,
                            checkedTrackColor = StrikeGoldLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (name.isNotBlank() && amountValue != null && amountValue > 0) {
                        // Calculate next payment date
                        val nextPaymentDate = existingRecurring?.nextPaymentDate 
                            ?: when (selectedFrequency) {
                                RecurringFrequency.WEEKLY -> System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
                                RecurringFrequency.MONTHLY -> System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L)
                            }
                        
                        val recurring = RecurringTransaction(
                            id = existingRecurring?.id ?: 0,
                            name = name,
                            amount = amountValue,
                            category = selectedCategory,
                            frequency = selectedFrequency,
                            nextPaymentDate = nextPaymentDate,
                            notificationEnabled = notificationEnabled
                        )
                        onSave(recurring)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StrikeBlue
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = name.isNotBlank() && amount.toDoubleOrNull() != null
            ) {
                Text(
                    text = "Save Recurring",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Delete Button (if editing)
            if (existingRecurring != null && onDelete != null) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = StrikeError
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Delete Recurring",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && existingRecurring != null && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Recurring?") },
            text = { Text("Are you sure you want to delete this recurring bill?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(existingRecurring)
                        showDeleteDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StrikeError
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

