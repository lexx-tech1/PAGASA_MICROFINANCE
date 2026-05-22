package com.pagasa.microfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagasa.microfinance.data.model.SavingsAccount
import com.pagasa.microfinance.data.model.TransactionRecord
import com.pagasa.microfinance.data.model.TransactionType
import com.pagasa.microfinance.ui.components.*
import com.pagasa.microfinance.ui.theme.PagasaGreen
import com.pagasa.microfinance.ui.theme.PagasaMuted

@Composable
fun SavingsScreen(savings: SavingsAccount?, transactions: List<TransactionRecord>) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Savings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            if (savings == null) {
                EmptyState("Savings unavailable", "Reconnect to load your latest balance.")
            } else {
                FinanceCard(
                    title = "Total savings balance",
                    value = peso(savings.balance),
                    subtitle = "Total contributions ${peso(savings.totalContributions)} • Last deposit ${dateText(savings.lastDepositDate)}",
                    accent = PagasaGreen
                )
            }
        }
        item { SectionTitle("Savings growth") }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp)) {
                    MiniLineChart(listOf(12f, 13.2f, 14.1f, 15.0f, 16.4f, 18.4f), color = PagasaGreen)
                    Text("Your savings are trending upward. Keep consistent weekly deposits.", color = PagasaMuted)
                }
            }
        }
        item { SectionTitle("Contribution history") }
        val deposits = transactions.filter { it.type == TransactionType.SAVINGS_DEPOSIT }
        if (deposits.isEmpty()) {
            item { EmptyState("No recent deposits", "Savings deposits will appear here after branch posting.") }
        } else {
            items(deposits.size) { index ->
                val tx = deposits[index]
                ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(tx.title, fontWeight = FontWeight.SemiBold)
                            Text(dateTimeText(tx.timestamp), color = PagasaMuted, style = MaterialTheme.typography.bodySmall)
                            Text("Ref: ${tx.reference}", color = PagasaMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("+${peso(tx.amount)}", color = PagasaGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Request withdrawal / branch approval") }
        }
    }
}
