package com.klmpk5.daycare_admin.ui.screen.raport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.klmpk5.daycare_admin.data.local.entities.Child
import com.klmpk5.daycare_admin.ui.theme.DaycareBackground
import com.klmpk5.daycare_admin.ui.theme.DaycareBorder
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_admin.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_admin.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_admin.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_admin.viewmodel.AdminChildViewModel

/**
 * RaportScreen adalah halaman awal menu raport.
 *
 * Halaman ini menampilkan daftar anak.
 * Guru bisa memilih anak untuk melihat riwayat nilai dan menambahkan nilai harian.
 */
@Composable
fun RaportScreen(
    adminChildViewModel: AdminChildViewModel,
    onOpenHistory: (String) -> Unit
) {
    val children by adminChildViewModel.children.collectAsState(initial = emptyList())

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
                RaportHeader()
            }

            item {
                RaportInfoCard(
                    totalChildren = children.count { it.isActive },
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
                    text = "Pilih Anak",
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .offset(y = (-30).dp),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            val activeChildren = children.filter { it.isActive }

            if (activeChildren.isEmpty()) {
                item {
                    EmptyRaportCard(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-24).dp)
                    )
                }
            } else {
                items(
                    items = activeChildren,
                    key = { it.childId }
                ) { child ->
                    RaportChildItem(
                        child = child,
                        onClick = {
                            onOpenHistory(child.childId)
                        },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp)
                            .offset(y = (-24).dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RaportHeader() {
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
                text = "Raport Anak",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Lihat perkembangan dan isi nilai harian anak",
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
                text = "📄",
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun RaportInfoCard(
    totalChildren: Int,
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
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = "⭐",
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Daily Score",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycareTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$totalChildren anak aktif tersedia untuk penilaian",
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )
            }
        }
    }
}

@Composable
fun RaportChildItem(
    child: Child,
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
                    .size(56.dp)
                    .background(
                        color = DaycarePrimaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (child.gender == "Perempuan") "👧" else "👦",
                    fontSize = 28.sp
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
            }

            Text(
                text = "›",
                fontSize = 30.sp,
                color = DaycareTextMuted
            )
        }
    }
}

@Composable
fun EmptyRaportCard(
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
