package com.klmpk5.daycare_admin.ui.theme.screen.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary

// ─────────────────────────────────────────────────────────────────────────────
// Data models
// ─────────────────────────────────────────────────────────────────────────────

data class ScheduleUi(
    val emoji: String,
    val title: String,
    val className: String,
    val time: String,
    val status: String? = null
)

data class TaskUi(
    val emoji: String,
    val title: String,
    val deadline: String
)

// ─────────────────────────────────────────────────────────────────────────────
// DashboardScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        containerColor = DaycareBackground,
        bottomBar = {
            DashboardBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DaycareBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item { DashboardHeader() }

            item {
                TodayScheduleCard(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onWeeklyPlanClick = { navController.navigate("classroom") }
                )
            }

            item { Spacer(modifier = Modifier.height(18.dp)) }

            item {
                TodoListCard(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onSeeAllClick = { navController.navigate("raport") }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TodayScheduleCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TodayScheduleCard(
    modifier: Modifier = Modifier,
    onWeeklyPlanClick: () -> Unit = {}
) {
    val schedules = listOf(
        ScheduleUi(
            emoji = "👧",
            title = "Morning Circle",
            className = "Kelas Matahari",
            time = "08:30 - 09:15",
            status = "Akan Dimulai"
        ),
        ScheduleUi(
            emoji = "🍎",
            title = "Snack Time",
            className = "Kelas Bintang",
            time = "09:15 - 09:45"
        ),
        ScheduleUi(
            emoji = "🎨",
            title = "Creative Learning",
            className = "Kelas Pelangi",
            time = "10:00 - 11:00"
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-42).dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📅", fontSize = 27.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Jadwal Hari Ini",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Thursday, 14 May",
                        fontSize = 14.sp,
                        color = DaycarePrimary
                    )
                }
                Surface(
                    color = DaycarePrimaryLight,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, DaycarePrimary.copy(alpha = 0.18f)),
                    onClick = onWeeklyPlanClick
                ) {
                    Text(
                        text = "Weekly Plan",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        color = DaycarePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            schedules.forEachIndexed { index, item ->
                ScheduleItemCard(item = item)
                if (index != schedules.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            TextButton(
                onClick = onWeeklyPlanClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Lihat Semua Jadwal  ›",
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ScheduleItemCard(item: ScheduleUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DaycareBorder.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(color = DaycarePrimaryLight, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 27.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "🏫  ${item.className}", fontSize = 13.sp, color = DaycareTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "⏰  ${item.time}", fontSize = 13.sp, color = DaycareTextSecondary)
            }

            Column(horizontalAlignment = Alignment.End) {
                if (item.status != null) {
                    Surface(color = DaycarePrimaryLight, shape = RoundedCornerShape(50)) {
                        Text(
                            text = item.status,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = DaycarePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedButton(
                    onClick = { /* TODO: tampilkan detail / aksi kelas */ },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, DaycarePrimary),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Detail",
                        color = DaycarePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TodoListCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TodoListCard(
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {}
) {
    val tasks = listOf(
        TaskUi(
            emoji = "📝",
            title = "Update child attendance",
            deadline = "Hari ini • Sebelum 10:00"
        ),
        TaskUi(
            emoji = "📊",
            title = "Prepare weekly activity report",
            deadline = "Besok • Sebelum 16:00"
        ),
        TaskUi(
            emoji = "📷",
            title = "Upload lesson photos",
            deadline = "Kamis, 15 May • Sebelum 17:00"
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-42).dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📋", fontSize = 27.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tugas Saya",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Tugas yang perlu perhatian",
                        fontSize = 14.sp,
                        color = DaycareTextSecondary
                    )
                }
                Surface(
                    color = Color(0xFFFFF3D6),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(text = "⭐", modifier = Modifier.padding(12.dp), fontSize = 25.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            TodoFilterChips()

            Spacer(modifier = Modifier.height(14.dp))

            tasks.forEachIndexed { index, task ->
                TaskItem(task = task)
                if (index != tasks.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = DaycareBorder.copy(alpha = 0.45f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onSeeAllClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Lihat Semua Tugas  ›",
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TodoFilterChips() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChipItem(text = "All",         selected = true)
        FilterChipItem(text = "Raport",      selected = false)
        FilterChipItem(text = "Weekly Plan", selected = false)
        FilterChipItem(text = "Classroom",   selected = false)
    }
}

@Composable
fun FilterChipItem(text: String, selected: Boolean) {
    Surface(
        color = if (selected) DaycarePrimary else DaycarePrimaryLight.copy(alpha = 0.65f),
        shape = RoundedCornerShape(50),
        border = if (selected) null else BorderStroke(1.dp, DaycarePrimary.copy(alpha = 0.10f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else DaycarePrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TaskItem(task: TaskUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = DaycarePrimaryLight, shape = RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = task.emoji, fontSize = 23.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DaycareTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.deadline, fontSize = 13.sp, color = DaycareTextSecondary)
        }

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .padding(2.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(1.5.dp, DaycareTextMuted.copy(alpha = 0.65f))
            ) {}
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(text = "›", fontSize = 26.sp, color = DaycareTextMuted)
    }
}