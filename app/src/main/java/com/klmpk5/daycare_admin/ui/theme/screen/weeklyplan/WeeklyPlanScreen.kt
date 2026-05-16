package com.klmpk5.daycare_admin.ui.theme.screen.weeklyplan

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.AdminWeeklyPlanViewModel
import com.klmpk5.daycare_admin.viewmodel.WeeklyPlanSaveState
import java.util.UUID

@Composable
fun WeeklyPlanScreen(
    weeklyPlanViewModel: AdminWeeklyPlanViewModel
) {
    val weeklyPlans by weeklyPlanViewModel.weeklyPlans.collectAsState(initial = emptyList())
    val saveState by weeklyPlanViewModel.saveState.collectAsState()

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(saveState) {
        when (saveState) {
            is WeeklyPlanSaveState.Success -> {
                message = "Weekly Plan berhasil disimpan"
                weeklyPlanViewModel.resetSaveState()

                startDate = ""
                endDate = ""
                description = ""
            }

            is WeeklyPlanSaveState.Error -> {
                message = (saveState as WeeklyPlanSaveState.Error).message
                weeklyPlanViewModel.resetSaveState()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DaycareBackground)
            .padding(bottom = 24.dp)
    ) {
        WeeklyPlanHeader()

        WeeklyPlanFormCard(
            startDate = startDate,
            onStartDateChange = { startDate = it },
            endDate = endDate,
            onEndDateChange = { endDate = it },
            description = description,
            onDescriptionChange = { description = it },
            saveState = saveState,
            message = message,
            onSaveClick = {
                if (startDate.isBlank()) {
                    message = "Tanggal mulai tidak boleh kosong"
                    return@WeeklyPlanFormCard
                }

                if (endDate.isBlank()) {
                    message = "Tanggal selesai tidak boleh kosong"
                    return@WeeklyPlanFormCard
                }

                if (description.isBlank()) {
                    message = "Deskripsi kegiatan tidak boleh kosong"
                    return@WeeklyPlanFormCard
                }

                val plan = WeeklyPlan(
                    planId = UUID.randomUUID().toString(),
                    startDate = startDate.trim(),
                    endDate = endDate.trim(),
                    description = description.trim()
                )

                weeklyPlanViewModel.addWeeklyPlan(plan)
            },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-36).dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        WeeklyPlanListHeader(
            total = weeklyPlans.size,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-36).dp)
        )

        if (weeklyPlans.isEmpty()) {
            EmptyWeeklyPlanCard(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-36).dp)
            )
        } else {
            weeklyPlans.forEach { plan ->
                WeeklyPlanItemCard(
                    plan = plan,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp)
                        .offset(y = (-36).dp)
                )
            }
        }
    }
}

@Composable
fun WeeklyPlanHeader() {
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
                text = "Weekly Plan",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kelola rencana kegiatan mingguan daycare",
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
                .background(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📅",
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun WeeklyPlanFormCard(
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    saveState: WeeklyPlanSaveState,
    message: String?,
    onSaveClick: () -> Unit,
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
            WeeklySectionTitle(
                emoji = "📝",
                title = "Periode Rencana",
                subtitle = "Tentukan tanggal mulai dan selesai"
            )

            Spacer(modifier = Modifier.height(18.dp))

            WeeklyPlanTextField(
                value = startDate,
                onValueChange = onStartDateChange,
                label = "Tanggal Mulai",
                placeholder = "YYYY-MM-DD",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(14.dp))

            WeeklyPlanTextField(
                value = endDate,
                onValueChange = onEndDateChange,
                label = "Tanggal Selesai",
                placeholder = "YYYY-MM-DD",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(24.dp))

            WeeklySectionTitle(
                emoji = "📋",
                title = "Deskripsi Kegiatan",
                subtitle = "Jelaskan rencana aktivitas daycare minggu ini"
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                label = {
                    Text("Deskripsi")
                },
                placeholder = {
                    Text("Contoh: Minggu ini anak-anak akan belajar warna, motorik halus, dan kegiatan seni.")
                },
                maxLines = 5,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DaycarePrimary,
                    unfocusedBorderColor = DaycareBorder,
                    focusedLabelColor = DaycarePrimary,
                    cursorColor = DaycarePrimary
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                color = DaycarePrimaryLight.copy(alpha = 0.55f),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Weekly Plan ini nantinya bisa tampil di dashboard sebagai jadwal hari ini.",
                        fontSize = 13.sp,
                        color = DaycareTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            if (!message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = message,
                    color = if (message.contains("berhasil", ignoreCase = true)) {
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
                onClick = onSaveClick,
                enabled = saveState !is WeeklyPlanSaveState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DaycarePrimary,
                    disabledContainerColor = DaycarePrimary.copy(alpha = 0.45f)
                )
            ) {
                if (saveState is WeeklyPlanSaveState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Simpan Weekly Plan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklySectionTitle(
    emoji: String,
    title: String,
    subtitle: String
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
                text = emoji,
                fontSize = 25.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                fontSize = 19.sp,
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
    }
}

@Composable
fun WeeklyPlanTextField(
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
fun WeeklyPlanListHeader(
    total: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Daftar Weekly Plan",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = DaycareTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$total rencana mingguan tersimpan",
            fontSize = 13.sp,
            color = DaycareTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
fun WeeklyPlanItemCard(
    plan: WeeklyPlan,
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
                    text = "📅",
                    fontSize = 27.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${plan.startDate} - ${plan.endDate}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = plan.description,
                    fontSize = 13.sp,
                    color = DaycareTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
fun EmptyWeeklyPlanCard(
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
                text = "📅",
                fontSize = 44.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Belum ada Weekly Plan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tambahkan rencana kegiatan mingguan terlebih dahulu.",
                fontSize = 14.sp,
                color = DaycareTextSecondary
            )
        }
    }
}
