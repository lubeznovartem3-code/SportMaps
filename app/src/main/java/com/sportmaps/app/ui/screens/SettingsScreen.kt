package com.sportmaps.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sportmaps.app.ui.components.*
import com.sportmaps.app.ui.viewmodel.SettingsViewModel

/** Экран «Аккаунт и безопасность»: смена пароля/логина и переключатель темы. */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onLogout: () -> Unit
) {
    val dark by viewModel.darkTheme.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { ScreenTopBar("Аккаунт и безопасность", navigationIcon = { BackButton(onBack) }) }
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
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(20.dp))
                SportTextField(
                    label = "Сменить пароль",
                    value = viewModel.newPassword,
                    onValueChange = { viewModel.newPassword = it; viewModel.clearError() },
                    placeholder = "Новый пароль",
                    isPassword = true
                )
                Spacer(Modifier.height(16.dp))
                SportTextField(
                    label = "Сменить логин",
                    value = viewModel.newLogin,
                    onValueChange = { viewModel.newLogin = it; viewModel.clearError() },
                    placeholder = "Новый логин"
                )
                viewModel.error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
                Spacer(Modifier.height(28.dp))
                Text(
                    "ИНТЕРФЕЙС",
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                SwitchRow("Светлая тема", checked = !dark) { viewModel.setDarkTheme(!it) }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SwitchRow("Тёмная тема", checked = dark) { viewModel.setDarkTheme(it) }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = {
                    viewModel.logout()
                    onLogout()
                }) {
                    Text("Выйти из аккаунта", color = MaterialTheme.colorScheme.error)
                }
            }
            PrimaryButton("Сохранить изменения") {
                if (viewModel.save()) onSaved()
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SwitchRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 15.sp)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
