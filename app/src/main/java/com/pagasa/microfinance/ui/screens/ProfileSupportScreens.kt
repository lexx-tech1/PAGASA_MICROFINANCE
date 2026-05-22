package com.pagasa.microfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagasa.microfinance.data.model.MemberProfile
import com.pagasa.microfinance.data.model.PagasaNotification
import com.pagasa.microfinance.data.repository.PagasaRepository
import com.pagasa.microfinance.ui.components.dateTimeText
import com.pagasa.microfinance.ui.theme.PagasaBlue
import com.pagasa.microfinance.ui.theme.PagasaGreen
import com.pagasa.microfinance.ui.theme.PagasaMuted

@Composable
fun ProfileScreen(profile: MemberProfile, onLogout: () -> Unit, onSupport: () -> Unit, onBranchAdmin: () -> Unit) {
    var reminders by remember { mutableStateOf(true) }
    var promos by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(68.dp).clip(CircleShape).background(PagasaBlue), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(profile.memberId, color = PagasaMuted)
                        AssistChip(onClick = {}, label = { Text(if (profile.verified) "Verified member" else "Verification required") })
                    }
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileOption(Icons.Outlined.Badge, "Personal information", "Update mobile number, address, and photo")
                    ProfileOption(Icons.Outlined.LockReset, "Password & biometrics", "Change password and enable fingerprint/face unlock")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = PagasaGreen)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text("Payment reminders"); Text("Due dates and account alerts", color = PagasaMuted, style = MaterialTheme.typography.bodySmall) }
                        Switch(checked = reminders, onCheckedChange = { reminders = it })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Settings, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text("Promotional loan offers"); Text("Optional announcements", color = PagasaMuted, style = MaterialTheme.typography.bodySmall) }
                        Switch(checked = promos, onCheckedChange = { promos = it })
                    }
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Registered branch", style = MaterialTheme.typography.titleMedium)
                    Text(profile.branchName, fontWeight = FontWeight.SemiBold)
                    Text("Only records from this branch are visible to this account.", color = PagasaMuted)
                    OutlinedButton(onClick = onBranchAdmin, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.Business, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Branch staff/admin preview")
                    }
                }
            }
        }
        item { Button(onClick = onSupport, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Outlined.Chat, null); Spacer(Modifier.width(8.dp)); Text("Help & support") } }
        item { OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Outlined.Logout, null); Spacer(Modifier.width(8.dp)); Text("Logout") } }
    }
}

@Composable
private fun ProfileOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = PagasaMuted, style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
fun NotificationsScreen(repository: PagasaRepository, profile: MemberProfile, onBack: () -> Unit) {
    val notifications by produceState<List<PagasaNotification>>(initialValue = emptyList(), profile.uid) {
        value = repository.getNotifications(profile.uid)
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") }
                Text("Notifications", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        items(notifications.size) { index ->
            val item = notifications[index]
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(item.title, fontWeight = FontWeight.SemiBold)
                    Text(item.message, color = PagasaMuted)
                    Text(dateTimeText(item.timestamp), style = MaterialTheme.typography.bodySmall, color = PagasaMuted)
                }
            }
        }
    }
}

@Composable
fun SupportScreen(profile: MemberProfile, onBack: () -> Unit) {
    var feedback by remember { mutableStateOf("") }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") }
                Text("Help & support", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${profile.branchName} contact", style = MaterialTheme.typography.titleMedium)
                    Text("Phone: +63 2 8123 4567")
                    Text("Email: support@pagasa.example")
                    Text("Office hours: Mon–Fri, 8:00 AM–5:00 PM", color = PagasaMuted)
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("FAQ", style = MaterialTheme.typography.titleMedium)
                    Text("• How do I pay my loan? Visit your branch or use an approved payment partner.")
                    Text("• Why is my balance different? New branch postings may sync after verification.")
                    Text("• Can I apply offline? You can draft details offline, then submit when online.")
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Feedback & issue report", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = feedback, onValueChange = { feedback = it }, modifier = Modifier.fillMaxWidth(), minLines = 4, label = { Text("Tell us how we can improve") })
                    Button(onClick = { feedback = "" }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.Feedback, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Submit feedback")
                    }
                }
            }
        }
    }
}
