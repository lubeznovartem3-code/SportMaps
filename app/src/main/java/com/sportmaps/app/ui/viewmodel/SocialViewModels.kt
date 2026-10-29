package com.sportmaps.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sportmaps.app.data.MessagesRepository

/** Список друзей. */
class FriendsViewModel(private val repo: MessagesRepository) : ViewModel() {
    val friends = repo.friends

    var query by mutableStateOf("")

    fun addFriend(name: String): String? = repo.addFriend(name)
}

/** Личный чат. */
class ChatViewModel(private val repo: MessagesRepository) : ViewModel() {
    val friends = repo.friends
    val messages = repo.messages

    fun send(friendId: String, text: String) = repo.send(friendId, text)
}
