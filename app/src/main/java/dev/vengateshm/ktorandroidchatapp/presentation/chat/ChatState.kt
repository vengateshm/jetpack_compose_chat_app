package dev.vengateshm.ktorandroidchatapp.presentation.chat

import dev.vengateshm.ktorandroidchatapp.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)
