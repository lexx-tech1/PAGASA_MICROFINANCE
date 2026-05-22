# PAG-ASA Microfinance Security Checklist

## Mobile App

- [x] Cleartext HTTP disabled
- [x] Sensitive backups excluded
- [x] EncryptedSharedPreferences scaffold for session metadata
- [x] Biometric support dependency and availability helper
- [x] ProGuard/R8 enabled for release
- [ ] Firebase App Check / Play Integrity enabled
- [ ] Root/jailbreak risk controls reviewed
- [ ] Certificate pinning considered if using custom REST APIs
- [ ] Secure logout clears cached session tokens

## Backend/Firebase

- [x] Branch-specific Firestore rules provided
- [x] Storage rules for document/receipt isolation provided
- [ ] Custom claims assignment via trusted admin backend
- [ ] Balance changes restricted to trusted backend/staff workflow
- [ ] Immutable audit logs for payments and savings postings
- [ ] Staff/admin MFA enforced
- [ ] Rate limiting for OTP and support ticket submissions
- [ ] Daily backup and recovery plan

## Compliance and Privacy

- [ ] Privacy policy and consent screens
- [ ] Data retention policy
- [ ] User support process for data correction
- [ ] Incident response procedure
- [ ] Device lost/stolen support workflow
