package com.klmpk5.daycare_admin.ui.theme.screen.login.classroom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import java.util.UUID

/**
 * ClassroomScreen adalah halaman utama untuk fitur kelas.
 *
 * Di halaman ini guru/admin bisa mengakses:
 * - Master Data Anak
 * - Presensi
 * - Weekly Plan
 * - Upload Gambar Aktivitas
 *
 * Untuk sekarang UI masih static.
 * Nanti bisa dihubungkan ke ViewModel, Room, dan Firebase.
 */
@Composable
fun ClassroomScreen(
    adminChildViewModel: AdminChildViewModel,
    attendanceViewModel: AttendanceViewModel

) {
    var selectedMenu by remember { mutableStateOf(ClassroomMenu.MASTER_DATA) }
    var selectedChildForEdit by remember { mutableStateOf<Child?>(null) }

    Scaffold(
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
                ClassroomHeader()
            }

            item {
                ClassroomMenuGrid(
                    selectedMenu = selectedMenu,
                    onMenuClick = { selectedMenu = it },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .offset(y = (-34).dp)
                )
            }

            item {
                when (selectedMenu) {
                    ClassroomMenu.MASTER_DATA -> {
                        MasterDataChildForm(
                            adminChildViewModel = adminChildViewModel,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-34).dp)
                        )
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-34).dp)
                        ) {
                            MasterDataChildForm(
                                adminChildViewModel = adminChildViewModel,
                                selectedChild = selectedChildForEdit,
                                onFinishEdit = {
                                    selectedChildForEdit = null
                                }
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            ChildListSection(
                                adminChildViewModel = adminChildViewModel,
                                onEditClick = { child ->
                                    selectedChildForEdit = child
                                }
                            )
                        }
                    }

                    ClassroomMenu.ATTENDANCE -> {
                        AttendanceScreen(
                            adminChildViewModel = adminChildViewModel,
                            attendanceViewModel = attendanceViewModel
                        )

                    }

                    ClassroomMenu.WEEKLY_PLAN -> {
                        ClassroomComingSoonCard(
                            title = "Weekly Plan",
                            description = "Buat dan atur rencana kegiatan mingguan sesuai hari masuk daycare.",
                            emoji = "📅",
                            buttonText = "Kelola Weekly Plan",
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-34).dp)
                        )
                    }

                    ClassroomMenu.ACTIVITY_UPLOAD -> {
                        ClassroomComingSoonCard(
                            title = "Upload Aktivitas",
                            description = "Upload foto kegiatan anak untuk dokumentasi harian.",
                            emoji = "📷",
                            buttonText = "Upload Foto",
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-34).dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Header halaman Classroom.
 */
@Composable
fun ClassroomHeader() {
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
        // Dekorasi lingkaran transparan
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 35.dp, y = 20.dp)
                .background(
                    color = Color.White.copy(alpha = 0.10f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-25).dp, y = (-8).dp)
                .background(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = "Classroom",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kelola data anak, presensi, weekly plan, dan aktivitas kelas",
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        // Icon notifikasi
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
                text = "🔔",
                fontSize = 22.sp
            )

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 7.dp)
                    .background(
                        color = Color(0xFFFF5A5F),
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Grid menu Classroom.
 *
 * Menu ini digunakan untuk memilih fitur yang ingin dibuka.
 */
@Composable
fun ClassroomMenuGrid(
    selectedMenu: ClassroomMenu,
    onMenuClick: (ClassroomMenu) -> Unit,
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
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Menu Classroom",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pilih fitur yang ingin dikelola",
                fontSize = 13.sp,
                color = DaycareTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ClassroomMenuItem(
                    modifier = Modifier.weight(1f),
                    emoji = "👧",
                    title = "Master Data",
                    description = "Data anak",
                    selected = selectedMenu == ClassroomMenu.MASTER_DATA,
                    onClick = { onMenuClick(ClassroomMenu.MASTER_DATA) }
                )

                ClassroomMenuItem(
                    modifier = Modifier.weight(1f),
                    emoji = "✅",
                    title = "Presensi",
                    description = "Kehadiran",
                    selected = selectedMenu == ClassroomMenu.ATTENDANCE,
                    onClick = { onMenuClick(ClassroomMenu.ATTENDANCE) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ClassroomMenuItem(
                    modifier = Modifier.weight(1f),
                    emoji = "📅",
                    title = "Weekly Plan",
                    description = "Jadwal",
                    selected = selectedMenu == ClassroomMenu.WEEKLY_PLAN,
                    onClick = { onMenuClick(ClassroomMenu.WEEKLY_PLAN) }
                )

                ClassroomMenuItem(
                    modifier = Modifier.weight(1f),
                    emoji = "📷",
                    title = "Upload",
                    description = "Aktivitas",
                    selected = selectedMenu == ClassroomMenu.ACTIVITY_UPLOAD,
                    onClick = { onMenuClick(ClassroomMenu.ACTIVITY_UPLOAD) }
                )
            }
        }
    }
}

