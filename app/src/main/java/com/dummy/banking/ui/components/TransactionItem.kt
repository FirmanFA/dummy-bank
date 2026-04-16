package com.dummy.banking.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dummy.banking.model.Transaction
import com.dummy.banking.model.TransactionStatus
import com.dummy.banking.utils.CurrencyFormatter

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(transaction.recipient, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(transaction.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    CurrencyFormatter.formatToRupiah(transaction.amount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (transaction.status == TransactionStatus.Success) Color(0xFF2E7D32) else Color(
                        0xFFD32F2F
                    )
                )
                Text(
                    transaction.status.name,
                    fontSize = 11.sp,
                    color = if (transaction.status == TransactionStatus.Success) Color(0xFF2E7D32) else Color(
                        0xFFD32F2F
                    )
                )
            }
        }
    }
}
