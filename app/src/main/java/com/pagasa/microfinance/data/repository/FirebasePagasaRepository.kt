package com.pagasa.microfinance.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pagasa.microfinance.BuildConfig
import com.pagasa.microfinance.data.model.LoanApplication
import com.pagasa.microfinance.data.model.LoanType
import com.pagasa.microfinance.data.model.MemberProfile
import com.pagasa.microfinance.data.model.PagasaNotification
import com.pagasa.microfinance.data.model.PaymentScheduleItem
import com.pagasa.microfinance.data.model.TransactionRecord
import kotlinx.coroutines.tasks.await

/**
 * Production repository scaffold.
 *
 * This class is intentionally conservative: it checks that Firebase is configured before use.
 * Add app/google-services.json, enable Authentication/Firestore/Storage/FCM, then map Firestore
 * documents to the models in data/model/Models.kt.
 */
class FirebasePagasaRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PagasaRepository {

    private fun ensureConfigured() {
        check(BuildConfig.FIREBASE_CONFIGURED) {
            "Firebase is not configured. Add app/google-services.json or use MockPagasaRepository."
        }
    }

    override suspend fun login(emailOrMemberId: String, password: String): Result<MemberProfile> = runCatching {
        ensureConfigured()
        val email = emailOrMemberId.trim()
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("Missing authenticated user")
        val userDoc = firestore.collection("users").document(uid).get().await()
        MemberProfile(
            uid = uid,
            memberId = userDoc.getString("memberId").orEmpty(),
            fullName = userDoc.getString("fullName").orEmpty(),
            email = userDoc.getString("email").orEmpty(),
            mobileNumber = userDoc.getString("mobileNumber").orEmpty(),
            branchId = userDoc.getString("branchId").orEmpty(),
            branchName = userDoc.getString("branchName").orEmpty(),
            verified = userDoc.getBoolean("verified") ?: false
        )
    }

    override suspend fun requestOtp(destination: String): Result<Unit> = runCatching {
        ensureConfigured()
        // Recommended: trigger OTP with Firebase Phone Auth for SMS or a Cloud Function for email OTP.
        Unit
    }

    override suspend fun getDashboard(branchId: String, uid: String): DashboardSnapshot {
        ensureConfigured()
        // Production implementation should read only /branches/{branchId}/members/{uid}/... data.
        // See firebase/firestore.rules and docs/FIREBASE_SETUP.md.
        return MockPagasaRepository().getDashboard(branchId, uid)
    }

    override suspend fun getLoanTypes(branchId: String): List<LoanType> {
        ensureConfigured()
        return MockPagasaRepository().getLoanTypes(branchId)
    }

    override suspend fun submitLoanApplication(application: LoanApplication): Result<Unit> = runCatching {
        ensureConfigured()
        val uid = auth.currentUser?.uid ?: error("Not authenticated")
        firestore.collection("loanApplications")
            .document(application.id)
            .set(mapOf("uid" to uid, "amount" to application.amount, "termMonths" to application.termMonths, "status" to application.status.name))
            .await()
    }

    override suspend fun getPaymentSchedule(loanId: String): List<PaymentScheduleItem> {
        ensureConfigured()
        return MockPagasaRepository().getPaymentSchedule(loanId)
    }

    override suspend fun getTransactions(branchId: String, uid: String): List<TransactionRecord> {
        ensureConfigured()
        return MockPagasaRepository().getTransactions(branchId, uid)
    }

    override suspend fun getNotifications(uid: String): List<PagasaNotification> {
        ensureConfigured()
        return MockPagasaRepository().getNotifications(uid)
    }
}
