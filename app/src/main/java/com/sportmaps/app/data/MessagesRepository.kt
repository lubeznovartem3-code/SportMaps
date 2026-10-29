package com.sportmaps.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Репозиторий друзей и личных сообщений (в памяти, с готовыми диалогами). */
class MessagesRepository {

    private val _friends = MutableStateFlow(
        listOf(
            Friend("dmitry", "Дмитрий", true, "Собирает состав на баскетбол"),
            Friend("artem", "Артем", true, "Играет в футбол на Лужниках"),
            Friend("ivan", "Иван", false, "Был в сети 2 часа назад")
        )
    )
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private var nextId = 100L

    private val _messages = MutableStateFlow(
        mapOf(
            "dmitry" to listOf(
                ChatMessage(1, "Привет! Пойдешь сегодня на Хамовники? Собираем состав на баскетбол к 18:00.", false),
                ChatMessage(2, "Привет! Да, с радостью. Сколько уже людей набралось по приложению?", true),
                ChatMessage(3, "Пока 6 человек отметилось, как раз еще четверых до полноценного матча не хватает.", false),
                ChatMessage(4, "Отлично, буду вовремя! Мяч брать?", true)
            ),
            "artem" to listOf(
                ChatMessage(5, "Привет! Сегодня играем в футбол на Лужниках, присоединяйся.", false),
                ChatMessage(6, "Привет! Во сколько начало?", true),
                ChatMessage(7, "В 19:00, у главного входа.", false)
            ),
            "ivan" to listOf(
                ChatMessage(8, "Давно не виделись! Когда снова на площадку?", false)
            )
        )
    )
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    fun send(friendId: String, text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val message = ChatMessage(nextId++, clean, true)
        _messages.update { all -> all + (friendId to (all[friendId].orEmpty() + message)) }
    }

    /** Добавляет друга по имени; возвращает текст ошибки или null. */
    fun addFriend(name: String): String? {
        val clean = name.trim()
        if (clean.isEmpty()) return "Введите имя или логин"
        if (_friends.value.any { it.name.equals(clean, ignoreCase = true) }) {
            return "Этот пользователь уже в списке друзей"
        }
        val friend = Friend("u" + System.nanoTime(), clean, false, "Недавно добавлен")
        _friends.update { it + friend }
        return null
    }
}
