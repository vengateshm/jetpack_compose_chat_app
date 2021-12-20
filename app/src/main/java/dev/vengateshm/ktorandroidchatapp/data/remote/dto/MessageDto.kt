package dev.vengateshm.ktorandroidchatapp.data.remote.dto

import dev.vengateshm.ktorandroidchatapp.domain.model.Message
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.*

@Serializable
data class MessageDto(
    val text: String,
    val timestamp: Long,
    val username: String,
    val id: String
) {
    fun toMessage() = Message(
        text = text,
        username = username,
        formattedTime = DateFormat.getDateInstance(DateFormat.DEFAULT).format(Date(timestamp))
    )
}
