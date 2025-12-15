package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strikes.busgapp.data.entity.Wallet
import com.strikes.busgapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletsScreen(
    wallets: List<Wallet>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wallets",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Manage your wallets",
                    fontSize = 14.sp,
                    color = StrikeTextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(wallets) { wallet ->
                WalletCard(wallet = wallet)
            }
        }
    }
}

@Composable
fun WalletCard(wallet: Wallet) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null,
                    tint = StrikeBlue,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = wallet.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = StrikeTextPrimary
                    )
                    if (wallet.isDefault) {
                        Text(
                            text = "Default",
                            fontSize = 12.sp,
                            color = StrikeSuccess
                        )
                    }
                }
            }
            
            if (wallet.isDefault) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Default",
                    tint = StrikeSuccess
                )
            }
        }
    }
}

