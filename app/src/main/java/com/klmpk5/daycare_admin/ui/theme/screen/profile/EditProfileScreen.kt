package com.klmpk5.daycare_admin.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.ProfileSaveState
import com.klmpk5.daycare_admin.viewmodel.ProfileViewModel

/**
 * Form profil hanya berisi nama, email, dan keterangan.
 */
@Composable
fun EditProfileScreen(
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val profileState by profileViewModel.profileState.collectAsState()
    val saveState by profileViewModel.profileSaveState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var formInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.resetProfileSaveState()
        profileViewModel.loadProfile()
    }

    LaunchedEffect(
        profileState.fullName,
        profileState.email,
        profileState.description,
        profileState.isLoading
    ) {
        if (!formInitialized && !profileState.isLoading) {
            fullName = profileState.fullName
            userEmail = profileState.email
            description = profileState.description
            formInitialized = true
        }
    }

    LaunchedEffect(saveState) {
        when (saveState) {
            is ProfileSaveState.Success -> {
                message = (saveState as ProfileSaveState.Success).message
                profileViewModel.resetProfileSaveState()
            }

            is ProfileSaveState.Error -> {
                message = (saveState as ProfileSaveState.Error).message
                profileViewModel.resetProfileSaveState()
            }

            else -> Unit
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                emoji = "Edit",
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
                        text = "Data Profil",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Isi hanya nama, email, dan keterangan admin.",
                        fontSize = 14.sp,
                        color = DaycareTextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    ProfileTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            message = null
                        },
                        label = "Nama",
                        placeholder = "Masukkan nama"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileTextField(
                        value = userEmail,
                        onValueChange = {
                            userEmail = it
                            message = null
                        },
                        label = "Email",
                        placeholder = "admin@email.com",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            message = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        label = {
                            Text("Keterangan")
                        },
                        placeholder = {
                            Text("Tuliskan keterangan singkat...")
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
                            text = message.orEmpty(),
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
                            profileViewModel.updateProfile(
                                fullName = fullName,
                                email = userEmail,
                                description = description
                            )
                        },
                        enabled = saveState !is ProfileSaveState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DaycarePrimary,
                            disabledContainerColor = DaycarePrimary.copy(alpha = 0.45f)
                        )
                    ) {
                        if (saveState is ProfileSaveState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Simpan Profil",
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
                    text = "<",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = (-12).dp)
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
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
