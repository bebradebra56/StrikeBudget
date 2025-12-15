package com.strikes.busgapp.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.strikes.busgapp.data.entity.Transaction
import java.io.File
import java.io.FileWriter

object CsvExporter {
    fun exportTransactionsToCsv(
        context: Context,
        transactions: List<Transaction>,
        startDate: Long,
        endDate: Long
    ): File? {
        try {
            val fileName = "strike_budget_${DateUtils.formatDate(startDate, "yyyy-MM-dd")}_to_${DateUtils.formatDate(endDate, "yyyy-MM-dd")}.csv"
            val file = File(context.cacheDir, fileName)
            
            FileWriter(file).use { writer ->
                // Write header
                writer.append("Date,Time,Type,Category,Amount,Note,Wallet ID\n")
                
                // Write transactions
                transactions.forEach { transaction ->
                    val date = DateUtils.formatDate(transaction.timestamp, "yyyy-MM-dd")
                    val time = DateUtils.formatDate(transaction.timestamp, "HH:mm:ss")
                    writer.append("$date,$time,${transaction.type.name},${transaction.category.displayName},${transaction.amount},\"${transaction.note}\",${transaction.walletId}\n")
                }
            }
            
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share CSV"))
    }
}

