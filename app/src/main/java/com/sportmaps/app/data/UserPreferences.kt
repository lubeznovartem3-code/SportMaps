package com.sportmaps.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Локальное хранилище: список аккаунтов, текущая сессия и тема оформления.
 * Всё лежит в SharedPreferences, поэтому данные сохраняются между запусками.
 */
class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("sportmaps_prefs", Context.MODE_PRIVATE)

    private val _darkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK, false))
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    fun setDarkTheme(value: Boolean) {
        prefs.edit().putBoolean(KEY_DARK, value).apply()
        _darkTheme.value = value
    }

    var sessionLogin: String?
        get() = prefs.getString(KEY_SESSION, null)
        set(value) {
            prefs.edit().putString(KEY_SESSION, value).apply()
        }

    fun loadUsers(): List<User> {
        val raw = prefs.getString(KEY_USERS, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                User(
                    login = o.getString("login"),
                    password = o.getString("password"),
                    displayName = o.getString("displayName"),
                    favoriteSports = o.optString("favoriteSports", ""),
                    avatarPath = o.optString("avatarPath", "").ifEmpty { null }
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveUsers(users: List<User>) {
        val array = JSONArray()
        users.forEach { u ->
            array.put(
                JSONObject()
                    .put("login", u.login)
                    .put("password", u.password)
                    .put("displayName", u.displayName)
                    .put("favoriteSports", u.favoriteSports)
                    .put("avatarPath", u.avatarPath ?: "")
            )
        }
        prefs.edit().putString(KEY_USERS, array.toString()).apply()
    }

    private companion object {
        const val KEY_DARK = "dark_theme"
        const val KEY_SESSION = "session_login"
        const val KEY_USERS = "users"
    }
}
