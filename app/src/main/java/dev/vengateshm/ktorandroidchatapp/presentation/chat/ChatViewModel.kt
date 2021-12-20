package dev.vengateshm.ktorandroidchatapp.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vengateshm.ktorandroidchatapp.data.remote.ChatSocketService
import dev.vengateshm.ktorandroidchatapp.data.remote.MessageService
import dev.vengateshm.ktorandroidchatapp.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle // Restore view model state on process death
) : ViewModel() {

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _chatState = mutableStateOf(ChatState())
    val chatState: State<ChatState> = _chatState

    private val _onToastEvent = MutableSharedFlow<String>()
    val onToastEvent = _onToastEvent.asSharedFlow()

    fun connectToChat() {
        getAllMessages()
        savedStateHandle.get<String>("username")?.let { username ->
            viewModelScope.launch {
                val result = chatSocketService.initSession(username)
                when (result) {
                    is Resource.Success -> {
                        chatSocketService.observeMessages()
                            .onEach { message ->
                                val newList = chatState.value.messages.toMutableList().apply {
                                    add(0, message)
                                }
                                _chatState.value = chatState.value.copy(
                                    messages = newList
                                )
                            }
                            .launchIn(this)
                    }
                    is Resource.Error -> {
                        _onToastEvent.emit(result.message ?: "Unknown error")
                    }
                    is Resource.Loading -> TODO()
                }
            }
        }
    }

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotBlank()) {
                chatSocketService.sendMessage(messageText.value)
            }
        }
    }

    fun getAllMessages() {
        viewModelScope.launch {
            _chatState.value = chatState.value.copy(isLoading = true)
            val result = messageService.getAllMessages()
            _chatState.value = chatState.value.copy(
                isLoading = false,
                messages = result
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }
}