package com.strikes.busgapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currency: String,
    onCurrencyChange: (String) -> Unit,
    onExport: () -> Unit,
    onTemplates: () -> Unit,
    onRecurring: () -> Unit,
    onWallets: () -> Unit,
    onResetData: () -> Unit
) {
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StrikeBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = StrikeBackground
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // General Section
            item {
                SectionHeader(text = "General")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Star,
                    title = "Currency",
                    subtitle = currency,
                    onClick = { showCurrencyDialog = true }
                )
            }

            // Data Management
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(text = "Data Management")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.List,
                    title = "Templates",
                    subtitle = "Manage payment templates",
                    onClick = onTemplates
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Refresh,
                    title = "Recurring Bills",
                    subtitle = "Manage recurring payments",
                    onClick = onRecurring
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Share,
                    title = "Export Data",
                    subtitle = "Export transactions to CSV",
                    onClick = onExport
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Reset Data",
                    subtitle = "Clear all data",
                    onClick = { showResetDialog = true },
                    isDestructive = true
                )
            }

            // About Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(text = "About")
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "Privacy Policy",
                    subtitle = "Tap to read",
                    onClick =  {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://strikebudget.com/privacy-policy.html"))
                        context.startActivity(intent)
                    }
                )
            }

            item {
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
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Strike Budget",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = StrikeTextPrimary
                        )
                        Text(
                            text = "Version 1.0",
                            fontSize = 14.sp,
                            color = StrikeTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fast. Smart. Powerful.",
                            fontSize = 12.sp,
                            color = StrikeTextSecondary
                        )
                    }
                }
            }
        }
    }

    // Currency Dialog
    if (showCurrencyDialog) {
        CurrencyDialog(
            currentCurrency = currency,
            onDismiss = { showCurrencyDialog = false },
            onCurrencySelect = { newCurrency ->
                onCurrencyChange(newCurrency)
                showCurrencyDialog = false
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Data?") },
            text = { Text("This will delete all transactions, limits, templates, and recurring bills. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StrikeError
                    )
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = StrikeTextSecondary,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isDestructive) StrikeError else StrikeBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDestructive) StrikeError else StrikeTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = StrikeTextSecondary
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = StrikeTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CurrencyDialog(
    currentCurrency: String,
    onDismiss: () -> Unit,
    onCurrencySelect: (String) -> Unit
) {
    val currencies = listOf("$", "€", "£", "¥", "₽", "₴", "₹")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency") },
        text = {
            Column {
                currencies.forEach { currency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelect(currency) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currency,
                            fontSize = 18.sp
                        )
                        if (currency == currentCurrency) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = StrikeBlue
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