/**
 * Card kecil untuk setiap menu di Classroom.
 */
@Composable
fun ClassroomMenuItem(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(116.dp),
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = if (selected) DaycarePrimary else DaycarePrimaryLight.copy(alpha = 0.55f),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) DaycarePrimary else DaycarePrimary.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else DaycareTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = if (selected) Color.White.copy(alpha = 0.88f) else DaycareTextSecondary
            )
        }
    }
}

/**
 * Form Master Data Anak.
 *
 * Field disesuaikan dengan entity Child:
 *
 * childId      -> nanti bisa dibuat otomatis UUID
 * childIdRemote-> nanti dari Firebase jika sinkronisasi
 * fullName
 * nickName
 * birthDate
 * gender
 * parentUserId
 * parentEmail
 * photoUrl

 */
@Composable
fun MasterDataChildForm(
    adminChildViewModel: AdminChildViewModel,
    modifier: Modifier = Modifier,
    selectedChild: Child? = null,
    onFinishEdit: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Laki-laki") }
    var parentUserId by remember { mutableStateOf("") }
    var parentEmail by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedChild) {
        if (selectedChild != null) {
            fullName = selectedChild.fullName
            nickName = selectedChild.nickName ?: ""
            birthDate = selectedChild.birthDate
            gender = selectedChild.gender
            parentUserId = selectedChild.parentUserId ?: ""
            parentEmail = selectedChild.parentEmail ?: ""
            photoUrl = selectedChild.photoUrl ?: ""
            isActive = selectedChild.isActive
            message = "Mode edit data anak"
        }
    }

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
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = DaycarePrimaryLight,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👧",
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Master Data Anak",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Tambah atau kelola data anak daycare",
                        fontSize = 13.sp,
                        color = DaycareTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ClassroomTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Nama Lengkap",
                placeholder = "Contoh: Aisyah Putri Rahma"
            )

            Spacer(modifier = Modifier.height(14.dp))

            ClassroomTextField(
                value = nickName,
                onValueChange = { nickName = it },
                label = "Nama Panggilan",
                placeholder = "Contoh: Aisyah"
            )

            Spacer(modifier = Modifier.height(14.dp))

            ClassroomTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = "Tanggal Lahir",
                placeholder = "YYYY-MM-DD",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Jenis Kelamin",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GenderChip(
                    text = "Laki-laki",
                    selected = gender == "Laki-laki",
                    onClick = { gender = "Laki-laki" }
                )

                GenderChip(
                    text = "Perempuan",
                    selected = gender == "Perempuan",
                    onClick = { gender = "Perempuan" }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            ClassroomTextField(
                value = parentUserId,
                onValueChange = { parentUserId = it },
                label = "Parent User ID",
                placeholder = "Optional, isi jika sudah ada akun orang tua"
            )

            Spacer(modifier = Modifier.height(14.dp))

            ClassroomTextField(
                value = parentEmail,
                onValueChange = { parentEmail = it },
                label = "Email Orang Tua",
                placeholder = "parent@email.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            ClassroomTextField(
                value = photoUrl,
                onValueChange = { photoUrl = it },
                label = "Photo URL",
                placeholder = "Optional, URL foto anak"
            )

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                color = DaycarePrimaryLight.copy(alpha = 0.55f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = DaycarePrimary.copy(alpha = 0.12f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Status Anak",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DaycareTextPrimary
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (isActive) "Aktif mengikuti daycare" else "Tidak aktif",
                            fontSize = 13.sp,
                            color = DaycareTextSecondary
                        )
                    }

                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DaycarePrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = DaycareTextMuted
                        )
                    )
                }
            }

            if (message != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = message ?: "",
                    color = if (message == "Data anak berhasil disimpan") {
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
                    if (fullName.isBlank()) {
                        message = "Nama lengkap tidak boleh kosong"
                        return@Button
                    }

                    if (birthDate.isBlank()) {
                        message = "Tanggal lahir tidak boleh kosong"
                        return@Button
                    }

                    val now = System.currentTimeMillis()

                    val child = Child(
                        childId = UUID.randomUUID().toString(),
                        childIdRemote = null,
                        fullName = fullName.trim(),
                        nickName = nickName.ifBlank { null },
                        birthDate = birthDate.trim(),
                        gender = gender,
                        parentUserId = parentUserId.ifBlank { null },
                        parentEmail = parentEmail.ifBlank { null },
                        photoUrl = photoUrl.ifBlank { null },
                        isActive = isActive,
                        createdAt = now,
                        updatedAt = now
                    )

                    /**
                     * imageUri masih null karena form ini belum memakai image picker.
                     * Kalau nanti sudah ada upload foto dari galeri,
                     * parameter null ini diganti menjadi selectedImageUri.
                     */
                    adminChildViewModel.addChild(
                        child = child,
                        imageUri = null
                    )

                    message = "Data anak berhasil disimpan"

                    fullName = ""
                    nickName = ""
                    birthDate = ""
                    gender = "Laki-laki"
                    parentUserId = ""
                    parentEmail = ""
                    photoUrl = ""
                    isActive = true
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
                    text = "Simpan Data Anak",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    fullName = ""
                    nickName = ""
                    birthDate = ""
                    gender = "Laki-laki"
                    parentUserId = ""
                    parentEmail = ""
                    photoUrl = ""
                    isActive = true
                    message = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = DaycarePrimary
                )
            ) {
                Text(
                    text = "Reset Form",
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
/**
 * Daftar Anak di Master Data Menu Classroom.
 */
@Composable
fun ChildListSection(
    adminChildViewModel: AdminChildViewModel,
    onEditClick: (Child) -> Unit,
    modifier: Modifier = Modifier
) {
    val children by adminChildViewModel.children.collectAsState(initial = emptyList())

    var childToDelete by remember { mutableStateOf<Child?>(null) }

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
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "Daftar Anak",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${children.size} anak terdaftar",
                fontSize = 13.sp,
                color = DaycareTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (children.isEmpty()) {
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
                        text = "Belum ada data anak",
                        color = DaycareTextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                children.forEachIndexed { index, child ->
                    ChildListItem(
                        child = child,
                        onEditClick = {
                            onEditClick(child)
                        },
                        onDeleteClick = {
                            childToDelete = child
                        }
                    )

                    if (index != children.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = DaycareBorder.copy(alpha = 0.45f)
                        )
                    }
                }
            }
        }
    }

    if (childToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                childToDelete = null
            },
            title = {
                Text(text = "Nonaktifkan Data Anak?")
            },
            text = {
                Text(
                    text = "Data ${childToDelete?.fullName} tidak akan dihapus permanen, hanya dibuat nonaktif."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        childToDelete?.let { child ->
                            adminChildViewModel.softDeleteChild(child)
                        }
                        childToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB91C1C)
                    )
                ) {
                    Text("Nonaktifkan")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        childToDelete = null
                    }
                ) {
                    Text(
                        text = "Batal",
                        color = DaycarePrimary
                    )
                }
            }
        )
    }
}


