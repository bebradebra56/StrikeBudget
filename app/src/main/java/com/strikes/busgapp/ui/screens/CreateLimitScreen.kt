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
import com.strikes.busgapp.data.entity.DailyLimit
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLimitScreen(
    existingLimit: DailyLimit? = null,
    onSave: (DailyLimit) -> Unit,
    onDelete: ((DailyLimit) -> Unit)? = null,
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf(existingLimit?.amount?.toString() ?: "") }
    var isGlobal by remember { mutableStateOf(existingLimit?.category == null) }
    var selectedCategory by remember { mutableStateOf(existingLimit?.category ?: Category.FOOD) }
    var isMonthly by remember { mutableStateOf(existingLimit?.isMonthly ?: false) }
    var enabled by remember { mutableStateOf(existingLimit?.enabled ?: true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Disable type change if editing
    val canChangeType = existingLimit == null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingLimit == null) "Create Limit" else "Edit Limit",
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
            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Limit Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = StrikeBlue,
                    unfocusedIndicatorColor = StrikeTextSecondary
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Type Selection
            if (canChangeType) {
                Text(
                    text = "Limit Type",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StrikeTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TypeButton(
                        text = "Global Daily",
                        icon = "🌍",
                        selected = isGlobal,
                        onClick = { isGlobal = true },
                        modifier = Modifier.weight(1f)
                    )
                    TypeButton(
                        text = "Category",
                        icon = "📁",
                        selected = !isGlobal,
                        onClick = { isGlobal = false },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StrikeBluePale
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isGlobal) "🌍 Global Daily Limit" else "📁 ${existingLimit?.category?.displayName ?: "Category"} Limit",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StrikeBlue
                        )
                    }
                }
            }

            // Category Selection (if not global and can change)
            if (!isGlobal && canChangeType) {
                Text(
                    text = "Select Category",
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
            }

            // Period Selection
            Text(
                text = "Period",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = StrikeTextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TypeButton(
                    text = "Daily",
                    icon = "📅",
                    selected = !isMonthly,
                    onClick = { isMonthly = false },
                    modifier = Modifier.weight(1f)
                )
                TypeButton(
                    text = "Monthly",
                    icon = "📆",
                    selected = isMonthly,
                    onClick = { isMonthly = true },
                    modifier = Modifier.weight(1f)
                )
            }

            // Enable Toggle
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
                    Text(
                        text = "Enable Limit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = StrikeTextPrimary
                    )
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
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
                    if (amountValue != null && amountValue > 0) {
                        val limit = DailyLimit(
                            id = existingLimit?.id ?: 0,
                            amount = amountValue,
                            enabled = enabled,
                            category = if (isGlobal) null else selectedCategory,
                            isMonthly = isMonthly
                        )
                        onSave(limit)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StrikeBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Save Limit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Delete Button (if editing)
            if (existingLimit != null && onDelete != null) {
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
                        text = "Delete Limit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && existingLimit != null && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Limit?") },
            text = { Text("Are you sure you want to delete this limit?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(existingLimit)
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

