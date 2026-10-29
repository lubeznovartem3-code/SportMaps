package com.sportmaps.app.ui.navigation

/** Маршруты навигации приложения. */
object Routes {
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val MAP = "map"
    const val LOBBY = "lobby"
    const val FRIENDS = "friends/{invite}"
    const val CHAT = "chat/{friendId}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    /** invite = true — экран открыт из лобби для приглашения друзей. */
    fun friends(invite: Boolean) = "friends/$invite"
    fun chat(friendId: String) = "chat/$friendId"
}
