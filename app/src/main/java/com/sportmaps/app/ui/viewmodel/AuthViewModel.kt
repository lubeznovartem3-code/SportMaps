package com.sportmaps.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sportmaps.app.data.AuthRepository

/** Регистрация и вход. */
class AuthViewModel(private val auth: AuthRepository) : ViewModel() {

    var error by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        error = null
    }

    /** true — аккаунт создан и пользователь вошёл. */
    fun register(identifier: String, password: String, repeat: String): Boolean {
        error = auth.register(identifier, password, repeat)
        return error == null
    }

    /** true — вход выполнен. */
    fun login(identifier: String, password: String): Boolean {
        error = auth.login(identifier, password)
        return error == null
    }
}
