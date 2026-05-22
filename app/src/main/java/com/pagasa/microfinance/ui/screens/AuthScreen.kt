package com.pagasa.microfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pagasa.microfinance.data.model.MemberProfile
import com.pagasa.microfinance.data.repository.PagasaRepository
import com.pagasa.microfinance.ui.theme.PagasaBlue
import com.pagasa.microfinance.ui.theme.PagasaGreen
import com.pagasa.microfinance.ui.theme.PagasaMuted
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    repository: PagasaRepository,
    onAuthenticated: (MemberProfile) -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("maria.santos@example.com") }
    var password by remember { mutableStateOf("pagasa123") }
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.size(86.dp).clip(CircleShape).background(PagasaBlue),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(18.dp))
            Text("PAG-ASA Microfinance", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Secure member access", color = PagasaMuted)
            Spacer(Modifier.height(24.dp))

            ElevatedCard(shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Login", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "For registered PAG-ASA Microfinance members only.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PagasaMuted
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email or Member ID") },
                        leadingIcon = { Icon(Icons.Outlined.Mail, contentDescription = null) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (otpSent) {
                        OutlinedTextField(
                            value = otp,
                            onValueChange = { otp = it.take(6) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("One-time PIN") },
                            singleLine = true,
                            supportingText = { Text("Demo OTP: enter any 6 digits") }
                        )
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        onClick = {
                            scope.launch {
                                loading = true
                                error = null
                                if (!otpSent) {
                                    repository.requestOtp(email)
                                    otpSent = true
                                    loading = false
                                } else {
                                    val result = repository.login(email, password)
                                    loading = false
                                    result.onSuccess(onAuthenticated).onFailure { error = it.message }
                                }
                            }
                        }
                    ) {
                        Text(
                            when {
                                loading -> "Please wait…"
                                !otpSent -> "Send OTP"
                                else -> "Login securely"
                            }
                        )
                    }
                    OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { onForgotPassword() }) {
                        Text("Forgot password")
                    }
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            scope.launch {
                                loading = true
                                repository.login("demo@pagasa.local", "pagasa123")
                                    .onSuccess(onAuthenticated)
                                    .onFailure { error = it.message }
                                loading = false
                            }
                        }
                    ) { Text("Use demo account") }
                }
            }

            Spacer(Modifier.height(18.dp))
            AssistChip(
                onClick = { },
                label = { Text("Biometric login ready after first successful login") },
                leadingIcon = { Icon(Icons.Outlined.Fingerprint, contentDescription = null, tint = PagasaGreen) }
            )
            Spacer(Modifier.height(8.dp))
            Text("Encrypted communication • Branch-restricted records • Offline cache", style = MaterialTheme.typography.bodySmall, color = PagasaMuted)
        }
    }
}
