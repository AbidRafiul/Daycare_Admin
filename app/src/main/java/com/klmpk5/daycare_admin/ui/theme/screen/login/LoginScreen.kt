package com.klmpk5.daycare_admin.ui.theme.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.R
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycareDisabled
import com.klmpk5.daycare_admin.ui.theme.DaycareErrorBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareErrorText
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareSoftMint
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.viewmodel.LoginState
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    // Mengambil status login dari ViewModel
    val loginState by viewModel.loginState.collectAsState()

    // State untuk input email
    var email by remember { mutableStateOf("") }

    // State untuk input password
    var password by remember { mutableStateOf("") }

    // State untuk show/hide password
    var passwordVisible by remember { mutableStateOf(false) }

    // Jika login berhasil, pindah ke halaman berikutnya
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DaycareSoftMint,
                        DaycareBackground
                    )
                )
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo aplikasi Daykids Club
            Image(
                painter = painterResource(id = R.drawable.daykids_logo),
                contentDescription = "Logo Daykids Club",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .padding(bottom = 18.dp),
                contentScale = ContentScale.Fit
            )

            // Card utama untuk form login
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(30.dp),
                        spotColor = Color(0x22000000)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon kecil sebagai aksen di atas judul
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(
                                color = DaycarePrimaryLight,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Icon keamanan",
                            tint = DaycarePrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Judul halaman login
                    Text(
                        text = "Selamat Datang",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle untuk menjelaskan role pengguna
                    Text(
                        text = "Masuk sebagai Guru",
                        fontSize = 14.sp,
                        color = DaycareTextSecondary
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Input email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp),
                        label = { Text("Email") },
                        placeholder = { Text("admin@daycare.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Icon email",
                                tint = DaycarePrimary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DaycarePrimary,
                            unfocusedBorderColor = DaycareBorder,
                            focusedLabelColor = DaycarePrimary,
                            cursorColor = DaycarePrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp),
                        label = { Text("Password") },
                        placeholder = { Text("Masukkan password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Icon password",
                                tint = DaycarePrimary
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.Visibility
                                    } else {
                                        Icons.Default.VisibilityOff
                                    },
                                    contentDescription = "Tampilkan atau sembunyikan password",
                                    tint = DaycareTextSecondary
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Link lupa password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                // TODO: Tambahkan navigasi ke fitur lupa password
                            }
                        ) {
                            Text(
                                text = "Lupa Password?",
                                color = DaycarePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tombol login
                    Button(
                        onClick = {
                            viewModel.login(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = loginState !is LoginState.Loading,
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DaycarePrimary,
                            disabledContainerColor = DaycareDisabled
                        )
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Masuk  →",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer kecil di bawah card
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = DaycarePrimaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Icon aman",
                    tint = DaycarePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Kelola aktivitas daycare",
                fontSize = 14.sp,
                color = DaycareTextSecondary
            )

            Text(
                text = "dengan mudah dan aman",
                fontSize = 14.sp,
                color = DaycareTextMuted
            )
        }

        // Snackbar untuk menampilkan pesan error login
        if (loginState is LoginState.Error) {
            val message = (loginState as LoginState.Error).message

            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                containerColor = DaycareErrorBackground,
                contentColor = DaycareErrorText,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = message)
            }
        }
    }
}