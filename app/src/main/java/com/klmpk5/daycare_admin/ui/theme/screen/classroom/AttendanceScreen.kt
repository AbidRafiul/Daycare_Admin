package com.klmpk5.daycare_admin.ui.theme.screen.classroom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.data.local.entities.Attendance
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.data.model.AttendanceStatus
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel
import com.klmpk5.daycare_admin.viewmodel.AttendanceSaveState
import com.klmpk5.daycare_admin.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AttendanceScreen adalah halaman untuk mengisi presensi anak.
 *
 * Data anak diambil dari AdminChildViewModel.
 * Data presensi disimpan melalui AttendanceViewModel.
 */
@Composable
fun AttendanceScreen(
    adminChildViewModel: AdminChildViewModel,
    attendanceViewModel: AttendanceViewModel,
    showHeader: Boolean = true
) {
    val children by adminChildViewModel.children.collectAsState(initial = emptyList())
    val attendanceList by attendanceViewModel.attendanceList.collectAsState(initial = emptyList())
    val saveState by attendanceViewModel.saveState.collectAsState()

    var attendancePage by remember { mutableStateOf(AttendancePage.LIST) }

    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val readableDate = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(Date())
    }

    /**
     * selectedStatuses menyimpan pilihan status presensi setiap anak.
     *
     * Key   = childId
     * Value = status presensi, contoh: PRESENT, SICK, PERMISSION, ABSENT
     */
    var selectedStatuses by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    /**
     * noteMap menyimpan catatan opsional setiap anak.
     */
    var noteMap by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    LaunchedEffect(todayDate) {
        attendanceViewModel.setDate(todayDate)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DaycareBackground)
            .padding(bottom = 24.dp)
    ) {
        if (showHeader) {
            AttendanceHeader()
        }

        AttendanceSummaryCard(
            totalChildren = children.count { it.isActive },
            selectedCount = if (attendancePage == AttendancePage.LIST) {
                attendanceList.size
            } else {
                selectedStatuses.size
            },
            readableDate = readableDate,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-36).dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        when (attendancePage) {
            AttendancePage.LIST -> {
                AttendanceListSection(
                    attendanceList = attendanceList,
                    activeChildrenCount = children.count { it.isActive },
                    onStartAttendanceClick = {
                        attendancePage = AttendancePage.FORM
                    },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .offset(y = (-36).dp)
                )
            }

            AttendancePage.FORM -> {
                if (children.none { it.isActive }) {
                    EmptyAttendanceCard(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-36).dp)
                    )
                } else {
                    children.filter { it.isActive }.forEach { child ->
                        AttendanceChildItem(
                            child = child,
                            selectedStatus = selectedStatuses[child.childId],
                            note = noteMap[child.childId].orEmpty(),
                            onStatusSelected = { status ->
                                selectedStatuses = selectedStatuses + (child.childId to status)
                            },
                            onNoteChange = { note ->
                                noteMap = noteMap + (child.childId to note)
                            },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 14.dp)
                                .offset(y = (-36).dp)
                        )
                    }

                    SaveAttendanceButton(
                        enabled = selectedStatuses.isNotEmpty(),
                        isLoading = saveState is AttendanceSaveState.Loading,
                        onClick = {
                            children
                                .filter { it.isActive }
                                .forEach { child ->
                                    val status = selectedStatuses[child.childId]

                                    if (status != null) {
                                        val now = System.currentTimeMillis()

                                        val attendance = Attendance(
                                            attendanceId = "${child.childId}_$todayDate",
                                            attendanceIdRemote = null,
                                            childId = child.childId,
                                            childName = child.fullName,
                                            date = todayDate,
                                            status = status,
                                            recordedBy = null,
                                            createdAt = now,
                                            updatedAt = now
                                        )

                                        attendanceViewModel.saveAttendance(attendance)
                                    }
                                }

                            selectedStatuses = emptyMap()
                            noteMap = emptyMap()
                            attendancePage = AttendancePage.LIST
                        },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-24).dp)
                    )

                    TextButton(
                        onClick = {
                            attendancePage = AttendancePage.LIST
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .offset(y = (-18).dp)
                    ) {
                        Text(
                            text = "Kembali ke Daftar Presensi",
                            color = DaycarePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (attendancePage == AttendancePage.LIST) {
            AttendanceSaveMessage(
                saveState = saveState,
                onReset = {
                    attendanceViewModel.resetSaveState()
                },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-24).dp)
            )
        }
    }
}

