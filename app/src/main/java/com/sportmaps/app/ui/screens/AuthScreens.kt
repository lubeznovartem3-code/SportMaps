package com.sportmaps.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaps.app.ui.components.PrimaryButton
import com.sportmaps.app.ui.components.SportTextField
import com.sportmaps.app.ui.viewmodel.AuthViewModel

/** Общий каркас экранов входа и регистрации. */
@Composable
private fun AuthScaffold(
    subtitle: String,
    fields: @Composable ColumnScope.() -> Unit,
    bottom: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))
            Text("SportMaps", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                fields()
            }
        }
        bottom()
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    var identifier by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeat by rememberSaveable { mutableStateOf("") }

    AuthScaffold(
        subtitle = "Найди идеальное место для игры",
        fields = {
            SportTextField(
                label = "Email или номер телефона",
                value = identifier,
                onValueChange = { identifier = it; viewModel.clearError() },
                placeholder = "example@mail.com",
                keyboardType = KeyboardType.Email
            )
            SportTextField(
                label = "Пароль",
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                placeholder = "••••••••",
                isPassword = true
            )
            SportTextField(
                label = "Повторите пароль",
                value = repeat,
                onValueChange = { repeat = it; viewModel.clearError() },
                placeholder = "••••••••",
                isPassword = true
            )
        },
        bottom = {
            viewModel.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            PrimaryButton("Создать аккаунт") {
                if (viewModel.register(identifier, password, repeat)) onSuccess()
            }
            TextButton(
                onClick = onGoToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    "Уже есть аккаунт? Войти",
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    var identifier by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AuthScaffold(
        subtitle = "Рады возвращению",
        fields = {
            SportTextField(
                label = "Логин или Email",
                value = identifier,
                onValueChange = { identifier = it; viewModel.clearError() },
                placeholder = "Введите логин"
            )
            SportTextField(
                label = "Пароль",
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                placeholder = "••••••••",
                isPassword = true
            )
            Text(
                "Демо-аккаунт: alex_sport / 123456",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        bottom = {
            viewModel.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            PrimaryButton("Войти") {
                if (viewModel.login(identifier, password)) onSuccess()
            }
            TextButton(
                onClick = onGoToRegister,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    "Зарегистрироваться",
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
