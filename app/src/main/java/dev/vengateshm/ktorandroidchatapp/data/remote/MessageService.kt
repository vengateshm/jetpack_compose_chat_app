package dev.vengateshm.ktorandroidchatapp.data.remote

import dev.vengateshm.ktorandroidchatapp.domain.model.Message

interface MessageService {
    suspend fun getAllMessages(): List<Message>

    companion object {
        const val BASE_URL = "http://192.168.43.177:8082"
    }

    sealed class Endpoints(val url: String) {
        object GetAllMessages : Endpoints("$BASE_URL/messages")
    }
}