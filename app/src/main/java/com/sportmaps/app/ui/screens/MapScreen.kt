package com.sportmaps.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sportmaps.app.data.Arena
import com.sportmaps.app.ui.components.*
import com.sportmaps.app.ui.viewmodel.MapViewModel
import com.sportmaps.app.ui.viewmodel.SessionViewModel
import kotlinx.coroutines.launch

/** Экран «Карта площадок» с боковым меню. */
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    sessionViewModel: SessionViewModel,
    onPlay: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val user by sessionViewModel.user.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchOpen by remember { mutableStateOf(false) }

    AppDrawer(
        drawerState = drawerState,
        selected = DrawerItem.MAP,
        user = user,
        onNavigate = { route ->
            scope.launch { drawerState.close() }
            onNavigate(route)
        }
    ) {
        Scaffold(
            topBar = {
                ScreenTopBar(
                    title = "Карта площадок",
                    navigationIcon = { MenuButton { scope.launch { drawerState.open() } } },
                    actions = {
                        IconButton(onClick = {
                            searchOpen = !searchOpen
                            if (!searchOpen) viewModel.onQueryChange("")
                        }) {
                            Icon(
                                if (searchOpen) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Поиск"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
            ) {
                MockMap(
                    arenas = viewModel.visibleArenas,
                    selectedId = viewModel.selectedId,
                    onSelect = viewModel::select,
                    bottomReserved = 330.dp,
                    modifier = Modifier.fillMaxSize()
                )
                if (searchOpen) {
                    OutlinedTextField(
                        value = viewModel.query,
                        onValueChange = viewModel::onQueryChange,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(12.dp),
                        placeholder = { Text("Поиск площадок и видов спорта") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
                ArenaCard(
                    arena = viewModel.selected,
                    onPlay = {
                        viewModel.play()
                        onPlay()
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

/** Стилизованная заглушка карты Москвы: улицы, река и маркеры площадок. */
@Composable
private fun MockMap(
    arenas: List<Arena>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    bottomReserved: Dp,
    modifier: Modifier = Modifier
) {
    val background = MaterialTheme.colorScheme.surfaceVariant
    val street = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
    val river = MaterialTheme.colorScheme.outlineVariant
    val marker = MaterialTheme.colorScheme.primary

    BoxWithConstraints(modifier.background(background)) {
        val areaWidth = maxWidth
        val areaHeight = (maxHeight - bottomReserved).coerceAtLeast(180.dp)

        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Река
            val riverPath = Path().apply {
                moveTo(-20f, h * 0.62f)
                cubicTo(w * 0.25f, h * 0.35f, w * 0.55f, h * 0.75f, w + 20f, h * 0.40f)
            }
            drawPath(riverPath, river, style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Round))
            // Улицы
            val width = 10.dp.toPx()
            drawLine(street, Offset(0f, h * 0.15f), Offset(w, h * 0.30f), width)
            drawLine(street, Offset(w * 0.15f, 0f), Offset(w * 0.30f, h), width)
            drawLine(street, Offset(w * 0.70f, 0f), Offset(w * 0.62f, h), width)
            drawLine(street, Offset(0f, h * 0.50f), Offset(w, h * 0.56f), width / 2f)
            drawLine(street, Offset(w * 0.45f, 0f), Offset(w * 0.50f, h), width / 2f)
        }

        arenas.forEach { arena ->
            val isSelected = arena.id == selectedId
            val cx = areaWidth * arena.mapX
            val cy = areaHeight * arena.mapY
            if (isSelected) {
                Box(
                    Modifier
                        .offset(x = cx - 36.dp, y = cy - 22.dp)
                        .size(width = 72.dp, height = 92.dp)
                        .background(river, RoundedCornerShape(50))
                )
            }
            Box(
                Modifier
                    .offset(x = cx - 22.dp, y = cy - 22.dp)
                    .size(44.dp)
                    .clickable { onSelect(arena.id) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(if (isSelected) 26.dp else 20.dp)
                        .background(marker, CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
                )
            }
        }
    }
}

/** Карточка площадки внизу карты. */
@Composable
private fun ArenaCard(arena: Arena, onPlay: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 12.dp
    ) {
        Column(
            Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp, bottom = 12.dp)
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    arena.name,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.width(8.dp))
                SportChip(arena.sport.title)
            }
            Spacer(Modifier.height(2.dp))
            Text(
                arena.address,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            DashedBox(Modifier.fillMaxWidth().height(88.dp)) {
                Text(
                    "📷 Фотография площадки",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(14.dp))
            PrimaryButton("Играть", onClick = onPlay)
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Stat("ИГРОКИ", "${arena.players} / ${arena.maxPlayers} человек")
                Stat("ВРЕМЯ РАБОТЫ", arena.hours)
                Stat("СТОИМОСТЬ", arena.cost)
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(
            label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
