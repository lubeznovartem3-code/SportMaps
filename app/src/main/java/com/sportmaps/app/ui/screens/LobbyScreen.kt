package com.sportmaps.app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sportmaps.app.data.Lobby
import com.sportmaps.app.ui.components.*
import com.sportmaps.app.ui.viewmodel.LobbyViewModel
import kotlinx.coroutines.launch

/** Экран «Сбор состава». */
@Composable
fun LobbyScreen(
    viewModel: LobbyViewModel,
    onBack: () -> Unit,
    onInvite: () -> Unit,
    onLeft: () -> Unit
) {
    val lobby by viewModel.lobby.collectAsStateWithLifecycle()
    val current = lobby
    if (current == null) {
        // Лобби покинуто — возвращаемся на карту
        LaunchedEffect(Unit) { onLeft() }
        Box(Modifier.fillMaxSize())
        return
    }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    Scaffold(
        topBar = {
            ScreenTopBar(
                title = "Сбор состава",
                navigationIcon = { BackButton(onBack) },
                actions = {
                    IconButton(onClick = {
                        clipboard.setText(AnnotatedString("sportmaps://lobby/${current.arena.id}"))
                        scope.launch { snackbar.showSnackbar("Ссылка на лобби скопирована") }
                    }) {
                        Icon(Icons.Default.Link, contentDescription = "Скопировать ссылку")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(16.dp))
                LobbyInfoCard(current)
                Spacer(Modifier.height(20.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Состав команды", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        "${current.players.size} / ${current.capacity} игроков",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(12.dp))
                current.players.forEach { player ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Avatar(name = player.name, size = 36.dp)
                        Spacer(Modifier.width(12.dp))
                        Text(player.name, modifier = Modifier.weight(1f), fontSize = 14.sp)
                        Text(
                            player.status,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                repeat((current.capacity - current.players.size).coerceAtLeast(0)) {
                    DashedBox(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(44.dp)
                    ) {
                        Text(
                            "+ Ожидание игрока...",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            PrimaryButton("Пригласить друзей", onClick = onInvite)
            Spacer(Modifier.height(10.dp))
            SecondaryButton("Покинуть лобби", onClick = viewModel::leave)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LobbyInfoCard(lobby: Lobby) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(Modifier.padding(16.dp)) {
            Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primary) {
                Text(
                    "${sportEmoji(lobby)} ${lobby.arena.sport.title.uppercase()}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(lobby.arena.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    lobby.arena.address.removePrefix("Москва, "),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    InfoLabel("КОГДА ИГРАЕМ")
                    Text(lobby.time, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Column(Modifier.weight(1f)) {
                    InfoLabel("ОРГАНИЗАТОР")
                    Text("${lobby.organizer} (Вы)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}

private fun sportEmoji(lobby: Lobby): String = when (lobby.arena.sport) {
    com.sportmaps.app.data.SportType.BASKETBALL -> "🏀"
    com.sportmaps.app.data.SportType.FOOTBALL -> "⚽"
    com.sportmaps.app.data.SportType.VOLLEYBALL -> "🏐"
}

@Composable
private fun InfoLabel(text: String) {
    Text(
        text,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(2.dp))
}
