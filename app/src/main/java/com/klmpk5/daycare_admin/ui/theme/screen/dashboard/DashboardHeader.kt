package com.klmpk5.daycare_admin.ui.theme.screen.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.R
import com.klmpk5.daycare_admin.ui.theme.DaycarePrimary

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
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(color = Color.White, shape = CircleShape)
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

            Column(modifier = Modifier.weight(1f)) {
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

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.16f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔔", fontSize = 23.sp)
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 7.dp)
                        .background(color = Color(0xFFFF5A5F), shape = CircleShape)
                )
            }
        }
    }
}