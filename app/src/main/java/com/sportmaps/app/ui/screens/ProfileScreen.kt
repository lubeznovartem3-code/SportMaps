package com.sportmaps.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sportmaps.app.data.SportType
import com.sportmaps.app.ui.components.*
import com.sportmaps.app.ui.viewmodel.ProfileViewModel

/** Экран «Профиль»: имя, фото и любимые виды спорта. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.setPhoto(context, uri)
    }

    LaunchedEffect(viewModel.savedMessage) {
        viewModel.savedMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.savedMessage = null
        }
    }

    Scaffold(
        topBar = { ScreenTopBar("Профиль", navigationIcon = { BackButton(onBack) }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(28.dp))
                Avatar(
                    name = viewModel.name,
                    size = 120.dp,
                    avatarPath = user?.avatarPath
                )
                TextButton(onClick = {
                    picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text(
                        "Изменить фото профиля",
                        fontSize = 13.sp,
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(16.dp))
                SportTextField(
                    label = "Отображаемое имя",
                    value = viewModel.name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = "Ваше имя"
                )
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth()) {
                    Text("Любимый спорт", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SportType.entries.forEach { sport ->
                            FilterChip(
                                selected = sport.title in viewModel.sports,
                                onClick = { viewModel.toggleSport(sport.title) },
                                label = { Text(sport.title) },
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }
                viewModel.error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
            PrimaryButton("Сохранить", onClick = viewModel::save)
            Spacer(Modifier.height(16.dp))
        }
    }
}