@Composable
fun ChildListItem(
    child: Child,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    color = DaycarePrimaryLight,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (child.gender == "Perempuan") "👧" else "👦",
                fontSize = 26.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = child.fullName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${child.gender} • ${child.birthDate}",
                fontSize = 13.sp,
                color = DaycareTextSecondary
            )

            if (!child.parentEmail.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Parent: ${child.parentEmail}",
                    fontSize = 12.sp,
                    color = DaycareTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        width = 1.dp,
                        color = DaycarePrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Edit",
                        color = DaycarePrimary,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFB91C1C)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Hapus",
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Surface(
            color = if (child.isActive) DaycarePrimaryLight else Color(0xFFFFE5E5),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = if (child.isActive) "Aktif" else "Nonaktif",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (child.isActive) DaycarePrimary else Color(0xFFB91C1C)
            )
        }
    }
}
/**
 * TextField reusable untuk form Classroom.
 */
@Composable
fun ClassroomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeholder)
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
            cursorColor = DaycarePrimary
        )
    )
}

/**
 * Chip untuk pilihan gender anak.
 */
@Composable
fun GenderChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) DaycarePrimary else DaycarePrimaryLight.copy(alpha = 0.65f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) DaycarePrimary else DaycarePrimary.copy(alpha = 0.15f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            color = if (selected) Color.White else DaycarePrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Card sementara untuk fitur yang belum dibuat detail UI-nya.
 *
 * Nanti masing-masing bisa dipisah menjadi screen sendiri:
 * - AttendanceScreen
 * - WeeklyPlanScreen
 * - ActivityUploadScreen
 */
@Composable
fun ClassroomComingSoonCard(
    title: String,
    description: String,
    emoji: String,
    buttonText: String,
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
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .background(
                        color = DaycarePrimaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = DaycareTextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    // TODO: Nanti arahkan ke screen sesuai fitur
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DaycarePrimary
                )
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Enum untuk menu Classroom.
 */
enum class ClassroomMenu {
    MASTER_DATA,
    ATTENDANCE,
    WEEKLY_PLAN,
    ACTIVITY_UPLOAD
}