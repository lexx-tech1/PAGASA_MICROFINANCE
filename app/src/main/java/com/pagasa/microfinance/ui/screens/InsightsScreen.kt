package com.pagasa.microfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagasa.microfinance.data.repository.DashboardSnapshot
import com.pagasa.microfinance.ui.components.*
import com.pagasa.microfinance.ui.theme.PagasaGreen
import com.pagasa.microfinance.ui.theme.PagasaMuted

@Composable
fun InsightsScreen(snapshot: DashboardSnapshot?) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Financial insights", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        if (snapshot == null) {
            item { EmptyState("Insights unavailable", "Reconnect to load analytics.") }
        } else {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FinanceCard(Modifier.weight(1f), "Loan paid", "${((snapshot.activeLoan?.progress ?: 0f) * 100).toInt()}%", "Repayment completion")
                }
            }
            item {
                ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Loan repayment trend", style = MaterialTheme.typography.titleMedium)
                        MiniLineChart(listOf(3.5f, 4.0f, 4.4f, 4.75f, 4.75f, 4.75f))
                    }
                }
            }
            item {
                ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Savings growth", style = MaterialTheme.typography.titleMedium)
                        MiniLineChart(listOf(10f, 11.5f, 13f, 14.2f, 16f, 18.4f), color = PagasaGreen)
                    }
                }
            }
            item { SectionTitle("Health indicators") }
            items(snapshot.insights.size) { index ->
                val insight = snapshot.insights[index]
                ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(insight.label, fontWeight = FontWeight.SemiBold)
                            Text(insight.trend, color = PagasaMuted)
                        }
                        Text(insight.value, fontWeight = FontWeight.Bold, color = if (insight.positive) PagasaGreen else MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
