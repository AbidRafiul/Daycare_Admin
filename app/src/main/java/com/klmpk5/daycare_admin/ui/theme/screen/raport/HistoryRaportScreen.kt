package com.klmpk5.daycare_admin.ui.screen.raport

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.klmpk5.daycare_admin.data.local.entities.Attendance
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_admin.data.model.AttendanceStatus
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminScoreViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminWeeklyPlanViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.io.File

/**
 * HistoryRaportScreen menampilkan detail raport anak.
 *
 * Isi halaman:
 * - Tombol back
 * - Detail anak
 * - Ringkasan score
 * - Riwayat nilai
 * - Form tambah nilai harian di bagian bawah
 */
@Composable
fun HistoryRaportScreen(
    childId: String,
    adminChildViewModel: AdminChildViewModel,
    scoreViewModel: AdminScoreViewModel,
    weeklyPlanViewModel: AdminWeeklyPlanViewModel,
    attendanceViewModel: AttendanceViewModel,
    onBack: () -> Unit
) {
    val children by adminChildViewModel.children.collectAsState(initial = emptyList())
    val child = children.find { it.childId == childId }

    val scores by scoreViewModel.getScores(childId).collectAsState(initial = emptyList())
    val weeklyPlans by weeklyPlanViewModel.weeklyPlans.collectAsState(initial = emptyList())
    val attendanceList by attendanceViewModel.attendanceList.collectAsState(initial = emptyList())
    var selectedScoreDetail by remember { mutableStateOf<DailyScore?>(null) }

    LaunchedEffect(childId) {
        scoreViewModel.syncScores(childId)
    }

    Scaffold(
        containerColor = DaycareBackground
    ) { innerPadding ->

        if (child == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Data anak tidak ditemukan",
                    color = DaycareTextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DaycareBackground)
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    HistoryRaportHeader(
                        onBack = onBack
                    )
                }

                item {
                    ChildRaportProfileCard(
                        child = child,
                        scores = scores,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-36).dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                item {
                    Text(
                        text = "Riwayat Nilai Harian",
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-28).dp),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )
                }

                if (scores.isEmpty()) {
                    item {
                        EmptyScoreCard(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .offset(y = (-18).dp)
                        )
                    }
                } else {
                    items(
                        items = scores,
                        key = { it.scoreId }
                    ) { score ->
                        ScoreHistoryItem(
                            score = score,
                            onClick = {
                                attendanceViewModel.setDate(score.date)
                                selectedScoreDetail = score
                            },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 12.dp)
                                .offset(y = (-18).dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    AddDailyScoreForm(
                        child = child,
                        scoreViewModel = scoreViewModel,
                        weeklyPlans = weeklyPlans,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    )
                }
            }

            selectedScoreDetail?.let { score ->
                val attendanceForScoreDate = attendanceList.find { attendance ->
                    attendance.childId == childId && attendance.date == score.date
                }

                ScoreDetailDialog(
                    score = score,
                    attendance = attendanceForScoreDate,
                    onDismiss = {
                        selectedScoreDetail = null
                    }
                )
            }
        }
    }
}

