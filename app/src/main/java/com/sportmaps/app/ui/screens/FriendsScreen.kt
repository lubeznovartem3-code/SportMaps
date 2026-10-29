package com.sportmaps.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sportmaps.app.data.Friend
import com.sportmaps.app.ui.components.*
import com.sportmaps.app.ui.viewmodel.FriendsViewModel
import com.sportmaps.app.ui.viewmodel.SessionViewModel
import kotlinx.coroutines.launch

/** Экран «Друзья». В режиме приглашения (из лобби) вместо меню показывается «Назад». */
@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel,
    sessionViewModel: SessionViewModel,
    inviteMode: Boolean,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val user by sessionViewModel.user.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAdd by remember { mutableStateOf(false) }

    val query = viewModel.query.trim()
    val filtered = friends.filter { query.isEmpty() || it.name.contains(query, ignoreCase = true) }
    val online = filtered.filter { it.online }
    val offline = filtered.filter { !it.online }

    AppDrawer(
        drawerState = drawerState,
        selected = DrawerItem.FRIENDS,
        user = user,
        gesturesEnabled = !inviteMode,
        onNavigate = { route ->
            scope.launch { drawerState.close() }
            onNavigate(route)
        }
    ) {
        Scaffold(
            topBar = {
                ScreenTopBar(
                    title = "Друзья",
                    navigationIcon = {
                        if (inviteMode) BackButton(onBack)
                        else MenuButton { scope.launch { drawerState.open() } }
                    },
                    actions = {
                        IconButton(onClick = { showAdd = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Добавить друга")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (inviteMode) {
                    item {
                        Text(
                            "Выберите друга, чтобы отправить ему приглашение в лобби",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = viewModel.query,
                        onValueChange = { viewModel.query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Поиск друзей...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = sportFieldColors()
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (online.isNotEmpty()) {
                    item { SectionTitle("В СЕТИ") }
                    items(online, key = { it.id }) { FriendRow(it) { onOpenChat(it.id) } }
                }
                if (offline.isNotEmpty()) {
                    item { SectionTitle("НЕ В СЕТИ") }
                    items(offline, key = { it.id }) { FriendRow(it) { onOpenChat(it.id) } }
                }
                if (filtered.isEmpty()) {
                    item {
                        Text(
                            "Никого не найдено",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        var name by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Добавить друга") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; error = null },
                        placeholder = { Text("Имя или логин") },
                        singleLine = true,
                        isError = error != null,
                        supportingText = { error?.let { Text(it) } }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    error = viewModel.addFriend(name)
                    if (error == null) showAdd = false
                }) { Text("Добавить") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Отмена") } }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
    )
}

@Composable
private fun FriendRow(friend: Friend, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(name = friend.name, size = 48.dp, online = friend.online)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(friend.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(
                friend.status,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.Chat,
            contentDescription = "Открыть чат",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
