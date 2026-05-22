package com.pagasa.microfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagasa.microfinance.data.model.TransactionType
import com.pagasa.microfinance.data.repository.DashboardSnapshot
import com.pagasa.microfinance.ui.components.*
import com.pagasa.microfinance.ui.theme.PagasaGreen
import com.pagasa.microfinance.ui.theme.PagasaMuted
import java.time.LocalTime

@Composable
fun DashboardScreen(
    snapshot: DashboardSnapshot?,
    onOpenNotifications: () -> Unit,
    onApplyLoan: () -> Unit,
    onLoanDetail: () -> Unit,
    onRefresh: () -> Unit
) {
    if (snapshot == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val greeting = when (LocalTime.now().hour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("$greeting,", color = PagasaMuted)
                    Text(snapshot.profile.fullName.substringBefore(' '), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(snapshot.profile.branchName, style = MaterialTheme.typography.bodySmall, color = PagasaMuted)
                }
                IconButton(onClick = onRefresh) { Icon(Icons.Outlined.Refresh, contentDescription = "Refresh") }
                BadgedBox(badge = { Badge { Text(snapshot.notifications.count { !it.read }.toString()) } }) {
                    IconButton(onClick = onOpenNotifications) { Icon(Icons.Outlined.Notifications, contentDescription = "Notifications") }
                }
            }
        }
        item {
            snapshot.activeLoan?.let { loan ->
                ElevatedCard(shape = RoundedCornerShape(28.dp), onClick = onLoanDetail) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Active loan", color = PagasaMuted)
                                Text(loan.typeName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Next due: ${dateText(loan.dueDate)}", color = PagasaMuted)
                            }
                            ProgressRing(progress = loan.progress, label = "paid")
                        }
                        LinearProgressIndicator(progress = { loan.progress }, modifier = Modifier.fillMaxWidth().height(10.dp), color = PagasaGreen)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text("Remaining", color = PagasaMuted); Text(peso(loan.remainingBalance), fontWeight = FontWeight.Bold) }
                            Column(horizontalAlignment = Alignment.End) { Text("Paid", color = PagasaMuted); Text(peso(loan.totalPaid), fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            } ?: EmptyState(
                title = "No active loan",
                message = "You may qualify for a PAG-ASA livelihood or emergency loan based on your branch profile.",
                action = "View loan offers",
                onAction = onApplyLoan
            )
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FinanceCard(
                    modifier = Modifier.weight(1f),
                    title = "Savings",
                    value = peso(snapshot.savings.balance),
                    subtitle = "Last deposit ${dateText(snapshot.savings.lastDepositDate)}",
                    accent = PagasaGreen
                )
            }
        }
        item { SectionTitle("Financial insights") }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                snapshot.insights.take(3).forEach { insight ->
                    ElevatedCard(Modifier.weight(1f), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(14.dp)) {
                            Text(insight.label, style = MaterialTheme.typography.labelMedium, color = PagasaMuted)
                            Text(insight.value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(insight.trend, style = MaterialTheme.typography.bodySmall, color = if (insight.positive) PagasaGreen else MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        item { SectionTitle("Recent transactions", action = "View all") }
        items(snapshot.recentTransactions.size) { index ->
            val tx = snapshot.recentTransactions[index]
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(tx.title, fontWeight = FontWeight.SemiBold)
                        Text(dateTimeText(tx.timestamp), style = MaterialTheme.typography.bodySmall, color = PagasaMuted)
                    }
                    Text(
                        text = (if (tx.type == TransactionType.SAVINGS_DEPOSIT) "+" else "-") + peso(tx.amount),
                        fontWeight = FontWeight.Bold,
                        color = if (tx.type == TransactionType.SAVINGS_DEPOSIT) PagasaGreen else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
