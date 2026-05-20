package com.klmpk5.daycare_admin.ui.theme.screen.classroom

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.ui.theme.screen.weeklyplan.EmptyWeeklyPlanCard
import com.klmpk5.daycare_admin.ui.theme.screen.weeklyplan.WeeklyPlanItemCard
import com.klmpk5.daycare_admin.ui.theme.screen.weeklyplan.WeeklyPlanListHeader
import com.klmpk5.daycare_admin.ui.theme.screen.weeklyplan.WeeklyPlanScreen
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminWeeklyPlanViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * ClassroomScreen adalah halaman utama untuk fitur kelas.
 *
 * Di halaman ini guru/admin bisa mengakses:
 * - Master Data Anak
 * - Presensi
 * - Weekly Plan
 *
 * Untuk sekarang UI masih static.
 * Nanti bisa dihubungkan ke ViewModel, Room, dan Firebase.
 */
@Composable
fun ClassroomScreen(
    adminChildViewModel: AdminChildViewModel,
    attendanceViewModel: AttendanceViewModel,
    weeklyPlanViewModel: AdminWeeklyPlanViewModel
) {
    var selectedMenu by remember { mutableStateOf<ClassroomMenu?>(null) }
    var masterDataPage by remember { mutableStateOf(MasterDataPage.LIST) }
    var selectedChildForEdit by remember { mutableStateOf<Child?>(null) }
    val weeklyPlans by weeklyPlanViewModel.weeklyPlans.collectAsState(initial = emptyList())

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
            when {
                selectedMenu == ClassroomMenu.MASTER_DATA &&
                    masterDataPage == MasterDataPage.FORM -> {
                    item {
                        ChildPageHeader(
                            title = if (selectedChildForEdit == null) "Tambah Anak" else "Edit Anak",
                            subtitle = if (selectedChildForEdit == null) {
                                "Isi data anak baru"
                            } else {
                                "Perbarui data anak"
                            },
                            onBack = {
                                selectedChildForEdit = null
                                masterDataPage = MasterDataPage.LIST
                            }
                        )
                    }

                    item {
                        MasterDataChildForm(
                            adminChildViewModel = adminChildViewModel,
                            selectedChild = selectedChildForEdit,
                            onBackToList = {
                                selectedChildForEdit = null
                                masterDataPage = MasterDataPage.LIST
                            },
                            onSaveComplete = {
                                selectedChildForEdit = null
                                masterDataPage = MasterDataPage.LIST
                            },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-34).dp)
                        )
                    }
                }

                selectedMenu == ClassroomMenu.MASTER_DATA -> {
                    item {
                        ChildPageHeader(
                            title = "Daftar Anak",
                            subtitle = "Kelola master data anak daycare",
                            onBack = {
                                selectedChildForEdit = null
                                masterDataPage = MasterDataPage.LIST
                                selectedMenu = null
                            }
                        )
                    }

                    item {
                        ChildListSection(
                            adminChildViewModel = adminChildViewModel,
                            onAddClick = {
                                selectedChildForEdit = null
                                masterDataPage = MasterDataPage.FORM
                            },
                            onEditClick = { child ->
                                selectedChildForEdit = child
                                masterDataPage = MasterDataPage.FORM
                            },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-34).dp)
                        )
                    }
                }

                selectedMenu == ClassroomMenu.ATTENDANCE -> {
                    item {
                        ChildPageHeader(
                            title = "Presensi",
                            subtitle = "Kelola kehadiran anak daycare hari ini",
                            onBack = {
                                selectedMenu = null
                            }
                        )
                    }

                    item {
                        AttendanceScreen(
                            adminChildViewModel = adminChildViewModel,
                            attendanceViewModel = attendanceViewModel,
                            showHeader = false
                        )
                    }
                }

                selectedMenu == ClassroomMenu.WEEKLY_PLAN -> {
                    item {
                        ChildPageHeader(
                            title = "Weekly Plan",
                            subtitle = "Tambah rencana kegiatan mingguan",
                            onBack = {
                                selectedMenu = null
                            }
                        )
                    }

                    item {
                        WeeklyPlanScreen(
                            weeklyPlanViewModel = weeklyPlanViewModel,
                            showHeader = false,
                            showList = false
                        )
                    }
                }

                else -> {
                    item {
                        ClassroomHeader()
                    }

                    item {
                        ClassroomMenuGrid(
                            selectedMenu = selectedMenu,
                            onMenuClick = { menu ->
                                selectedMenu = menu
                                if (menu == ClassroomMenu.MASTER_DATA) {
                                    selectedChildForEdit = null
                                    masterDataPage = MasterDataPage.LIST
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-34).dp)
                        )
                    }

                    item {
                        ClassroomWeeklyPlanSection(
                            total = weeklyPlans.size,
                            weeklyPlans = weeklyPlans,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-18).dp)
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
                .offset(y = (-22).dp)
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

    }
}

