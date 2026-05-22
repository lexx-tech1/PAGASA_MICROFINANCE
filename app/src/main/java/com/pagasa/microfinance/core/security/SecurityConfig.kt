package com.pagasa.microfinance.core.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurityConfig {
    const val SESSION_TIMEOUT_MINUTES = 15L
    const val MIN_PASSWORD_LENGTH = 8

    fun biometricAvailable(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        val result = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun secureSessionPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        "secure_session.xml",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun strongPasswordHint(): String =
        "Use at least 8 characters with letters, numbers, and a symbol. Never share your OTP."
}
