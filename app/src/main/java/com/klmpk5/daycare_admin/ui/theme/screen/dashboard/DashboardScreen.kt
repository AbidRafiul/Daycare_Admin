package com.klmpk5.daycare_admin.ui.theme.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.klmpk5.daycare_admin.data.local.entities.Attendance
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.data.local.entities.DailyScore
import com.klmpk5.daycare_admin.data.local.entities.WeeklyPlan
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

data class ScheduleUi(
    val title: String,
    val dateRange: String,
    val status: String
)

data class TaskUi(
    val title: String,
    val deadline: String,
    val route: String
)

@Composable
fun DashboardScreen(
    navController: NavController,
    adminChildViewModel: AdminChildViewModel,
    attendanceViewModel: AttendanceViewModel,
    weeklyPlanViewModel: AdminWeeklyPlanViewModel,
    scoreViewModel: AdminScoreViewModel
) {
    val todayDate = remember { todayIsoDate() }
    val todayLabel = remember { todayDisplayDate() }
    val children by adminChildViewModel.children.collectAsState(initial = emptyList())
    val weeklyPlans by weeklyPlanViewModel.weeklyPlans.collectAsState(initial = emptyList())
    val attendanceList by attendanceViewModel.attendanceList.collectAsState(initial = emptyList())
    val todayScoresFlow = remember(todayDate) {
        scoreViewModel.getScoresByDate(todayDate)
    }
    val todayScores by todayScoresFlow.collectAsState(initial = emptyList())

    LaunchedEffect(todayDate) {
        attendanceViewModel.setDate(todayDate)
    }

    LaunchedEffect(children) {
        children.forEach { child ->
            scoreViewModel.syncScores(child.childId)
        }
    }

    val schedules = remember(weeklyPlans, todayDate) {
        buildTodaySchedules(weeklyPlans, todayDate)
    }
    val tasks = remember(children, attendanceList, weeklyPlans, todayScores, todayDate) {
        buildDashboardTasks(
            children = children,
            attendanceList = attendanceList,
            weeklyPlans = weeklyPlans,
            todayScores = todayScores,
            todayDate = todayDate
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = DaycareBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DaycareBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                DashboardHeader(
                    childCount = children.size,
                    taskCount = tasks.size
                )
            }

            item {
                TodayScheduleCard(
                    schedules = schedules,
                    todayLabel = todayLabel,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onWeeklyPlanClick = { navController.navigate("classroom") }
                )
            }

            item { Spacer(modifier = Modifier.height(18.dp)) }

            item {
                TodoListCard(
                    tasks = tasks,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onTaskClick = { route -> navController.navigate(route) },
                    onSeeAllClick = { navController.navigate("raport") }
                )
            }
        }
    }
}

