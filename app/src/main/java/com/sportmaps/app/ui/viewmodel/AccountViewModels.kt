package com.sportmaps.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaps.app.data.AuthRepository
import com.sportmaps.app.data.SportType
import com.sportmaps.app.data.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/** Экран «Профиль». */
class ProfileViewModel(private val auth: AuthRepository) : ViewModel() {

    val user = auth.currentUser

    var name by mutableStateOf(auth.currentUser.value?.displayName.orEmpty())

    val sports = mutableStateListOf<String>().apply {
        addAll(
            auth.currentUser.value?.favoriteSports.orEmpty()
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
        )
    }

    var error by mutableStateOf<String?>(null)
        private set

    var savedMessage by mutableStateOf<String?>(null)

    fun toggleSport(title: String) {
        if (title in sports) sports.remove(title) else sports.add(title)
        error = null
    }

    fun onNameChange(value: String) {
        name = value
        error = null
    }

    fun save() {
        val ordered = SportType.entries.map { it.title }.filter { it in sports }
        error = auth.updateProfile(name, ordered.joinToString(", "))
        if (error == null) savedMessage = "Профиль сохранён"
    }

    /** Копирует выбранное фото во внутреннее хранилище и сохраняет путь в профиле. */
    fun setPhoto(context: Context, uri: Uri) {
        val appContext = context.applicationContext
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(appContext.filesDir, "avatar_${System.currentTimeMillis()}.jpg")
                appContext.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                if (file.exists() && file.length() > 0) auth.updateAvatar(file.absolutePath)
            } catch (e: Exception) {
                savedMessage = "Не удалось загрузить фото"
            }
        }
    }
}

/** Экран «Аккаунт и безопасность». */
class SettingsViewModel(
    private val auth: AuthRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    var newPassword by mutableStateOf("")
    var newLogin by mutableStateOf("")

    var error by mutableStateOf<String?>(null)
        private set

    val darkTheme = prefs.darkTheme

    fun setDarkTheme(value: Boolean) = prefs.setDarkTheme(value)

    fun clearError() {
        error = null
    }

    /** true — изменения применены, можно возвращаться на карту. */
    fun save(): Boolean {
        error = auth.updateCredentials(newLogin, newPassword)
        return error == null
    }

    fun logout() = auth.logout()
}