@Composable
fun ChildPageHeader(
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

/**
 * Grid menu Classroom.
 *
 * Menu ini digunakan untuk memilih fitur yang ingin dibuka.
 */
@Composable
fun ClassroomMenuGrid(
    selectedMenu: ClassroomMenu?,
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
            }
        }
    }
}

@Composable
fun ClassroomWeeklyPlanSection(
    total: Int,
    weeklyPlans: List<WeeklyPlan>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        WeeklyPlanListHeader(total = total)

        if (weeklyPlans.isEmpty()) {
            EmptyWeeklyPlanCard()
        } else {
            weeklyPlans
                .sortedByDescending { it.startDate }
                .take(3)
                .forEach { plan ->
                    WeeklyPlanItemCard(
                        plan = plan,
                        modifier = Modifier.padding(bottom = 12.dp)
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
 * parentEmail
 * photoUrl

 */
@Composable
fun MasterDataChildForm(
    adminChildViewModel: AdminChildViewModel,
    modifier: Modifier = Modifier,
    selectedChild: Child? = null,
    onBackToList: () -> Unit,
    onSaveComplete: () -> Unit
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Laki-laki") }
    var parentEmail by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var isActive by remember { mutableStateOf(true) }

    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedChild) {
        if (selectedChild != null) {
            fullName = selectedChild.fullName
            nickName = selectedChild.nickName ?: ""
            birthDate = selectedChild.birthDate
            gender = selectedChild.gender
            parentEmail = selectedChild.parentEmail ?: ""
            photoUrl = selectedChild.photoUrl ?: ""
            selectedImageUri = null
            pendingCameraUri = null
            isActive = selectedChild.isActive
            message = "Mode edit data anak"
        } else {
            fullName = ""
            nickName = ""
            birthDate = ""
            gender = "Laki-laki"
            parentEmail = ""
            photoUrl = ""
            selectedImageUri = null
            pendingCameraUri = null
            isActive = true
            message = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = pendingCameraUri
            photoUrl = ""
            message = "Foto anak siap diupload"
        } else {
            message = "Pengambilan foto dibatalkan"
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
                        text = if (selectedChild == null) "Tambah Data Anak" else "Edit Data Anak",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = if (selectedChild == null) {
                            "Isi form untuk menambahkan anak daycare"
                        } else {
                            "Perbarui data anak daycare"
                        },
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

            ClassroomDateField(
                value = birthDate,
                onDateSelected = { birthDate = it },
                label = "Tanggal Lahir",
                placeholder = "Pilih tanggal lahir"
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
                value = parentEmail,
                onValueChange = { parentEmail = it },
                label = "Email Orang Tua",
                placeholder = "parent@email.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            ChildPhotoCameraField(
                selectedImageUri = selectedImageUri,
                currentPhotoUrl = photoUrl.ifBlank { null },
                gender = gender,
                onTakePhoto = {
                    val imageUri = createChildCameraImageUri(context)
                    pendingCameraUri = imageUri
                    cameraLauncher.launch(imageUri)
                },
                onRemovePhoto = {
                    selectedImageUri = null
                    photoUrl = ""
                    message = "Foto anak dihapus dari form"
                }
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
                    color = if (
                        message == "Data anak berhasil disimpan" ||
                        message == "Data anak berhasil diperbarui" ||
                        message == "Data anak dan foto sedang disimpan" ||
                        message == "Data anak dan foto sedang diperbarui" ||
                        message == "Foto anak siap diupload" ||
                        message == "Foto anak dihapus dari form" ||
                        message == "Mode edit data anak"
                    ) {
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
                        childId = selectedChild?.childId ?: UUID.randomUUID().toString(),
                        childIdRemote = selectedChild?.childIdRemote,
                        fullName = fullName.trim(),
                        nickName = nickName.ifBlank { null },
                        birthDate = birthDate.trim(),
                        gender = gender,
                        parentEmail = parentEmail.ifBlank { null },
                        photoUrl = photoUrl.ifBlank { null },
                        isActive = isActive,
                        createdAt = selectedChild?.createdAt ?: now,
                        updatedAt = now
                    )

                    if (selectedChild == null) {
                        adminChildViewModel.addChild(
                            child = child,
                            imageUri = selectedImageUri
                        )
                    } else {
                        adminChildViewModel.updateChild(
                            child = child,
                            imageUri = selectedImageUri
                        )
                    }

                    message = if (selectedChild == null) {
                        if (selectedImageUri == null) "Data anak berhasil disimpan" else "Data anak dan foto sedang disimpan"
                    } else {
                        if (selectedImageUri == null) "Data anak berhasil diperbarui" else "Data anak dan foto sedang diperbarui"
                    }

                    fullName = ""
                    nickName = ""
                    birthDate = ""
                    gender = "Laki-laki"
                    parentEmail = ""
                    photoUrl = ""
                    selectedImageUri = null
                    pendingCameraUri = null
                    isActive = true
                    onSaveComplete()
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
                    text = if (selectedChild == null) "Simpan Data Anak" else "Simpan Perubahan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    onBackToList()
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
                    text = "Kembali ke Daftar Anak",
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ChildPhotoCameraField(
    selectedImageUri: Uri?,
    currentPhotoUrl: String?,
    gender: String,
    onTakePhoto: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    val photoModel: Any? = selectedImageUri ?: currentPhotoUrl

    Surface(
        color = DaycarePrimaryLight.copy(alpha = 0.55f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = DaycarePrimary.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Foto Anak",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when {
                    selectedImageUri != null -> "Foto baru akan diupload ke Cloudinary saat disimpan"
                    !currentPhotoUrl.isNullOrBlank() -> "Foto anak saat ini tersimpan di Cloudinary"
                    else -> "Ambil foto langsung dari kamera"
                },
                fontSize = 12.sp,
                color = DaycareTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (photoModel != null) {
                AsyncImage(
                    model = photoModel,
                    contentDescription = "Foto anak",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.82f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (gender == "Perempuan") "Foto anak perempuan" else "Foto anak laki-laki",
                        fontSize = 13.sp,
                        color = DaycareTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onTakePhoto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = DaycarePrimary
                )
            ) {
                Text(
                    text = if (photoModel == null) "Ambil Foto" else "Ambil Ulang Foto",
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (photoModel != null) {
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onRemovePhoto,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Hapus Foto",
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
    onAddClick: () -> Unit,
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

            Button(
                onClick = onAddClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DaycarePrimary
                )
            ) {
                Text(
                    text = "Tambah Data Anak",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomDateField(
    value: String,
    onDateSelected: (String) -> Unit,
    label: String,
    placeholder: String
) {
    var showPicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = label)
            },
            placeholder = {
                Text(text = placeholder)
            },
            trailingIcon = {
                Text(
                    text = "Pilih",
                    modifier = Modifier.padding(end = 12.dp),
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DaycarePrimary,
                unfocusedBorderColor = DaycareBorder,
                focusedLabelColor = DaycarePrimary,
                cursorColor = DaycarePrimary
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showPicker = true }
        )
    }

    if (showPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseDateMillis(value)
        )

        DatePickerDialog(
            onDismissRequest = {
                showPicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(formatDateMillis(millis))
                        }
                        showPicker = false
                    }
                ) {
                    Text("Pilih", color = DaycarePrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Batal", color = DaycarePrimary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun parseDateMillis(value: String): Long? {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value)?.time
    } catch (e: Exception) {
        null
    }
}

private fun formatDateMillis(millis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
}

fun createChildCameraImageUri(context: android.content.Context): Uri {
    val imageDir = File(context.cacheDir, "images")
    imageDir.mkdirs()

    val imageFile = File.createTempFile(
        "child_${System.currentTimeMillis()}_",
        ".jpg",
        imageDir
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
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
    WEEKLY_PLAN
}

private enum class MasterDataPage {
    LIST,
    FORM
}
