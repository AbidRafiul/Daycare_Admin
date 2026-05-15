package com.klmpk5.daycare_admin.ui.theme.screen.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "💬", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Chat", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Coming soon", fontSize = 14.sp)
        }
    }
}