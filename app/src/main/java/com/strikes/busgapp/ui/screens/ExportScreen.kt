package com.strikes.busgapp.ui.screens

import androidx.compose.foundation.layout.*
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
import com.strikes.busgapp.ui.theme.*
import com.strikes.busgapp.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onExport: (Long, Long) -> Unit,
    onBack: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("This Month") }
    val periods = listOf("Last 7 Days", "Last 30 Days", "This Month", "Last Month", "All Time")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Export Data",
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Export your transactions to CSV file",
                fontSize = 16.sp,
                color = StrikeTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Period Selection
            Text(
                text = "Select Period",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = StrikeTextPrimary
            )

            periods.forEach { period ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPeriod == period) StrikeBluePale else StrikeSurface
                    ),
                    onClick = { selectedPeriod = period }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = period,
                            fontSize = 16.sp,
                            color = StrikeTextPrimary
                        )
                        if (selectedPeriod == period) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = StrikeBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Export Button
            Button(
                onClick = {
                    val (startDate, endDate) = when (selectedPeriod) {
                        "Last 7 Days" -> {
                            val start = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                            start to System.currentTimeMillis()
                        }
                        "Last 30 Days" -> {
                            val start = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
                            start to System.currentTimeMillis()
                        }
                        "This Month" -> {
                            DateUtils.getStartOfMonth() to System.currentTimeMillis()
                        }
                        "Last Month" -> {
                            val cal = java.util.Calendar.getInstance()
                            cal.add(java.util.Calendar.MONTH, -1)
                            cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                            cal.set(java.util.Calendar.MINUTE, 0)
                            cal.set(java.util.Calendar.SECOND, 0)
                            val start = cal.timeInMillis
                            cal.add(java.util.Calendar.MONTH, 1)
                            cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
                            cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
                            cal.set(java.util.Calendar.MINUTE, 59)
                            val end = cal.timeInMillis
                            start to end
                        }
                        else -> 0L to System.currentTimeMillis()
                    }
                    onExport(startDate, endDate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StrikeBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Export & Share",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "The CSV file will be saved and you can share it via any app",
                fontSize = 12.sp,
                color = StrikeTextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

