package com.klmpk5.daycare_admin.ui.theme.screen.chat

import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_admin.data.remote.model.ChatMessageRemoteDto
import com.klmpk5.daycare_admin.ui.theme.*
import com.klmpk5.daycare_admin.viewmodel.ChatViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBackClick: () -> Unit = {}
) {
    val messages by viewModel.messages.collectAsState()
    var typedText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Grup Diskusi Daycare", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Semua Admin & Orang Tua", fontSize = 12.sp, color = DaycareTextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = DaycareTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = typedText,
                        onValueChange = { typedText = it },
                        placeholder = { Text("Ketik pesan di sini...") },
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DaycarePrimary,
                            cursorColor = DaycarePrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (typedText.isNotBlank()) {
                                viewModel.sendMessage(typedText)
                                typedText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = DaycarePrimary),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Tombol Kirim",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        containerColor = DaycareBackground
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                val isAdmin = message.role == "admin"

                // KARTU PESAN (UI SAMA PERSIS DENGAN ORANG TUA)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Lingkaran Avatar
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = if (isAdmin) DaycarePrimaryLight else Color(0xFFFFF3E0), // Hijau untuk Admin, Oranye untuk Parent
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isAdmin) "G" else message.senderName.take(1).uppercase(),
                                color = if (isAdmin) DaycarePrimary else Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Nama dan Isi Pesan
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = message.senderName,
                                color = DaycareTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.text,
                                color = DaycareTextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        // Jam
                        val cal = Calendar.getInstance(Locale.ENGLISH).apply { timeInMillis = message.timestamp }
                        val timeString = DateFormat.format("HH:mm", cal).toString()

                        Text(
                            text = timeString,
                            color = DaycareTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Top) // Bikin jam nempel di atas
                        )
                    }
                }
            }
        }
    }
}