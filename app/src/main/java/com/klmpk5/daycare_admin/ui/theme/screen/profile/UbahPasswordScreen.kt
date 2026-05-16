package com.klmpk5.daycare_admin.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.PasswordState
import com.klmpk5.daycare_admin.viewmodel.ProfileViewModel

/**
 * ChangePasswordScreen untuk mengubah password akun Firebase Auth.
 */
@Composable
fun ChangePasswordScreen(
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val passwordState by profileViewModel.passwordState.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(passwordState) {
        when (passwordState) {
            is PasswordState.Success -> {
                message = "Password berhasil diubah"
                oldPassword = ""
                newPassword = ""
                confirmPassword = ""
                profileViewModel.resetState()
            }

            is PasswordState.Error -> {
                message = (passwordState as PasswordState.Error).message
                profileViewModel.resetState()
            }

            else -> Unit
        }
    }

    Scaffold(
        containerColor = DaycareBackground
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DaycareBackground)
                .padding(innerPadding)
        ) {
            ProfilePageHeader(
                title = "Ubah Password",
                subtitle = "Kelola keamanan akun admin/guru",
                emoji = "Key",
                onBack = onBack
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-36).dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Text(
                        text = "Keamanan Akun",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Masukkan password lama untuk verifikasi sebelum mengganti password.",
                        fontSize = 14.sp,
                        color = DaycareTextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    PasswordInputField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = "Password Lama"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PasswordInputField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = "Password Baru"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PasswordInputField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Konfirmasi Password Baru"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        color = DaycarePrimaryLight.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "💡 Password baru minimal 6 karakter. Gunakan kombinasi huruf dan angka agar lebih aman.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp,
                            color = DaycareTextSecondary,
                            lineHeight = 18.sp
                        )
                    }

                    if (!message.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = message ?: "",
                            color = if (message?.contains("berhasil", ignoreCase = true) == true) {
                                DaycarePrimary
                            } else {
                                Color(0xFFB91C1C)
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            profileViewModel.changePassword(
                                oldPassword = oldPassword,
                                newPassword = newPassword,
                                confirmPassword = confirmPassword
                            )
                        },
                        enabled = passwordState !is PasswordState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DaycarePrimary,
                            disabledContainerColor = DaycarePrimary.copy(alpha = 0.45f)
                        )
                    ) {
                        if (passwordState is PasswordState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Simpan Password",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DaycarePrimary,
            unfocusedBorderColor = DaycareBorder,
            focusedLabelColor = DaycarePrimary,
            cursorColor = DaycarePrimary
        )
    )
}
