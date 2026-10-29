package com.sportmaps.app.data

import android.content.Context

/** Простой контейнер зависимостей: хранит единственные экземпляры репозиториев. */
class AppContainer(context: Context) {
    val prefs = UserPreferences(context)
    val auth = AuthRepository(prefs)
    val grounds = GroundsRepository()
    val messages = MessagesRepository()
}
