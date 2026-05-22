# PAG-ASA Microfinance Android Application

A modern Android Studio starter project for **PAG-ASA Microfinance Application** built with **Kotlin**, **Jetpack Compose**, **Material Design 3**, and a Firebase-ready architecture.

The project includes secure authentication UI, member dashboard, loan management, savings, transaction history, notifications, profile/support, branch staff preview, offline cache scaffolding, Firebase security rules, and production integration notes.

## Tech Stack

- Kotlin + Jetpack Compose
- Material Design 3 with light/dark mode
- Navigation Compose
- Firebase Auth, Firestore, Storage, Cloud Messaging, Analytics
- Room offline cache scaffold
- AndroidX Security Crypto for encrypted session preferences
- AndroidX Biometric support
- WorkManager-ready sync architecture

## Project Status

This is a professional starter project/prototype with mock member data so it can be opened and reviewed immediately. Live PAG-ASA branch database integration requires Firebase configuration and production mapping.

### Implemented starter screens

- Login with OTP flow placeholder for registered members
- Biometric-login readiness message
- Home dashboard with active loan, repayment progress, savings balance, alerts, insights, and recent transactions
- Loan offers, application form, calculator, requirements, document picker, approval-status placeholder
- Loan detail screen with schedule and repayment trend chart
- Savings dashboard with growth chart and contribution records
- Transaction history with search/filter and PDF receipt placeholder
- Notifications
- Profile/settings/preferences
- Help, FAQ, support, feedback
- Branch staff/admin preview screen

## How to Open in Android Studio

1. Open Android Studio.
2. Choose **File → Open**.
3. Select this folder: `PAGASA-Microfinance-Android`.
4. Allow Gradle sync to finish.
5. Run the `app` configuration on an emulator or Android device.

The app uses mock data by default in `MainActivity.kt`:

```kotlin
PagasaApp(repository = MockPagasaRepository())
```

## Firebase Setup

The project is Firebase-ready, but intentionally excludes secrets.

1. Create a Firebase project.
2. Add Android app package:
   - Debug: `com.pagasa.microfinance.debug`
   - Release: `com.pagasa.microfinance`
3. Download `google-services.json`.
4. Place it at:

```text
app/google-services.json
```

5. Enable Firebase products:
   - Authentication: Email/Password and Phone provider if using SMS OTP
   - Firestore
   - Storage
   - Cloud Messaging
   - Analytics
6. Deploy rules:

```bash
firebase deploy --only firestore:rules,storage:rules
```

Rules are located in:

- `firebase/firestore.rules`
- `firebase/storage.rules`

## Production Integration Notes

### Branch-specific privacy model

Recommended Firestore structure:

```text
users/{uid}
branches/{branchId}
branches/{branchId}/members/{uid}
branches/{branchId}/members/{uid}/loans/{loanId}
branches/{branchId}/members/{uid}/savings/{accountId}
branches/{branchId}/members/{uid}/transactions/{transactionId}
branches/{branchId}/members/{uid}/notifications/{notificationId}
branches/{branchId}/loanTypes/{loanTypeId}
branches/{branchId}/loanApplications/{applicationId}
branches/{branchId}/supportTickets/{ticketId}
```

For strict segregation, set Firebase Auth custom claims after staff verification:

```json
{
  "branchId": "BR-MNL-001",
  "role": "MEMBER"
}
```

Staff claims should use:

```json
{
  "branchId": "BR-MNL-001",
  "role": "BRANCH_STAFF"
}
```

### Security checklist

- Never store passwords locally.
- Use Firebase Auth or secure backend tokens.
- Require OTP/MFA for sensitive logins and account changes.
- Enforce branch access using Firestore/Storage rules and custom claims.
- Do not allow members to write balances, loan status, or transaction amounts directly.
- Use encrypted local preferences for session metadata.
- Disable Android backups for sensitive files; already configured in XML.
- Use HTTPS only; cleartext traffic is disabled.
- Sign release builds with a protected keystore.
- Enable Play Integrity/App Check before production release.

## Offline Mode

Room entities and DAO are included as a scaffold for cached account and transaction data:

- `CachedAccountEntity`
- `CachedTransactionEntity`
- `SyncQueueEntity`

Recommended behavior:

1. Cache previously loaded account summary and transaction history.
2. Allow offline viewing only for sensitive financial records.
3. Queue low-risk actions such as feedback drafts.
4. Never allow offline balance-changing transactions without server verification.
5. Sync automatically using WorkManager when internet is restored.

## Next Development Steps

1. Connect `FirebasePagasaRepository` to real Firestore paths.
2. Implement Firebase Phone Auth or email OTP Cloud Function.
3. Upload loan documents to Firebase Storage under branch/member paths.
4. Generate PDF receipts using Android `PdfDocument` or server-generated PDFs.
5. Add App Check / Play Integrity.
6. Add admin approval workflows.
7. Add unit/UI tests for auth, branch access, and financial computations.
8. Prepare privacy policy and compliance review before production use.

## Demo Credentials

The mock repository accepts any valid-looking email/member ID and a password with at least 6 characters. The login screen includes:

- Email: `maria.santos@example.com`
- Password: `pagasa123`

OTP is a demo placeholder; enter any 6 digits after pressing **Send OTP**.
