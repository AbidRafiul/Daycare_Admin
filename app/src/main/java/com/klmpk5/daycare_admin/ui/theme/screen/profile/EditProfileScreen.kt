package com.klmpk5.daycare_admin.ui.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary

/**
 * EditProfileScreen adalah halaman edit profil admin/guru.
 *
 * Untuk sekarang:
 * - data email diambil dari FirebaseAuth.currentUser
 * - tombol simpan masih UI state lokal
 *
 * Nanti bisa disambungkan ke Firestore users.
 */
@Composable
fun EditProfileScreen(
    onBack: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val email = currentUser?.email ?: ""

    var fullName by remember { mutableStateOf("Admin Daycare") }
    var userEmail by remember { mutableStateOf(email) }
    var phoneNumber by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Admin / Guru") }
    var bio by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

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
                title = "Edit Profil",
                subtitle = "Ubah informasi akun admin/guru",
                emoji = "✏️",
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
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .background(
                                color = DaycarePrimaryLight,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👩‍🏫",
                            fontSize = 46.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = DaycarePrimaryLight.copy(alpha = 0.60f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = 1.dp,
                            color = DaycarePrimary.copy(alpha = 0.15f)
                        )
                    ) {
                        Text(
                            text = "Ubah Foto",
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            color = DaycarePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Nama Lengkap",
                        placeholder = "Masukkan nama lengkap"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        label = "Email",
                        placeholder = "admin@email.com",
                        keyboardType = KeyboardType.Email,
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = "No. WhatsApp",
                        placeholder = "0812-3456-7890",
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = "Peran",
                        placeholder = "Admin / Guru",
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        label = {
                            Text("Tentang Saya")
                        },
                        placeholder = {
                            Text("Tuliskan deskripsi singkat...")
                        },
                        maxLines = 4,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DaycarePrimary,
                            unfocusedBorderColor = DaycareBorder,
                            focusedLabelColor = DaycarePrimary,
                            cursorColor = DaycarePrimary
                        )
                    )

                    if (!message.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = message ?: "",
                            color = DaycarePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            message = "Profil berhasil diperbarui"
                            /**
                             * TODO:
                             * Nanti sambungkan ke Firestore collection users.
                             *
                             * Contoh konsep:
                             * profileViewModel.updateProfile(
                             *     fullName = fullName,
                             *     phoneNumber = phoneNumber,
                             *     bio = bio
                             * )
                             */
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DaycarePrimary
                        )
                    ) {
                        Text(
                            text = "Simpan Perubahan",
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

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        placeholder = {
            Text(placeholder)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DaycarePrimary,
            unfocusedBorderColor = DaycareBorder,
            focusedLabelColor = DaycarePrimary,
            cursorColor = DaycarePrimary,
            disabledBorderColor = DaycareBorder.copy(alpha = 0.70f),
            disabledTextColor = DaycareTextSecondary,
            disabledLabelColor = DaycareTextSecondary
        )
    )
}

/**
 * Header reusable untuk Edit Profile dan Change Password.
 */
@Composable
fun ProfilePageHeader(
    title: String,
    subtitle: String,
    emoji: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DaycarePrimary,
                        Color(0xFF23897D)
                    )
                )
            )
            .padding(horizontal = 22.dp)
            .statusBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp)
                .size(42.dp),
            onClick = onBack,
            color = Color.White.copy(alpha = 0.16f),
            shape = CircleShape
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 22.dp)
                .size(44.dp)
                .background(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 22.sp
            )
        }
    }
}