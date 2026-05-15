package com.klmpk5.daycare_admin.ui.screen.raport

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AdminScoreViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

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
    onBack: () -> Unit
) {
    val children by adminChildViewModel.children.collectAsState(initial = emptyList())
    val child = children.find { it.childId == childId }

    val scores by scoreViewModel.getScores(childId).collectAsState(initial = emptyList())

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
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    )
                }
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
fun AddDailyScoreForm(
    child: Child,
    scoreViewModel: AdminScoreViewModel,
    modifier: Modifier = Modifier
) {
    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    var date by remember { mutableStateOf(todayDate) }
    var activityName by remember { mutableStateOf("") }
    var selectedScore by remember { mutableStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

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

            RaportTextField(
                value = activityName,
                onValueChange = { activityName = it },
                label = "Nama Aktivitas",
                placeholder = "Contoh: Melukis, Puzzle, Membaca Buku",
                keyboardType = KeyboardType.Text
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

                    if (activityName.isBlank()) {
                        message = "Nama aktivitas tidak boleh kosong"
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
                        activityName = activityName.trim(),
                        score = selectedScore,
                        notes = notes.ifBlank { null }
                    )

                    scoreViewModel.addScore(dailyScore)

                    message = "Nilai harian berhasil disimpan"

                    activityName = ""
                    selectedScore = 0
                    notes = ""
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