package com.sportmaps.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sportmaps.app.data.ChatMessage
import com.sportmaps.app.ui.components.*
import com.sportmaps.app.ui.theme.OnlineGreen
import com.sportmaps.app.ui.viewmodel.ChatViewModel

/** Личный чат с другом. */
@Composable
fun ChatScreen(viewModel: ChatViewModel, friendId: String, onBack: () -> Unit) {
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val allMessages by viewModel.messages.collectAsStateWithLifecycle()
    val friend = friends.firstOrNull { it.id == friendId }
    val messages = allMessages[friendId].orEmpty()
    var text by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Прокрутка к последнему сообщению
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    fun send() {
        if (text.isNotBlank()) {
            viewModel.send(friendId, text)
            text = ""
        }
    }

    Scaffold(
        topBar = {
            ScreenTopBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(name = friend?.name.orEmpty(), size = 38.dp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(friend?.name.orEmpty(), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(
                                if (friend?.online == true) "В сети" else "Не в сети",
                                fontSize = 11.sp,
                                color = if (friend?.online == true) OnlineGreen
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = { BackButton(onBack) }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { MessageBubble(it) }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Сообщение...") },
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                    colors = sportFieldColors()
                )
                IconButton(onClick = { send() }, enabled = text.isNotBlank()) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val mine = message.fromMe
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (mine) 16.dp else 4.dp,
                bottomEnd = if (mine) 4.dp else 16.dp
            ),
            color = if (mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ) {
            Text(
                message.text,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                fontSize = 14.sp
            )
        }
    }
}