@Composable
fun AttendanceListSection(
    attendanceList: List<Attendance>,
    activeChildrenCount: Int,
    onStartAttendanceClick: () -> Unit,
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
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "Daftar Presensi Anak",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${attendanceList.size} dari $activeChildrenCount anak sudah dipresensi",
                fontSize = 13.sp,
                color = DaycareTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onStartAttendanceClick,
                enabled = activeChildrenCount > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DaycarePrimary,
                    disabledContainerColor = DaycarePrimary.copy(alpha = 0.45f)
                )
            ) {
                Text(
                    text = "Presensi Anak",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (activeChildrenCount == 0) {
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
                        text = "Tambahkan data anak terlebih dahulu di menu Master Data.",
                        color = DaycareTextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else if (attendanceList.isEmpty()) {
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
                        text = "Belum ada presensi hari ini",
                        color = DaycareTextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                attendanceList.forEachIndexed { index, attendance ->
                    AttendanceListItem(attendance = attendance)

                    if (index != attendanceList.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = DaycareBorder.copy(alpha = 0.45f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceListItem(
    attendance: Attendance
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
                text = "OK",
                fontSize = 14.sp,
                color = DaycarePrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = attendance.childName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = attendance.date,
                fontSize = 13.sp,
                color = DaycareTextSecondary
            )
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
                text = statusLabel(attendance.status),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                color = DaycarePrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Header halaman presensi.
 */
@Composable
fun AttendanceHeader() {
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

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = (-22).dp)
        ) {
            Text(
                text = "Presensi Anak",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kelola kehadiran anak daycare hari ini",
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp)
                .size(44.dp)
                .background(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✅",
                fontSize = 22.sp
            )
        }
    }
}

/**
 * Card ringkasan presensi hari ini.
 */
@Composable
fun AttendanceSummaryCard(
    totalChildren: Int,
    selectedCount: Int,
    readableDate: String,
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
                        .size(54.dp)
                        .background(
                            color = DaycarePrimaryLight,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📋",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Presensi Hari Ini",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = readableDate,
                        fontSize = 13.sp,
                        color = DaycareTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AttendanceMiniStat(
                    title = "Total Anak",
                    value = totalChildren.toString(),
                    modifier = Modifier.weight(1f)
                )

                AttendanceMiniStat(
                    title = "Sudah Diisi",
                    value = selectedCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Statistik kecil di dalam summary card.
 */
@Composable
fun AttendanceMiniStat(
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
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DaycarePrimary
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = DaycareTextSecondary
            )
        }
    }
}

/**
 * Card item anak untuk presensi.
 */
@Composable
fun AttendanceChildItem(
    child: Child,
    selectedStatus: String?,
    note: String,
    onStatusSelected: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
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
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
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
                        text = if (child.gender == "Perempuan") "👧" else "👦",
                        fontSize = 27.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = child.fullName,
                        fontSize = 16.sp,
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
                }

                if (selectedStatus != null) {
                    Surface(
                        color = DaycarePrimaryLight,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = statusLabel(selectedStatus),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DaycarePrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Status Presensi",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            AttendanceStatusSelector(
                selectedStatus = selectedStatus,
                onStatusSelected = onStatusSelected
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Catatan")
                },
                placeholder = {
                    Text("Optional, contoh: datang terlambat")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DaycarePrimary,
                    unfocusedBorderColor = DaycareBorder,
                    focusedLabelColor = DaycarePrimary,
                    cursorColor = DaycarePrimary
                )
            )
        }
    }
}

/**
 * Selector status presensi.
 */
@Composable
fun AttendanceStatusSelector(
    selectedStatus: String?,
    onStatusSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AttendanceStatusChip(
                text = AttendanceStatus.PRESENT.label,
                selected = selectedStatus == AttendanceStatus.PRESENT.value,
                onClick = {
                    onStatusSelected(AttendanceStatus.PRESENT.value)
                },
                modifier = Modifier.weight(1f)
            )

            AttendanceStatusChip(
                text = AttendanceStatus.SICK.label,
                selected = selectedStatus == AttendanceStatus.SICK.value,
                onClick = {
                    onStatusSelected(AttendanceStatus.SICK.value)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AttendanceStatusChip(
                text = AttendanceStatus.PERMISSION.label,
                selected = selectedStatus == AttendanceStatus.PERMISSION.value,
                onClick = {
                    onStatusSelected(AttendanceStatus.PERMISSION.value)
                },
                modifier = Modifier.weight(1f)
            )

            AttendanceStatusChip(
                text = AttendanceStatus.ABSENT.label,
                selected = selectedStatus == AttendanceStatus.ABSENT.value,
                onClick = {
                    onStatusSelected(AttendanceStatus.ABSENT.value)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Chip status presensi.
 */
@Composable
fun AttendanceStatusChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(42.dp),
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) DaycarePrimary else DaycarePrimaryLight.copy(alpha = 0.55f),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) DaycarePrimary else DaycarePrimary.copy(alpha = 0.12f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else DaycarePrimary
            )
        }
    }
}

/**
 * Tombol simpan semua presensi yang sudah dipilih.
 */
@Composable
fun SaveAttendanceButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DaycarePrimary,
            disabledContainerColor = DaycarePrimary.copy(alpha = 0.45f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            Text(
                text = "Simpan Presensi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Card kosong jika belum ada data anak.
 */
@Composable
fun EmptyAttendanceCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👧",
                fontSize = 46.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Belum ada data anak",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tambahkan data anak terlebih dahulu di menu Master Data.",
                fontSize = 14.sp,
                color = DaycareTextSecondary
            )
        }
    }
}

/**
 * Pesan setelah proses simpan presensi.
 */
@Composable
fun AttendanceSaveMessage(
    saveState: AttendanceSaveState,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (saveState) {
        is AttendanceSaveState.Success -> {
            LaunchedEffect(Unit) {
                onReset()
            }

            Text(
                text = "Presensi berhasil disimpan",
                modifier = modifier,
                color = DaycarePrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        is AttendanceSaveState.Error -> {
            Text(
                text = saveState.message,
                modifier = modifier,
                color = Color(0xFFB91C1C),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        else -> Unit
    }
}

/**
 * Helper untuk mengubah value status menjadi label.
 */
fun statusLabel(status: String): String {
    return when (status) {
        AttendanceStatus.PRESENT.value -> AttendanceStatus.PRESENT.label
        AttendanceStatus.SICK.value -> AttendanceStatus.SICK.label
        AttendanceStatus.PERMISSION.value -> AttendanceStatus.PERMISSION.label
        AttendanceStatus.ABSENT.value -> AttendanceStatus.ABSENT.label
        else -> status
    }
}

enum class AttendancePage {
    LIST,
    FORM
}
