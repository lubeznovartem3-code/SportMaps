package com.sportmaps.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sportmaps.app.data.Arena
import com.sportmaps.app.data.AuthRepository
import com.sportmaps.app.data.GroundsRepository
import com.sportmaps.app.data.Lobby
import kotlinx.coroutines.flow.StateFlow

/** Текущий пользователь для бокового меню. */
class SessionViewModel(private val auth: AuthRepository) : ViewModel() {
    val user = auth.currentUser
}

/** Экран «Карта площадок». */
class MapViewModel(
    private val grounds: GroundsRepository,
    private val auth: AuthRepository
) : ViewModel() {

    val arenas: List<Arena> = grounds.arenas

    var selectedId by mutableIntStateOf(arenas.first().id)
        private set

    var query by mutableStateOf("")
        private set

    val selected: Arena get() = arenas.first { it.id == selectedId }

    val visibleArenas: List<Arena>
        get() {
            val q = query.trim()
            if (q.isEmpty()) return arenas
            return arenas.filter {
                it.name.contains(q, ignoreCase = true) ||
                    it.address.contains(q, ignoreCase = true) ||
                    it.sport.title.contains(q, ignoreCase = true)
            }
        }

    fun select(id: Int) {
        selectedId = id
    }

    fun onQueryChange(value: String) {
        query = value
        val matches = visibleArenas
        if (matches.isNotEmpty() && matches.none { it.id == selectedId }) {
            selectedId = matches.first().id
        }
    }

    /** Кнопка «Играть». */
    fun play() {
        grounds.joinLobby(selected, auth.currentUser.value?.displayName ?: "Игрок")
    }
}

/** Экран «Сбор состава». */
class LobbyViewModel(private val grounds: GroundsRepository) : ViewModel() {
    val lobby: StateFlow<Lobby?> = grounds.lobby

    fun leave() {
        grounds.leaveLobby()
    }
}
