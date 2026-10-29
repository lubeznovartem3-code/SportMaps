package com.sportmaps.app.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.sportmaps.app.SportMapsApp
import com.sportmaps.app.data.AppContainer

/** Фабрика ViewModel с передачей репозиториев из контейнера. */
class AppViewModelFactory(private val c: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(c.auth)
            modelClass.isAssignableFrom(SessionViewModel::class.java) -> SessionViewModel(c.auth)
            modelClass.isAssignableFrom(MapViewModel::class.java) -> MapViewModel(c.grounds, c.auth)
            modelClass.isAssignableFrom(LobbyViewModel::class.java) -> LobbyViewModel(c.grounds)
            modelClass.isAssignableFrom(FriendsViewModel::class.java) -> FriendsViewModel(c.messages)
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> ChatViewModel(c.messages)
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(c.auth)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(c.auth, c.prefs)
            else -> throw IllegalArgumentException("Неизвестная ViewModel: ${modelClass.name}")
        }
        return vm as T
    }
}

/** Получить ViewModel внутри Compose-функции. */
@Composable
inline fun <reified T : ViewModel> appViewModel(): T {
    val app = LocalContext.current.applicationContext as SportMapsApp
    return viewModel<T>(factory = AppViewModelFactory(app.container))
}
