# Firebase Setup and Database Design

## Authentication

Enable:

- Email/Password for member login
- Phone Auth for SMS OTP, or Cloud Functions for email OTP
- Multi-factor authentication for staff/admin accounts

Use custom claims for access control:

```js
await admin.auth().setCustomUserClaims(uid, {
  branchId: 'BR-MNL-001',
  role: 'MEMBER'
});
```

Roles:

- `MEMBER`
- `BRANCH_STAFF`
- `ADMIN`

## Firestore Collections

### users/{uid}

```json
{
  "memberId": "PAGASA-2026-0001",
  "fullName": "Maria Santos",
  "email": "maria.santos@example.com",
  "mobileNumber": "+639170000000",
  "branchId": "BR-MNL-001",
  "branchName": "PAG-ASA Manila Branch",
  "role": "MEMBER",
  "verified": true,
  "createdAt": "serverTimestamp",
  "updatedAt": "serverTimestamp"
}
```

### branches/{branchId}/members/{uid}/loans/{loanId}

```json
{
  "typeName": "Kabuhayan Business Loan",
  "principalAmount": 50000,
  "annualInterestRate": 0.12,
  "remainingBalance": 28500,
  "totalPaid": 21500,
  "dueDate": "2026-05-30",
  "projectedPayoffDate": "2027-02-25",
  "status": "RELEASED",
  "penalties": 0,
  "createdAt": "serverTimestamp",
  "updatedAt": "serverTimestamp"
}
```

### transactions

Every balance-changing event should create an immutable transaction record. Prefer Cloud Functions or a secure backend for posting verified payments.

```json
{
  "type": "LOAN_REPAYMENT",
  "title": "Loan repayment",
  "amount": 4750,
  "reference": "OR-100212",
  "timestamp": "serverTimestamp",
  "postedBy": "staffUid",
  "receiptPath": "branches/BR-MNL-001/members/uid/receipts/OR-100212.pdf"
}
```

## Recommended Indexes

- `branches/{branchId}/loanApplications`: `status ASC, submittedAt DESC`
- `branches/{branchId}/members/{uid}/transactions`: `timestamp DESC, type ASC`
- `branches/{branchId}/members/{uid}/notifications`: `read ASC, timestamp DESC`

## Push Notifications

Use Firebase Cloud Messaging. Store device tokens in:

```text
branches/{branchId}/members/{uid}/devices/{tokenHash}
```

Send notifications for:

- Due date reminders
- Missed payments
- Approval updates
- Savings milestones
- Branch announcements
- Promotional offers, only if user opted in
