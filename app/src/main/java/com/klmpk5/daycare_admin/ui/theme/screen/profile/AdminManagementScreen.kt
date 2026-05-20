package com.klmpk5.daycare_admin.ui.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_admin.data.remote.model.UserRemoteDto
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.AdminManagementViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminManagementPage(
    adminManagementViewModel: AdminManagementViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = DaycareBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DaycareBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                AdminPageHeader(
                    title = "Master Admin",
                    subtitle = "Tambah dan nonaktifkan akun admin",
                    onBack = onBack
                )
            }

            item {
                AdminManagementScreen(
                    adminManagementViewModel = adminManagementViewModel,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .offset(y = (-34).dp)
                )
            }
        }
    }
}

@Composable
private fun AdminPageHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(164.dp)
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
                .padding(top = 8.dp)
                .size(42.dp),
            onClick = onBack,
            color = Color.White.copy(alpha = 0.16f),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
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
                .padding(start = 58.dp, end = 4.dp)
                .offset(y = (-18).dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 27.sp,
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
    }
}

@Composable
fun AdminManagementScreen(
    adminManagementViewModel: AdminManagementViewModel,
    modifier: Modifier = Modifier
) {
    val state by adminManagementViewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var adminToDisable by remember { mutableStateOf<UserRemoteDto?>(null) }
    var adminToReactivate by remember { mutableStateOf<UserRemoteDto?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "Master Data Admin",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${state.admins.size} akun admin terdaftar",
                fontSize = 13.sp,
                color = DaycareTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            AdminCreateForm(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                fullName = fullName,
                onFullNameChange = { fullName = it },
                description = description,
                onDescriptionChange = { description = it },
                isLoading = state.isCreating,
                onSubmit = {
                    adminManagementViewModel.registerNewAdmin(
                        email = email,
                        pass = password,
                        fullName = fullName,
                        description = description
                    )
                    if (email.isNotBlank() && password.length >= 6) {
                        email = ""
                        password = ""
                        fullName = ""
                        description = ""
                    }
                }
            )

            if (state.message != null || state.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.message ?: state.errorMessage.orEmpty(),
                    color = if (state.message != null) DaycarePrimary else Color(0xFFB91C1C),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Akun",
                    modifier = Modifier.weight(1f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary
                )

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = DaycarePrimary,
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.admins.isEmpty() && !state.isLoading) {
                EmptyAdminBox()
            } else {
                state.admins.forEachIndexed { index, admin ->
                    AdminAccountItem(
                        admin = admin,
                        isCurrentUser = admin.uid == FirebaseAuth.getInstance().currentUser?.uid,
                        isProcessing = state.disablingAdminUid == admin.uid,
                        isReactivating = state.reactivatingAdminUid == admin.uid,
                        onDisableClick = { adminToDisable = admin },
                        onReactivateClick = { adminToReactivate = admin }
                    )

                    if (index != state.admins.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = DaycareBorder.copy(alpha = 0.45f)
                        )
                    }
                }
            }
        }
    }

    adminToDisable?.let { admin ->
        AlertDialog(
            onDismissRequest = { adminToDisable = null },
            title = { Text("Nonaktifkan Admin?") },
            text = {
                Text(
                    text = "Akun ${admin.email} tidak bisa login lagi setelah dinonaktifkan. Keterangan penonaktif akan disimpan memakai email admin yang sedang login."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        adminManagementViewModel.deactivateAdmin(admin)
                        adminToDisable = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
                ) {
                    Text("Nonaktifkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { adminToDisable = null }) {
                    Text("Batal", color = DaycarePrimary)
                }
            }
        )
    }

    adminToReactivate?.let { admin ->
        AlertDialog(
            onDismissRequest = { adminToReactivate = null },
            title = { Text("Aktifkan Admin?") },
            text = {
                Text(
                    text = "Akun ${admin.email} akan bisa login kembali setelah diaktifkan."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        adminManagementViewModel.reactivateAdmin(admin)
                        adminToReactivate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DaycarePrimary)
                ) {
                    Text("Aktifkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { adminToReactivate = null }) {
                    Text("Batal", color = DaycarePrimary)
                }
            }
        )
    }
}

@Composable
private fun AdminCreateForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Surface(
        color = DaycarePrimaryLight.copy(alpha = 0.55f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, DaycarePrimary.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tambah Akun Admin",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            AdminTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "Nama",
                placeholder = "Nama admin"
            )

            Spacer(modifier = Modifier.height(10.dp))

            AdminTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "admin@email.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(10.dp))

            AdminTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "Minimal 6 karakter",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            AdminTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = "Keterangan",
                placeholder = "Contoh: Admin kelas pagi"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSubmit,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DaycarePrimary)
            ) {
                Text(
                    text = if (isLoading) "Menyimpan..." else "Tambah Admin",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminAccountItem(
    admin: UserRemoteDto,
    isCurrentUser: Boolean,
    isProcessing: Boolean,
    isReactivating: Boolean,
    onDisableClick: () -> Unit,
    onReactivateClick: () -> Unit
) {
    val isActive = admin.isAdminActive()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    color = if (isActive) DaycarePrimaryLight else Color(0xFFFFE5E5),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = admin.email.firstOrNull()?.uppercase().orEmpty(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) DaycarePrimary else Color(0xFFB91C1C)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = admin.fullName.ifBlank { "Admin Daycare" },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = admin.email,
                fontSize = 12.sp,
                color = DaycareTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (admin.createdByEmail.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Ditambahkan oleh ${admin.createdByEmail}",
                    fontSize = 12.sp,
                    color = DaycareTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!isActive && admin.disabledByEmail.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Dinonaktifkan oleh ${admin.disabledByEmail}${formatAdminDisabledAt(admin.disabledAt)}",
                    fontSize = 12.sp,
                    color = Color(0xFFB91C1C),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = if (isActive) onDisableClick else onReactivateClick,
                enabled = !isCurrentUser && !isProcessing && !isReactivating,
                shape = RoundedCornerShape(50),
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        isCurrentUser -> DaycareBorder
                        isActive -> Color(0xFFB91C1C)
                        else -> DaycarePrimary
                    }
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when {
                        isCurrentUser -> "Akun Ini"
                        isProcessing -> "Proses..."
                        isReactivating -> "Proses..."
                        isActive -> "Nonaktifkan"
                        else -> "Aktifkan"
                    },
                    color = when {
                        isCurrentUser -> DaycareTextMuted
                        isActive -> Color(0xFFB91C1C)
                        else -> DaycarePrimary
                    },
                    fontSize = 12.sp
                )
            }
        }

        Surface(
            color = if (isActive) DaycarePrimaryLight else Color(0xFFFFE5E5),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = if (isActive) "Aktif" else "Nonaktif",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) DaycarePrimary else Color(0xFFB91C1C)
            )
        }
    }
}

@Composable
private fun EmptyAdminBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DaycarePrimaryLight.copy(alpha = 0.55f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Belum ada data admin",
            color = DaycareTextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun AdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DaycarePrimary,
            unfocusedBorderColor = DaycareBorder,
            focusedLabelColor = DaycarePrimary,
            cursorColor = DaycarePrimary
        )
    )
}

private fun formatAdminDisabledAt(value: Long?): String {
    if (value == null) return ""
    return try {
        val date = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(value))
        " pada $date"
    } catch (e: Exception) {
        ""
    }
}

private fun UserRemoteDto.isAdminActive(): Boolean {
    val normalizedStatus = status.lowercase()
    return isActive && normalizedStatus != "inactive" && normalizedStatus != "nonaktif"
}
