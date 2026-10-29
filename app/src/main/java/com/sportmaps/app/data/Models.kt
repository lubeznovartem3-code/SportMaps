package com.sportmaps.app.data

/** Пользователь приложения. */
data class User(
    val login: String,
    val password: String,
    val displayName: String,
    val favoriteSports: String,
    val avatarPath: String? = null
) {
    /** Короткое имя вида «@alex_sport». */
    val handle: String get() = "@" + login.substringBefore('@')
}

/** Виды спорта. */
enum class SportType(val title: String) {
    BASKETBALL("Баскетбол"),
    FOOTBALL("Футбол"),
    VOLLEYBALL("Волейбол")
}

/** Спортивная площадка (арена). mapX / mapY — положение маркера на макете карты (0..1). */
data class Arena(
    val id: Int,
    val name: String,
    val address: String,
    val sport: SportType,
    val players: Int,
    val maxPlayers: Int,
    val hours: String,
    val cost: String,
    val mapX: Float,
    val mapY: Float,
    val lobbySize: Int
)

/** Игрок в лобби. */
data class LobbyPlayer(val name: String, val status: String)

/** Сбор состава на выбранной площадке. */
data class Lobby(
    val arena: Arena,
    val time: String,
    val organizer: String,
    val players: List<LobbyPlayer>,
    val capacity: Int
)

/** Друг пользователя. */
data class Friend(
    val id: String,
    val name: String,
    val online: Boolean,
    val status: String
)

/** Сообщение в чате. */
data class ChatMessage(val id: Long, val text: String, val fromMe: Boolean)