@Composable
fun DashboardHeader(
    childCount: Int,
    taskCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
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
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = (-12).dp)
        ) {
            Text(
                text = "Dashboard",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$childCount anak aktif - $taskCount tugas perlu perhatian",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TodayScheduleCard(
    schedules: List<ScheduleUi>,
    todayLabel: String,
    modifier: Modifier = Modifier,
    onWeeklyPlanClick: () -> Unit = {}
) {
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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = DaycarePrimaryLight, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "WP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DaycarePrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weekly Plan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = todayLabel,
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
                        text = "Kelola",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        color = DaycarePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            if (schedules.isEmpty()) {
                EmptyDashboardState(
                    title = "Belum ada weekly plan aktif",
                    subtitle = "Tambahkan weekly plan untuk rentang tanggal hari ini."
                )
            } else {
                schedules.forEachIndexed { index, item ->
                    ScheduleItemCard(item = item, onDetailClick = onWeeklyPlanClick)
                    if (index != schedules.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            TextButton(
                onClick = onWeeklyPlanClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Lihat Semua Weekly Plan >",
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ScheduleItemCard(
    item: ScheduleUi,
    onDetailClick: () -> Unit
) {
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
                Text(text = "Plan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DaycarePrimary)
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
                Text(
                    text = item.dateRange,
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
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
                OutlinedButton(
                    onClick = onDetailClick,
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

@Composable
fun TodoListCard(
    tasks: List<TaskUi>,
    modifier: Modifier = Modifier,
    onTaskClick: (String) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = DaycarePrimaryLight, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = tasks.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DaycarePrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "To Do List",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Berdasarkan data yang belum lengkap",
                        fontSize = 14.sp,
                        color = DaycareTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (tasks.isEmpty()) {
                EmptyDashboardState(
                    title = "Semua data utama sudah lengkap",
                    subtitle = "Tidak ada tugas yang perlu perhatian hari ini."
                )
            } else {
                tasks.forEachIndexed { index, task ->
                    TaskItem(
                        task = task,
                        onClick = { onTaskClick(task.route) }
                    )
                    if (index != tasks.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = DaycareBorder.copy(alpha = 0.45f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onSeeAllClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Buka Raport >",
                    color = DaycarePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskUi,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White
    ) {
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
                Text(text = "!", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = DaycarePrimary)
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

            Text(text = ">", fontSize = 22.sp, color = DaycareTextMuted)
        }
    }
}

@Composable
fun EmptyDashboardState(
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DaycarePrimaryLight.copy(alpha = 0.45f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, DaycarePrimary.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = DaycareTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

private fun buildTodaySchedules(
    weeklyPlans: List<WeeklyPlan>,
    todayDate: String
): List<ScheduleUi> {
    return weeklyPlans
        .sortedWith(compareBy<WeeklyPlan> { it.startDate }.thenBy { it.endDate })
        .filter { plan ->
            isActivePlan(plan, todayDate) || plan.startDate > todayDate
        }
        .take(3)
        .map { plan ->
            ScheduleUi(
                title = plan.description.ifBlank { "Weekly Plan" },
                dateRange = "${formatShortDate(plan.startDate)} - ${formatShortDate(plan.endDate)}",
                status = if (isActivePlan(plan, todayDate)) "Aktif" else "Akan datang"
            )
        }
}

private fun buildDashboardTasks(
    children: List<Child>,
    attendanceList: List<Attendance>,
    weeklyPlans: List<WeeklyPlan>,
    todayScores: List<DailyScore>,
    todayDate: String
): List<TaskUi> {
    val tasks = mutableListOf<TaskUi>()

    if (children.isEmpty()) {
        tasks += TaskUi(
            title = "Tambahkan data anak",
            deadline = "Classroom - Data anak masih kosong",
            route = "classroom"
        )
    }

    if (weeklyPlans.none { isActivePlan(it, todayDate) }) {
        tasks += TaskUi(
            title = "Buat weekly plan untuk minggu ini",
            deadline = "Weekly Plan - Belum ada jadwal aktif",
            route = "classroom"
        )
    }

    if (children.isNotEmpty()) {
        val attendedChildIds = attendanceList.map { it.childId }.toSet()
        val missingAttendance = children.count { it.childId !in attendedChildIds }

        if (missingAttendance > 0) {
            tasks += TaskUi(
                title = "Lengkapi absensi $missingAttendance anak",
                deadline = "Hari ini - Presensi belum lengkap",
                route = "classroom"
            )
        }

        val scoredChildIds = todayScores.map { it.childId }.toSet()
        val missingScores = children.count { it.childId !in scoredChildIds }

        if (missingScores > 0) {
            tasks += TaskUi(
                title = "Isi raport harian $missingScores anak",
                deadline = "Hari ini - Perkembangan belum diisi",
                route = "raport"
            )
        }
    }

    return tasks
}

private fun isActivePlan(
    plan: WeeklyPlan,
    todayDate: String
): Boolean {
    return plan.startDate <= todayDate && todayDate <= plan.endDate
}

private fun todayIsoDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

private fun todayDisplayDate(): String {
    return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(Date())
}

private fun formatShortDate(value: String): String {
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value)
        if (date == null) {
            value
        } else {
            SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(date)
        }
    } catch (e: Exception) {
        value
    }
}
