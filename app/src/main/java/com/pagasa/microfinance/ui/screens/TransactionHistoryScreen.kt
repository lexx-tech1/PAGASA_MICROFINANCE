package com.pagasa.microfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagasa.microfinance.data.model.MemberProfile
import com.pagasa.microfinance.data.model.TransactionType
import com.pagasa.microfinance.data.repository.PagasaRepository
import com.pagasa.microfinance.ui.components.*
import com.pagasa.microfinance.ui.theme.PagasaGreen
import com.pagasa.microfinance.ui.theme.PagasaMuted

@Composable
fun TransactionHistoryScreen(repository: PagasaRepository, profile: MemberProfile) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf<TransactionType?>(null) }
    val transactions by produceState(initialValue = emptyList(), profile.uid) {
        value = repository.getTransactions(profile.branchId, profile.uid)
    }
    val filtered = transactions.filter { tx ->
        (filter == null || tx.type == filter) && (query.isBlank() || tx.title.contains(query, true) || tx.reference.contains(query, true))
    }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Transaction history", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by reference or transaction") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) }
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = filter == null, onClick = { filter = null }, label = { Text("All") })
                FilterChip(selected = filter == TransactionType.LOAN_REPAYMENT, onClick = { filter = TransactionType.LOAN_REPAYMENT }, label = { Text("Loan") })
                FilterChip(selected = filter == TransactionType.SAVINGS_DEPOSIT, onClick = { filter = TransactionType.SAVINGS_DEPOSIT }, label = { Text("Savings") })
            }
        }
        items(filtered.size) { index ->
            val tx = filtered[index]
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(tx.title, fontWeight = FontWeight.SemiBold)
                            Text(dateTimeText(tx.timestamp), color = PagasaMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = if (tx.type == TransactionType.SAVINGS_DEPOSIT) "+${peso(tx.amount)}" else peso(tx.amount),
                            fontWeight = FontWeight.Bold,
                            color = if (tx.type == TransactionType.SAVINGS_DEPOSIT) PagasaGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reference: ${tx.reference}", modifier = Modifier.weight(1f), color = PagasaMuted)
                        TextButton(onClick = { /* TODO: Generate PDF via PdfDocument and share. */ }) {
                            Icon(Icons.Outlined.Download, contentDescription = null)
                            Text("PDF")
                        }
                    }
                }
            }
        }
    }
}
