package com.pagasa.microfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagasa.microfinance.data.model.MemberProfile
import com.pagasa.microfinance.ui.components.FinanceCard
import com.pagasa.microfinance.ui.theme.PagasaMuted

@Composable
fun BranchAdminScreen(profile: MemberProfile, onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") }
                Text("Branch staff/admin", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(10.dp))
                        Text("Role-based access", style = MaterialTheme.typography.titleMedium)
                    }
                    Text("This screen is a preview for branch staff/admin accounts. Production access should require custom claims and branch-specific security rules.", color = PagasaMuted)
                    Text("Current branch: ${profile.branchName}", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FinanceCard(Modifier.weight(1f), "Pending loans", "14", "Need review")
                FinanceCard(Modifier.weight(1f), "Payments today", "₱86K", "Posted by branch")
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Admin capabilities to implement", style = MaterialTheme.typography.titleMedium)
                    Text("• Review and approve loan applications")
                    Text("• Post verified payments and savings deposits")
                    Text("• Send branch announcements")
                    Text("• Export audit reports")
                    Text("• Manage member support tickets")
                }
            }
        }
    }
}
