package com.klmpk5.daycare_admin.ui.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.ProfileViewModel

/**
 * ProfileScreen adalah halaman profile admin/guru.
 *
 * Isi utama:
 * - informasi akun
 * - menu Edit Profil
 * - menu Ubah Password
 * - tombol Logout
 */
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val profileState by profileViewModel.profileState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text(
                    text = "Yakin mau logout?",
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary
                )
            },
            text = {
                Text(
                    text = "Kamu akan keluar dari akun admin daycare.",
                    color = DaycareTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text(
                        text = "Logout",
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        text = "Batal",
                        color = DaycarePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
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
            ProfileHeader(
                onLogoutClick = {
                    showLogoutDialog = true
                }
            )

            ProfileInfoCard(
                name = profileState.fullName,
                role = profileState.role,
                email = profileState.email.ifBlank { "admin@daycare.com" },
                description = profileState.description,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-36).dp)
            )

            ProfileMenuCard(
                onEditProfileClick = onEditProfileClick,
                onChangePasswordClick = onChangePasswordClick,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-20).dp)
            )

            LogoutCard(
                onLogoutClick = {
                    showLogoutDialog = true
                },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-4).dp)
            )
        }
    }
}

@Composable
fun ProfileHeader(
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
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
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopEnd)
                .offset(x = 38.dp, y = 18.dp)
                .background(
                    color = Color.White.copy(alpha = 0.10f),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = (-22).dp)
        ) {
            Text(
                text = "Profile Admin",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kelola akun dan keamanan aplikasi",
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp)
                .size(44.dp)
                .clickable(onClick = onLogoutClick)
                .background(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Logout,
                contentDescription = "Logout",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun ProfileInfoCard(
    name: String,
    role: String,
    email: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .background(
                            color = DaycarePrimaryLight,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👩‍🏫",
                        fontSize = 38.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = role,
                        fontSize = 14.sp,
                        color = DaycarePrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = email,
                        fontSize = 13.sp,
                        color = DaycareTextSecondary
                    )
                }
            }

            if (description.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = DaycareTextSecondary,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                color = DaycarePrimaryLight.copy(alpha = 0.55f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Akun Anda aman",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DaycareTextPrimary
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Gunakan password yang kuat untuk menjaga akun.",
                            fontSize = 13.sp,
                            color = DaycareTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenuCard(
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(
                text = "Menu Pengaturan",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            ProfileMenuItem(
                emoji = "✏️",
                title = "Edit Profil",
                subtitle = "Ubah informasi profil Anda",
                onClick = onEditProfileClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = DaycareBorder.copy(alpha = 0.45f)
            )

            ProfileMenuItem(
                emoji = "🔐",
                title = "Ubah Password",
                subtitle = "Ganti password akun Anda",
                onClick = onChangePasswordClick
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = DaycarePrimaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )
            }

            Text(
                text = "›",
                fontSize = 28.sp,
                color = DaycareTextMuted
            )
        }
    }
}

@Composable
fun LogoutCard(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onLogoutClick,
        color = Color(0xFFFFE5E5),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFFFB4B4)
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.75f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🚪",
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Logout",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB91C1C)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Keluar dari akun admin",
                    fontSize = 13.sp,
                    color = Color(0xFFB91C1C).copy(alpha = 0.80f)
                )
            }
        }
    }
}
