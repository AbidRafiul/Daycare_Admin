package com.klmpk5.daycare_admin.ui.theme.screen.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import androidx.compose.foundation.BorderStroke
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
import com.klmpk5.daycare_admin.ui.theme.*
import com.klmpk5.daycare_admin.viewmodel.LoginState
import com.klmpk5.daycare_admin.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // FITUR BARU: Variabel untuk pop-up Lupa Password
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    val resetState by viewModel.resetPasswordState.collectAsState()

    val context = LocalContext.current
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("911994232553-b2qpkp9rrglm1rdsahaii78321e540bk.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.loginWithGoogle(idToken)
            } else {
                Toast.makeText(context, "Google ID Token kosong", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            if (e.statusCode != com.google.android.gms.common.api.CommonStatusCodes.CANCELED) {
                Toast.makeText(context, "Error Code: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

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
            Image(
                painter = painterResource(id = R.drawable.daykids_logo),
                contentDescription = "Logo Daykids Club",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .padding(bottom = 18.dp),
                contentScale = ContentScale.Fit
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(30.dp),
                        spotColor = Color(0x22000000)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(DaycarePrimaryLight, CircleShape),
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
                    Text(text = "Selamat Datang", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DaycareTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Masuk sebagai Guru", fontSize = 14.sp, color = DaycareTextSecondary)
                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth().height(62.dp),
                        label = { Text("Email") },
                        placeholder = { Text("admin@daycare.com") },
                        leadingIcon = { Icon(Icons.Default.Email, "Icon email", tint = DaycarePrimary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DaycarePrimary,
                            unfocusedBorderColor = DaycareBorder,
                            focusedLabelColor = DaycarePrimary,
                            cursorColor = DaycarePrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth().height(62.dp),
                        label = { Text("Password") },
                        placeholder = { Text("Masukkan password") },
                        leadingIcon = { Icon(Icons.Default.Lock, "Icon password", tint = DaycarePrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Tampilkan atau sembunyikan password",
                                    tint = DaycareTextSecondary
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DaycarePrimary,
                            unfocusedBorderColor = DaycareBorder,
                            focusedLabelColor = DaycarePrimary,
                            cursorColor = DaycarePrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        // FITUR BARU: Tombol Lupa Password sekarang memunculkan Dialog
                        TextButton(onClick = { showResetDialog = true }) {
                            Text("Lupa Password?", color = DaycarePrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = loginState !is LoginState.Loading,
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DaycarePrimary, disabledContainerColor = DaycareDisabled)
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text("Masuk  →", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "────────  Atau  ────────", color = DaycareTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = loginState !is LoginState.Loading,
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, DaycareBorder),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🌐 ", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Masuk dengan Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DaycareTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier.size(44.dp).background(DaycarePrimaryLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Shield, "Icon aman", tint = DaycarePrimary, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Kelola aktivitas daycare", fontSize = 14.sp, color = DaycareTextSecondary)
            Text("dengan mudah dan aman", fontSize = 14.sp, color = DaycareTextMuted)
        }

        // FITUR BARU: Pop-up Dialog untuk Lupa Password
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = {
                    showResetDialog = false
                    viewModel.clearResetState()
                },
                title = {
                    Text(text = "Reset Password", fontWeight = FontWeight.Bold, color = DaycareTextPrimary)
                },
                text = {
                    Column {
                        Text(text = "Masukkan email akun Anda. Kami akan mengirimkan tautan untuk mengatur ulang password.", fontSize = 14.sp, color = DaycareTextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Email") },
                            placeholder = { Text("admin@daycare.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DaycarePrimary,
                                cursorColor = DaycarePrimary
                            )
                        )
                        if (resetState is LoginState.Error) {
                            Text(
                                text = (resetState as LoginState.Error).message,
                                color = DaycareErrorText,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.resetPassword(resetEmail) },
                        colors = ButtonDefaults.buttonColors(containerColor = DaycarePrimary),
                        enabled = resetState !is LoginState.Loading
                    ) {
                        if (resetState is LoginState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Kirim", color = Color.White)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showResetDialog = false
                            viewModel.clearResetState()
                        }
                    ) {
                        Text("Batal", color = DaycarePrimary)
                    }
                },
                containerColor = Color.White
            )
        }

        // FITUR BARU: Eksekusi Toast ketika berhasil kirim email reset
        LaunchedEffect(resetState) {
            if (resetState is LoginState.Success) {
                Toast.makeText(context, "Link reset password telah dikirim! Cek inbox/spam email Anda.", Toast.LENGTH_LONG).show()
                showResetDialog = false
                resetEmail = "" // Kosongkan form email
                viewModel.clearResetState()
            }
        }

        if (loginState is LoginState.Error) {
            val message = (loginState as LoginState.Error).message
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
                containerColor = DaycareErrorBackground,
                contentColor = DaycareErrorText,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = message)
            }
        }
    }
}