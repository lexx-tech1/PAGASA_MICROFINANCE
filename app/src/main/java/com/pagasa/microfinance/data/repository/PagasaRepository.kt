package com.pagasa.microfinance.data.repository

import com.pagasa.microfinance.data.model.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.pow

interface PagasaRepository {
    suspend fun login(emailOrMemberId: String, password: String): Result<MemberProfile>
    suspend fun requestOtp(destination: String): Result<Unit>
    suspend fun getDashboard(branchId: String, uid: String): DashboardSnapshot
    suspend fun getLoanTypes(branchId: String): List<LoanType>
    suspend fun submitLoanApplication(application: LoanApplication): Result<Unit>
    suspend fun getPaymentSchedule(loanId: String): List<PaymentScheduleItem>
    suspend fun getTransactions(branchId: String, uid: String): List<TransactionRecord>
    suspend fun getNotifications(uid: String): List<PagasaNotification>
}

data class DashboardSnapshot(
    val profile: MemberProfile,
    val activeLoan: Loan?,
    val savings: SavingsAccount,
    val recentTransactions: List<TransactionRecord>,
    val notifications: List<PagasaNotification>,
    val insights: List<FinancialInsight>
)

class MockPagasaRepository : PagasaRepository {
    private val branch = Branch(
        id = "BR-MNL-001",
        name = "PAG-ASA Manila Branch",
        address = "123 Community Finance Road, Manila",
        contactNumber = "+63 2 8123 4567",
        email = "manila@pagasa.example",
        officeHours = "Mon–Fri, 8:00 AM–5:00 PM"
    )

    private val profile = MemberProfile(
        uid = "demo-user-001",
        memberId = "PAGASA-2026-0001",
        fullName = "Maria Santos",
        email = "maria.santos@example.com",
        mobileNumber = "+63 917 000 0000",
        branchId = branch.id,
        branchName = branch.name,
        verified = true
    )

    override suspend fun login(emailOrMemberId: String, password: String): Result<MemberProfile> {
        delay(500)
        return if (emailOrMemberId.isNotBlank() && password.length >= 6) Result.success(profile)
        else Result.failure(IllegalArgumentException("Enter a valid member ID/email and password."))
    }

    override suspend fun requestOtp(destination: String): Result<Unit> {
        delay(350)
        return if (destination.isNotBlank()) Result.success(Unit) else Result.failure(IllegalArgumentException("Missing destination"))
    }

    override suspend fun getDashboard(branchId: String, uid: String): DashboardSnapshot {
        delay(300)
        val loan = Loan(
            id = "LN-2026-051",
            typeName = "Kabuhayan Business Loan",
            principalAmount = 50000.0,
            annualInterestRate = 0.12,
            remainingBalance = 28500.0,
            totalPaid = 21500.0,
            dueDate = LocalDate.now().plusDays(9),
            projectedPayoffDate = LocalDate.now().plusMonths(9),
            status = LoanStatus.RELEASED,
            penalties = 0.0,
            branchId = branchId
        )
        val savings = SavingsAccount(
            id = "SVG-001",
            balance = 18420.0,
            totalContributions = 22600.0,
            lastDepositDate = LocalDate.now().minusDays(6),
            branchId = branchId
        )
        val transactions = sampleTransactions(branchId)
        val notifications = listOf(
            PagasaNotification("N-1", NotificationType.DUE_DATE, "Payment due soon", "Your next payment is due in 9 days.", LocalDateTime.now().minusHours(3)),
            PagasaNotification("N-2", NotificationType.SAVINGS_MILESTONE, "Savings milestone", "You reached ₱18,000 in savings.", LocalDateTime.now().minusDays(2), read = true),
            PagasaNotification("N-3", NotificationType.BRANCH_ANNOUNCEMENT, "Branch advisory", "Manila branch will be closed on the next public holiday.", LocalDateTime.now().minusDays(4), read = true)
        )
        val insights = listOf(
            FinancialInsight("Financial health", "Good", "On-time payments", true),
            FinancialInsight("Savings growth", "+8.4%", "vs. last month", true),
            FinancialInsight("Penalty risk", "Low", "No overdue balance", true)
        )
        return DashboardSnapshot(profile, loan, savings, transactions.take(4), notifications, insights)
    }

    override suspend fun getLoanTypes(branchId: String): List<LoanType> {
        delay(250)
        return listOf(
            LoanType("LT-01", "Kabuhayan Business Loan", "For livelihood, sari-sari store, market stall, and microenterprise expansion.", 5000.0, 100000.0, 0.12, listOf(6, 12, 18, 24), listOf("Valid ID", "Barangay clearance", "Business photo", "Proof of income")),
            LoanType("LT-02", "Emergency Loan", "Fast assistance for medical, calamity, and urgent family needs.", 2000.0, 30000.0, 0.10, listOf(3, 6, 12), listOf("Valid ID", "Emergency proof/document", "Co-maker details")),
            LoanType("LT-03", "Education Loan", "Support for tuition, school supplies, and learning materials.", 3000.0, 50000.0, 0.09, listOf(6, 12, 18), listOf("Valid ID", "Enrollment form", "School assessment"))
        )
    }

    override suspend fun submitLoanApplication(application: LoanApplication): Result<Unit> {
        delay(600)
        return if (application.amount > 0) Result.success(Unit) else Result.failure(IllegalArgumentException("Invalid amount"))
    }

    override suspend fun getPaymentSchedule(loanId: String): List<PaymentScheduleItem> {
        delay(200)
        return (1..12).map { month ->
            val paid = month <= 5
            PaymentScheduleItem(
                dueDate = LocalDate.now().minusMonths((6 - month).toLong()).withDayOfMonth(25),
                amountDue = 4750.0,
                paidAmount = if (paid) 4750.0 else 0.0,
                isPaid = paid,
                reference = if (paid) "PAY-2026-${1000 + month}" else null
            )
        }
    }

    override suspend fun getTransactions(branchId: String, uid: String): List<TransactionRecord> = sampleTransactions(branchId)

    override suspend fun getNotifications(uid: String): List<PagasaNotification> = getDashboard(profile.branchId, uid).notifications

    private fun sampleTransactions(branchId: String) = listOf(
        TransactionRecord("TX-001", TransactionType.LOAN_REPAYMENT, "Loan repayment", 4750.0, LocalDateTime.now().minusDays(3), "OR-100212", branchId),
        TransactionRecord("TX-002", TransactionType.SAVINGS_DEPOSIT, "Savings deposit", 1000.0, LocalDateTime.now().minusDays(6), "DEP-55102", branchId),
        TransactionRecord("TX-003", TransactionType.LOAN_REPAYMENT, "Loan repayment", 4750.0, LocalDateTime.now().minusMonths(1), "OR-099871", branchId),
        TransactionRecord("TX-004", TransactionType.SAVINGS_DEPOSIT, "Savings deposit", 1200.0, LocalDateTime.now().minusMonths(1).minusDays(3), "DEP-54871", branchId),
        TransactionRecord("TX-005", TransactionType.WITHDRAWAL, "Approved savings withdrawal", 2500.0, LocalDateTime.now().minusMonths(2), "WDR-1120", branchId)
    )
}

fun calculateMonthlyPayment(principal: Double, annualRate: Double, months: Int): Double {
    if (months <= 0) return 0.0
    val monthlyRate = annualRate / 12.0
    if (monthlyRate == 0.0) return principal / months
    return principal * monthlyRate * (1 + monthlyRate).pow(months) / ((1 + monthlyRate).pow(months) - 1)
}
