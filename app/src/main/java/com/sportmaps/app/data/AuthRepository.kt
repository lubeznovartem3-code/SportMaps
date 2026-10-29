package com.sportmaps.app.data

import android.util.Patterns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Репозиторий авторизации (имитация сервера).
 * Методы возвращают null при успехе или текст ошибки на русском.
 */
class AuthRepository(private val prefs: UserPreferences) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Демо-аккаунт, чтобы приложение можно было проверить сразу после установки
        if (prefs.loadUsers().isEmpty()) {
            prefs.saveUsers(
                listOf(User("alex_sport", "123456", "Александр", "Баскетбол, Футбол"))
            )
        }
        val saved = prefs.sessionLogin
        _currentUser.value = prefs.loadUsers().firstOrNull { it.login == saved }
    }

    fun register(identifier: String, password: String, repeat: String): String? {
        val login = normalize(identifier)
        if (login.isEmpty() || password.isEmpty() || repeat.isEmpty()) return "Заполните все поля"
        if (!isEmailOrPhone(login)) return "Введите корректный email или номер телефона"
        if (password.length < MIN_PASSWORD) return "Пароль должен содержать не менее $MIN_PASSWORD символов"
        if (password != repeat) return "Пароли не совпадают"
        val users = prefs.loadUsers()
        if (users.any { it.login.equals(login, ignoreCase = true) }) {
            return "Аккаунт с таким логином уже существует"
        }
        val name = login.substringBefore('@').replaceFirstChar { it.uppercase() }
        val user = User(login, password, name, "")
        prefs.saveUsers(users + user)
        prefs.sessionLogin = user.login
        _currentUser.value = user
        return null
    }

    fun login(identifier: String, password: String): String? {
        val login = normalize(identifier)
        if (login.isEmpty() || password.isEmpty()) return "Введите логин и пароль"
        val user = prefs.loadUsers().firstOrNull {
            it.login.equals(login, ignoreCase = true) && it.password == password
        } ?: return "Неверный логин или пароль"
        prefs.sessionLogin = user.login
        _currentUser.value = user
        return null
    }

    fun logout() {
        prefs.sessionLogin = null
        _currentUser.value = null
    }

    fun updateProfile(displayName: String, favoriteSports: String): String? {
        val current = _currentUser.value ?: return "Вы не авторизованы"
        if (displayName.isBlank()) return "Введите отображаемое имя"
        if (favoriteSports.isBlank()) return "Выберите хотя бы один любимый вид спорта"
        replaceUser(current, current.copy(displayName = displayName.trim(), favoriteSports = favoriteSports))
        return null
    }

    fun updateCredentials(newLogin: String, newPassword: String): String? {
        val current = _currentUser.value ?: return "Вы не авторизованы"
        val login = normalize(newLogin)
        if (login.isEmpty() && newPassword.isEmpty()) return null
        if (newPassword.isNotEmpty() && newPassword.length < MIN_PASSWORD) {
            return "Пароль должен содержать не менее $MIN_PASSWORD символов"
        }
        if (login.isNotEmpty()) {
            if (login.length < 3) return "Логин должен содержать не менее 3 символов"
            val taken = prefs.loadUsers().any {
                it.login.equals(login, ignoreCase = true) && it.login != current.login
            }
            if (taken) return "Такой логин уже занят"
        }
        replaceUser(
            current,
            current.copy(
                login = if (login.isEmpty()) current.login else login,
                password = if (newPassword.isEmpty()) current.password else newPassword
            )
        )
        return null
    }

    fun updateAvatar(path: String) {
        val current = _currentUser.value ?: return
        replaceUser(current, current.copy(avatarPath = path))
    }

    private fun replaceUser(old: User, new: User) {
        val users = prefs.loadUsers().map { if (it.login == old.login) new else it }
        prefs.saveUsers(users)
        if (prefs.sessionLogin == old.login) prefs.sessionLogin = new.login
        _currentUser.value = new
    }

    private fun normalize(raw: String): String {
        val trimmed = raw.trim()
        val digits = trimmed.replace(Regex("[\\s\\-()]"), "")
        return if (PHONE.matches(digits)) digits else trimmed
    }

    private fun isEmailOrPhone(value: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(value).matches() || PHONE.matches(value)

    private companion object {
        const val MIN_PASSWORD = 6
        val PHONE = Regex("^\\+?[0-9]{10,15}$")
    }
}