@Composable
fun HistoryRaportHeader(
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
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = "History Raport",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Riwayat perkembangan dan tambah nilai harian",
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
                text = "⭐",
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun ChildRaportProfileCard(
    child: Child,
    scores: List<DailyScore>,
    modifier: Modifier = Modifier
) {
    val averageScore = if (scores.isNotEmpty()) {
        scores.map { it.score }.average()
    } else {
        0.0
    }

    val progressLabel = when {
        scores.isEmpty() -> "Belum Ada"
        averageScore >= 4.5 -> "Sangat Baik"
        averageScore >= 3.5 -> "Baik"
        averageScore >= 2.5 -> "Cukup"
        else -> "Perlu Bimbingan"
    }

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
                        .size(66.dp)
                        .background(
                            color = DaycarePrimaryLight,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (child.gender == "Perempuan") "👧" else "👦",
                        fontSize = 34.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = child.fullName,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "${child.gender} • ${child.birthDate}",
                        fontSize = 13.sp,
                        color = DaycareTextSecondary
                    )

                    if (!child.nickName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Panggilan: ${child.nickName}",
                            fontSize = 12.sp,
                            color = DaycareTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RaportMiniStat(
                    title = "Total Nilai",
                    value = scores.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                RaportMiniStat(
                    title = "Rata-rata",
                    value = if (scores.isEmpty()) "-" else String.format("%.1f", averageScore),
                    modifier = Modifier.weight(1f)
                )

                RaportMiniStat(
                    title = "Progress",
                    value = progressLabel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RaportMiniStat(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DaycarePrimaryLight.copy(alpha = 0.65f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = DaycarePrimary.copy(alpha = 0.10f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = DaycarePrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                color = DaycareTextSecondary
            )
        }
    }
}

@Composable
fun ScoreHistoryItem(
    score: DailyScore,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = DaycareBorder.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = DaycarePrimaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⭐",
                    fontSize = 27.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = score.activityName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = score.date,
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )

                if (!score.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = score.notes,
                        fontSize = 12.sp,
                        color = DaycareTextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!score.imageUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Foto aktivitas tersedia",
                        fontSize = 12.sp,
                        color = DaycarePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Surface(
                color = DaycarePrimaryLight,
                shape = RoundedCornerShape(50),
                border = BorderStroke(
                    width = 1.dp,
                    color = DaycarePrimary.copy(alpha = 0.18f)
                )
            ) {
                Text(
                    text = score.score.toString(),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = DaycarePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun ScoreDetailDialog(
    score: DailyScore,
    attendance: Attendance?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Detail Nilai Harian",
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = score.activityName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tanggal: ${score.date}",
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Score: ${score.score}",
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Presensi Hari Itu",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DaycareTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DaycarePrimaryLight.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = DaycarePrimary.copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = attendance?.let { statusLabel(it.status) } ?: "Belum ada data presensi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (attendance == null) DaycareTextSecondary else DaycarePrimary
                        )

                        if (attendance != null) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Dicatat pada ${attendance.date}",
                                fontSize = 12.sp,
                                color = DaycareTextSecondary
                            )
                        }
                    }
                }

                if (!score.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Catatan Guru",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = score.notes,
                        fontSize = 13.sp,
                        color = DaycareTextSecondary,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Foto Aktivitas",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DaycareTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (score.imageUrl.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DaycarePrimaryLight.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Tidak ada foto untuk history ini",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp,
                            color = DaycareTextSecondary
                        )
                    }
                } else {
                    AsyncImage(
                        model = score.imageUrl,
                        contentDescription = "Foto aktivitas ${score.activityName}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DaycarePrimaryLight),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = score.imageUrl,
                        fontSize = 11.sp,
                        color = DaycareTextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Tutup",
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
fun AddDailyScoreForm(
    child: Child,
    scoreViewModel: AdminScoreViewModel,
    weeklyPlans: List<WeeklyPlan>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    var date by remember { mutableStateOf(todayDate) }
    var selectedWeeklyPlan by remember { mutableStateOf<WeeklyPlan?>(null) }
    var selectedScore by remember { mutableStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = pendingCameraUri
            message = "Foto aktivitas siap diupload"
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
                        text = "📝",
                        fontSize = 25.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Tambah Nilai Harian",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Isi perkembangan harian ${child.nickName ?: child.fullName}",
                        fontSize = 13.sp,
                        color = DaycareTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            RaportTextField(
                value = date,
                onValueChange = { date = it },
                label = "Tanggal",
                placeholder = "YYYY-MM-DD",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(14.dp))

            WeeklyPlanActivitySelector(
                weeklyPlans = weeklyPlans,
                selectedWeeklyPlan = selectedWeeklyPlan,
                onPlanSelected = { selectedWeeklyPlan = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Score (1 - 5)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            ScoreSelector(
                selectedScore = selectedScore,
                onScoreClick = { selectedScore = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1 = Perlu Bimbingan, 3 = Cukup, 5 = Sangat Baik",
                fontSize = 12.sp,
                color = DaycareTextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = {
                    Text("Catatan Guru")
                },
                placeholder = {
                    Text("Tuliskan catatan perkembangan anak...")
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

            Spacer(modifier = Modifier.height(16.dp))

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
                        text = "Foto Aktivitas",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (selectedImageUri == null) {
                            "Ambil foto langsung dari kamera"
                        } else {
                            "Foto sudah dipilih dan akan diupload saat disimpan"
                        },
                        fontSize = 12.sp,
                        color = DaycareTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val imageUri = createRaportCameraImageUri(context)
                            pendingCameraUri = imageUri
                            cameraLauncher.launch(imageUri)
                        },
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
                            text = if (selectedImageUri == null) "Ambil Foto" else "Ambil Ulang Foto",
                            color = DaycarePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = DaycarePrimaryLight.copy(alpha = 0.55f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Tips Penilaian",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycarePrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Nilai 1: perlu bimbingan • 2: mulai berkembang • 3: cukup • 4: baik • 5: sangat baik",
                        fontSize = 12.sp,
                        color = DaycareTextSecondary,
                        lineHeight = 18.sp
                    )
                }
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

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    if (date.isBlank()) {
                        message = "Tanggal tidak boleh kosong"
                        return@Button
                    }

                    if (selectedWeeklyPlan == null) {
                        message = "Pilih aktivitas dari Weekly Plan terlebih dahulu"
                        return@Button
                    }

                    if (selectedScore == 0) {
                        message = "Pilih score terlebih dahulu"
                        return@Button
                    }

                    val dailyScore = DailyScore(
                        scoreId = UUID.randomUUID().toString(),
                        childId = child.childId,
                        date = date.trim(),
                        activityName = selectedWeeklyPlan?.description.orEmpty(),
                        score = selectedScore,
                        notes = notes.ifBlank { null },
                        imageUrl = null
                    )

                    scoreViewModel.addScore(dailyScore, selectedImageUri)

                    message = if (selectedImageUri == null) {
                        "Nilai harian berhasil disimpan"
                    } else {
                        "Nilai harian dan foto berhasil disimpan"
                    }

                    selectedWeeklyPlan = null
                    selectedScore = 0
                    notes = ""
                    selectedImageUri = null
                    pendingCameraUri = null
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
                    text = "Simpan Nilai",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ScoreSelector(
    selectedScore: Int,
    onScoreClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (score in 1..5) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                onClick = {
                    onScoreClick(score)
                },
                color = if (selectedScore == score) DaycarePrimary else DaycarePrimaryLight.copy(alpha = 0.55f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selectedScore == score) DaycarePrimary else DaycarePrimary.copy(alpha = 0.12f)
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = score.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedScore == score) Color.White else DaycarePrimary
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyPlanActivitySelector(
    weeklyPlans: List<WeeklyPlan>,
    selectedWeeklyPlan: WeeklyPlan?,
    onPlanSelected: (WeeklyPlan) -> Unit
) {
    Text(
        text = "Aktivitas dari Weekly Plan",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = DaycareTextPrimary
    )

    Spacer(modifier = Modifier.height(10.dp))

    if (weeklyPlans.isEmpty()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DaycarePrimaryLight.copy(alpha = 0.55f),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(
                width = 1.dp,
                color = DaycarePrimary.copy(alpha = 0.12f)
            )
        ) {
            Text(
                text = "Belum ada Weekly Plan. Tambahkan dulu di menu Classroom.",
                modifier = Modifier.padding(16.dp),
                fontSize = 13.sp,
                color = DaycareTextSecondary
            )
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            weeklyPlans.forEach { plan ->
                val selected = selectedWeeklyPlan?.planId == plan.planId

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onPlanSelected(plan) },
                    color = if (selected) DaycarePrimary else DaycarePrimaryLight.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selected) DaycarePrimary else DaycarePrimary.copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "${plan.startDate} - ${plan.endDate}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White.copy(alpha = 0.88f) else DaycarePrimary
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = plan.description,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else DaycareTextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RaportTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
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
            cursorColor = DaycarePrimary
        )
    )
}

@Composable
fun EmptyScoreCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⭐",
                fontSize = 44.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Belum ada nilai harian",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tambahkan nilai harian anak melalui form di bawah.",
                fontSize = 14.sp,
                color = DaycareTextSecondary
            )
        }
    }
}

fun createRaportCameraImageUri(context: android.content.Context): Uri {
    val imageDir = File(context.cacheDir, "images")
    imageDir.mkdirs()

    val imageFile = File.createTempFile(
        "raport_${System.currentTimeMillis()}_",
        ".jpg",
        imageDir
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

fun statusLabel(status: String): String {
    return when (status) {
        AttendanceStatus.PRESENT.value -> AttendanceStatus.PRESENT.label
        AttendanceStatus.SICK.value -> AttendanceStatus.SICK.label
        AttendanceStatus.PERMISSION.value -> AttendanceStatus.PERMISSION.label
        AttendanceStatus.ABSENT.value -> AttendanceStatus.ABSENT.label
        else -> status
    }
}
