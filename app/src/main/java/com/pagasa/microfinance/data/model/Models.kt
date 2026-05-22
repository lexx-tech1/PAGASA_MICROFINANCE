package com.pagasa.microfinance.data.model

import java.time.LocalDate
import java.time.LocalDateTime

enum class UserRole { MEMBER, BRANCH_STAFF, ADMIN }
enum class LoanStatus { NONE, DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, RELEASED, REJECTED, CLOSED }
enum class TransactionType { LOAN_REPAYMENT, SAVINGS_DEPOSIT, WITHDRAWAL, PENALTY, SERVICE_FEE }
enum class NotificationType { DUE_DATE, MISSED_PAYMENT, APPROVAL_UPDATE, SAVINGS_MILESTONE, BRANCH_ANNOUNCEMENT, PROMO }

data class Branch(
    val id: String,
    val name: String,
    val address: String,
    val contactNumber: String,
    val email: String,
    val officeHours: String
)

data class MemberProfile(
    val uid: String,
    val memberId: String,
    val fullName: String,
    val email: String,
    val mobileNumber: String,
    val branchId: String,
    val branchName: String,
    val role: UserRole = UserRole.MEMBER,
    val photoUrl: String? = null,
    val verified: Boolean = false
)

data class LoanType(
    val id: String,
    val name: String,
    val description: String,
    val minAmount: Double,
    val maxAmount: Double,
    val annualInterestRate: Double,
    val termsMonths: List<Int>,
    val requirements: List<String>
)

data class Loan(
    val id: String,
    val typeName: String,
    val principalAmount: Double,
    val annualInterestRate: Double,
    val remainingBalance: Double,
    val totalPaid: Double,
    val dueDate: LocalDate,
    val projectedPayoffDate: LocalDate,
    val status: LoanStatus,
    val penalties: Double = 0.0,
    val branchId: String
) {
    val progress: Float get() = if (principalAmount <= 0.0) 0f else (totalPaid / principalAmount).coerceIn(0.0, 1.0).toFloat()
}

data class PaymentScheduleItem(
    val dueDate: LocalDate,
    val amountDue: Double,
    val paidAmount: Double,
    val isPaid: Boolean,
    val reference: String? = null
)

data class SavingsAccount(
    val id: String,
    val balance: Double,
    val totalContributions: Double,
    val lastDepositDate: LocalDate,
    val branchId: String
)

data class TransactionRecord(
    val id: String,
    val type: TransactionType,
    val title: String,
    val amount: Double,
    val timestamp: LocalDateTime,
    val reference: String,
    val branchId: String
)

data class PagasaNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
    val read: Boolean = false
)

data class LoanApplication(
    val id: String,
    val loanTypeId: String,
    val amount: Double,
    val termMonths: Int,
    val status: LoanStatus,
    val submittedAt: LocalDateTime?,
    val uploadedDocuments: List<String>
)

data class FinancialInsight(
    val label: String,
    val value: String,
    val trend: String,
    val positive: Boolean
)
