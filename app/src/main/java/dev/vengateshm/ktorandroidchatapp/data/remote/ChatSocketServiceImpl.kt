package dev.vengateshm.ktorandroidchatapp.data.remote

import dev.vengateshm.ktorandroidchatapp.data.remote.dto.MessageDto
import dev.vengateshm.ktorandroidchatapp.domain.model.Message
import dev.vengateshm.ktorandroidchatapp.util.Resource
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
) : ChatSocketService {

    private var socket: WebSocketSession? = null
    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.Endpoints.ChatSocket.url}?username=$username")
            }
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else Resource.Error(message = "Couldn't establish a connection.")
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(message = e.localizedMessage ?: "Unknown Error")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow {}
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}