package com.sportmaps.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Репозиторий площадок и лобби (данные-заглушки по Москве). */
class GroundsRepository {

    val arenas: List<Arena> = listOf(
        Arena(
            id = 1,
            name = "Хамовники Арена",
            address = "Москва, ул. Лужники, 24",
            sport = SportType.BASKETBALL,
            players = 6, maxPlayers = 10,
            hours = "08:00 - 23:00",
            cost = "Бесплатно",
            mapX = 0.50f, mapY = 0.50f,
            lobbySize = 6
        ),
        Arena(
            id = 2,
            name = "Лужники: футбольное поле",
            address = "Москва, Лужнецкая наб., 24",
            sport = SportType.FOOTBALL,
            players = 14, maxPlayers = 22,
            hours = "09:00 - 22:00",
            cost = "500 ₽ / час",
            mapX = 0.24f, mapY = 0.28f,
            lobbySize = 10
        ),
        Arena(
            id = 3,
            name = "Парк Горького: волейбол",
            address = "Москва, Крымский Вал, 9",
            sport = SportType.VOLLEYBALL,
            players = 8, maxPlayers = 12,
            hours = "10:00 - 21:00",
            cost = "Бесплатно",
            mapX = 0.78f, mapY = 0.26f,
            lobbySize = 6
        )
    )

    private val _lobby = MutableStateFlow<Lobby?>(null)
    val lobby: StateFlow<Lobby?> = _lobby.asStateFlow()

    /** Нажатие «Играть»: открывает (или продолжает) сбор состава на выбранной площадке. */
    fun joinLobby(arena: Arena, userName: String) {
        if (_lobby.value?.arena?.id == arena.id) return
        _lobby.value = Lobby(
            arena = arena,
            time = "Сегодня, 18:00",
            organizer = userName,
            players = listOf(
                LobbyPlayer(userName, "Создатель"),
                LobbyPlayer("Дмитрий", "Готов"),
                LobbyPlayer("Артем К.", "Готов")
            ),
            capacity = arena.lobbySize
        )
    }

    /** «Покинуть лобби». */
    fun leaveLobby() {
        _lobby.value = null
    }
}
