package com.klmpk5.daycare_admin.ui.theme.screen.login.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.R
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary

/**
 * DashboardScreen adalah halaman utama setelah Admin/Guru berhasil login.
 *
 * Untuk saat ini data masih dummy/static.
 * Nanti bagian Today Schedule akan diambil dari fitur Weekly Plan.
 * Bagian My To-Do List nanti bisa diambil dari fitur tugas / aktivitas guru.
 */
@Composable
fun DashboardScreen() {
    Scaffold(
        containerColor = DaycareBackground,

        // Bottom navigation utama aplikasi
        bottomBar = {
            DashboardBottomNavigation()
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DaycareBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                DashboardHeader()
            }

            item {
                TodayScheduleCard(
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                TodoListCard(
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

/**
 * Header bagian atas dashboard.
 *
 * Berisi:
 * - Logo daycare
 * - Nama aplikasi
 * - Role user
 * - Icon notifikasi
 */
@Composable
fun DashboardHeader() {
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
        // Dekorasi background agar tidak terlalu polos
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 28.dp, y = 30.dp)
                .background(
                    color = Color.White.copy(alpha = 0.10f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = (-12).dp)
                .background(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = CircleShape
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo aplikasi
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.daykids_logo),
                    contentDescription = "Logo Daykids Club",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Daykids Club",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Admin / Guru Dashboard",
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 15.sp
                )
            }

            // Icon notifikasi dibuat pakai emoji agar tidak bergantung pada dependency icon tambahan
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.16f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔔",
                    fontSize = 23.sp
                )

                // Badge merah kecil
                Box(
                    modifier = Modifier
                        .size(10.dp)
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
}

/**
 * Card Jadwal Hari Ini.
 *
 * Nanti data di bagian ini akan diambil dari Weekly Plan.
 */
@Composable
fun TodayScheduleCard(
    modifier: Modifier = Modifier
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
            time = "09:15 - 09:45",
            status = null
        ),
        ScheduleUi(
            emoji = "🎨",
            title = "Creative Learning",
            className = "Kelas Pelangi",
            time = "10:00 - 11:00",
            status = null
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-42).dp),
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
                Text(
                    text = "📅",
                    fontSize = 27.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
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

                // Chip untuk menuju fitur Weekly Plan
                Surface(
                    color = DaycarePrimaryLight,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        width = 1.dp,
                        color = DaycarePrimary.copy(alpha = 0.18f)
                    )
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
                onClick = {
                    // TODO: Nanti arahkan ke halaman Weekly Plan
                },
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

/**
 * Item jadwal yang muncul di dalam card Jadwal Hari Ini.
 *
 * Tombol Detail nanti bisa diarahkan ke:
 * - Detail Weekly Plan
 * - Bottom sheet aksi
 * - Mulai Kelas
 * - Absensi
 */
@Composable
fun ScheduleItemCard(
    item: ScheduleUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = DaycareBorder.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon ilustrasi kegiatan
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        color = DaycarePrimaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 27.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                    text = "🏫  ${item.className}",
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "⏰  ${item.time}",
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (item.status != null) {
                    Surface(
                        color = DaycarePrimaryLight,
                        shape = RoundedCornerShape(50)
                    ) {
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
                    onClick = {
                        // TODO: Nanti tampilkan detail / aksi kelas
                    },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        width = 1.dp,
                        color = DaycarePrimary
                    ),
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

/**
 * Card Tugas Saya.
 *
 * Untuk sekarang masih dummy.
 * Nanti bisa digunakan untuk menampilkan:
 * - tugas laporan
 * - absensi yang belum diisi
 * - upload foto aktivitas
 * - catatan perkembangan anak
 */
@Composable
fun TodoListCard(
    modifier: Modifier = Modifier
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
                Text(
                    text = "📋",
                    fontSize = 27.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
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

                // Dekorasi kecil kanan atas
                Surface(
                    color = Color(0xFFFFF3D6),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "⭐",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 25.sp
                    )
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
                onClick = {
                    // TODO: Nanti arahkan ke halaman semua tugas
                },
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

/**
 * Filter chip untuk kategori tugas.
 *
 * Untuk sekarang belum ada logic filter.
 */
@Composable
fun TodoFilterChips() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChipItem(
            text = "All",
            selected = true
        )

        FilterChipItem(
            text = "Raport",
            selected = false
        )

        FilterChipItem(
            text = "Weekly Plan",
            selected = false
        )

        FilterChipItem(
            text = "Classroom",
            selected = false
        )
    }
}

@Composable
fun FilterChipItem(
    text: String,
    selected: Boolean
) {
    Surface(
        color = if (selected) DaycarePrimary else DaycarePrimaryLight.copy(alpha = 0.65f),
        shape = RoundedCornerShape(50),
        border = if (selected) null else BorderStroke(
            width = 1.dp,
            color = DaycarePrimary.copy(alpha = 0.10f)
        )
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

/**
 * Item task di dalam My To-Do List.
 */
@Composable
fun TaskItem(
    task: TaskUi
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
                .background(
                    color = DaycarePrimaryLight,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = task.emoji,
                fontSize = 23.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DaycareTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.deadline,
                fontSize = 13.sp,
                color = DaycareTextSecondary
            )
        }

        // Lingkaran check kosong
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
                border = BorderStroke(
                    width = 1.5.dp,
                    color = DaycareTextMuted.copy(alpha = 0.65f)
                )
            ) {}
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "›",
            fontSize = 26.sp,
            color = DaycareTextMuted
        )
    }
}

/**
 * Bottom navigation aplikasi.
 *
 * Menu:
 * - Home
 * - Raport
 * - Weekly Plan
 * - Chat
 * - Profile
 *
 * Icon menggunakan emoji agar aman tanpa dependency material-icons-extended.
 */
@Composable
fun DashboardBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
    ) {
        DashboardNavItem(
            icon = "🏠",
            label = "Home",
            selected = true
        )

        DashboardNavItem(
            icon = "📄",
            label = "Raport",
            selected = false
        )

        DashboardNavItem(
            icon = "📅",
            label = "Classroom",
            selected = false
        )

        DashboardNavItem(
            icon = "💬",
            label = "Chat",
            selected = false
        )

        DashboardNavItem(
            icon = "👤",
            label = "Profile",
            selected = false
        )
    }
}

@Composable
fun RowScope.DashboardNavItem(
    icon: String,
    label: String,
    selected: Boolean
) {
    NavigationBarItem(
        selected = selected,
        onClick = {
            // TODO: Nanti hubungkan ke navigation route masing-masing menu
        },
        icon = {
            Text(
                text = icon,
                fontSize = 21.sp
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = DaycarePrimary,
            selectedTextColor = DaycarePrimary,
            indicatorColor = DaycarePrimaryLight,
            unselectedIconColor = DaycareTextMuted,
            unselectedTextColor = DaycareTextMuted
        )
    )
}

/**
 * Data dummy untuk item schedule.
 *
 * Nanti ini bisa diganti dengan entity WeeklyPlanItem atau data dari ViewModel.
 */
data class ScheduleUi(
    val emoji: String,
    val title: String,
    val className: String,
    val time: String,
    val status: String? = null
)

/**
 * Data dummy untuk item tugas.
 *
 * Nanti bisa diganti dengan entity Task atau data dari ViewModel.
 */
data class TaskUi(
    val emoji: String,
    val title: String,
    val deadline: String
)