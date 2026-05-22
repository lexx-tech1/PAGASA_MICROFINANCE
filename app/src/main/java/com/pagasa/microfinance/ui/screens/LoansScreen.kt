package com.pagasa.microfinance.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagasa.microfinance.data.model.*
import com.pagasa.microfinance.data.repository.PagasaRepository
import com.pagasa.microfinance.data.repository.calculateMonthlyPayment
import com.pagasa.microfinance.ui.components.*
import com.pagasa.microfinance.ui.theme.PagasaGreen
import com.pagasa.microfinance.ui.theme.PagasaMuted
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun LoansScreen(
    repository: PagasaRepository,
    profile: MemberProfile,
    activeLoan: Loan?,
    onApply: () -> Unit,
    onLoanDetail: () -> Unit
) {
    val loanTypes by produceState<List<LoanType>>(initialValue = emptyList(), profile.branchId) {
        value = repository.getLoanTypes(profile.branchId)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Text("Loans", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            activeLoan?.let { loan ->
                FinanceCard(
                    title = "Active loan",
                    value = peso(loan.remainingBalance),
                    subtitle = "${loan.typeName} • Due ${dateText(loan.dueDate)}",
                    trailing = { Button(onClick = onLoanDetail) { Text("Details") } }
                )
            } ?: EmptyState("No active loan", "Explore offers personalized for your membership and savings activity.", "Apply now", onApply)
        }
        item { SectionTitle("Available loan types") }
        items(loanTypes.size) { index ->
            val type = loanTypes[index]
            ElevatedCard(shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(type.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(type.description, color = PagasaMuted)
                        }
                        Icon(Icons.Outlined.Verified, contentDescription = null, tint = PagasaGreen)
                    }
                    Text("Amount: ${peso(type.minAmount)} – ${peso(type.maxAmount)}")
                    Text("Interest: ${(type.annualInterestRate * 100).toInt()}% p.a. • Terms: ${type.termsMonths.joinToString()} months", color = PagasaMuted)
                    AssistChip(onClick = {}, label = { Text(type.requirements.joinToString(" • ")) })
                    Button(onClick = onApply, modifier = Modifier.fillMaxWidth()) { Text("Start application") }
                }
            }
        }
    }
}

@Composable
fun LoanApplicationScreen(
    repository: PagasaRepository,
    profile: MemberProfile,
    onDone: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val loanTypes by produceState<List<LoanType>>(initialValue = emptyList(), profile.branchId) {
        value = repository.getLoanTypes(profile.branchId)
    }
    var selected by remember(loanTypes) { mutableStateOf<LoanType?>(loanTypes.firstOrNull()) }
    var amountText by remember { mutableStateOf("25000") }
    var term by remember { mutableIntStateOf(12) }
    var documents by remember { mutableStateOf<List<String>>(emptyList()) }
    var status by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { documents = documents + it.toString() }
    }
    val amount = amountText.toDoubleOrNull() ?: 0.0
    val estimatedPayment = calculateMonthlyPayment(amount, selected?.annualInterestRate ?: 0.0, term)

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDone) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") }
                Text("Loan application", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Choose loan type", style = MaterialTheme.typography.titleMedium)
                    loanTypes.forEach { type ->
                        FilterChip(
                            selected = selected?.id == type.id,
                            onClick = { selected = type; term = type.termsMonths.firstOrNull() ?: 12 },
                            label = { Text(type.name) }
                        )
                    }
                    OutlinedTextField(amountText, { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } }, modifier = Modifier.fillMaxWidth(), label = { Text("Requested amount") }, prefix = { Text("₱") })
                    Text("Term", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (selected?.termsMonths ?: listOf(6, 12, 18)).forEach { months ->
                            FilterChip(selected = term == months, onClick = { term = months }, label = { Text("$months mo") })
                        }
                    }
                    FinanceCard(
                        title = "Estimated monthly payment",
                        value = peso(estimatedPayment),
                        subtitle = "Estimate only. Final approval depends on branch assessment.",
                        accent = MaterialTheme.colorScheme.secondary,
                        trailing = { Icon(Icons.Outlined.Calculate, contentDescription = null) }
                    )
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Requirements", style = MaterialTheme.typography.titleMedium)
                    selected?.requirements.orEmpty().forEach { Text("• $it") }
                    OutlinedButton(onClick = { picker.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.CloudUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Upload document")
                    }
                    Text("Uploaded files: ${documents.size}", color = PagasaMuted)
                }
            }
        }
        item {
            status?.let { AssistChip(onClick = {}, label = { Text(it) }) }
            Button(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = selected != null && amount > 0,
                onClick = {
                    scope.launch {
                        val app = LoanApplication(
                            id = "APP-${UUID.randomUUID()}",
                            loanTypeId = selected!!.id,
                            amount = amount,
                            termMonths = term,
                            status = LoanStatus.SUBMITTED,
                            submittedAt = LocalDateTime.now(),
                            uploadedDocuments = documents
                        )
                        repository.submitLoanApplication(app)
                            .onSuccess { status = "Application submitted. Branch review is now in progress." }
                            .onFailure { status = it.message ?: "Unable to submit application." }
                    }
                }
            ) { Text("Submit application") }
        }
    }
}

@Composable
fun LoanDetailScreen(repository: PagasaRepository, loan: Loan?, onBack: () -> Unit) {
    if (loan == null) {
        ScreenScaffold("Loan details") { EmptyState("No loan selected", "There is no active loan to display.") }
        return
    }
    val schedule by produceState<List<PaymentScheduleItem>>(initialValue = emptyList(), loan.id) {
        value = repository.getPaymentSchedule(loan.id)
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") }
                Text("Loan details", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text(loan.typeName, style = MaterialTheme.typography.titleLarge)
                    InfoRow("Principal", peso(loan.principalAmount))
                    InfoRow("Remaining balance", peso(loan.remainingBalance))
                    InfoRow("Interest rate", "${(loan.annualInterestRate * 100).toInt()}% p.a.")
                    InfoRow("Penalties", peso(loan.penalties))
                    InfoRow("Next due date", dateText(loan.dueDate))
                    InfoRow("Projected payoff", dateText(loan.projectedPayoffDate))
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(progress = { loan.progress }, modifier = Modifier.fillMaxWidth().height(10.dp))
                }
            }
        }
        item { SectionTitle("Monthly repayment trend") }
        item { MiniLineChart(values = listOf(4.5f, 4.7f, 4.7f, 4.7f, 4.9f, 4.75f)) }
        item { SectionTitle("Payment schedule") }
        items(schedule.size) { index ->
            val item = schedule[index]
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(dateText(item.dueDate), fontWeight = FontWeight.SemiBold)
                        Text(item.reference ?: "Pending", color = PagasaMuted)
                    }
                    Text(peso(item.amountDue), fontWeight = FontWeight.Bold, color = if (item.isPaid) PagasaGreen else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
