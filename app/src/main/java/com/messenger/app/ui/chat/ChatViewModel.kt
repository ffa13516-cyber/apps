package com.messenger.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.app.data.model.Message
import com.messenger.app.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val chatRepository = ChatRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState

    fun observeMessages(chatId: String) {
        viewModelScope.launch {
            chatRepository.observeMessages(chatId).collect { _messages.value = it }
        }
    }

    fun sendMessage(chatId: String, senderId: String, senderName: String, text: String) {
        if (text.isBlank()) return
        _sendState.value = SendState.Sending
        viewModelScope.launch {
            val message = Message(
                senderId = senderId,
                senderName = senderName,
                text = text.trim(),
                timestamp = System.currentTimeMillis()
            )
            val result = chatRepository.sendMessage(chatId, message)
            _sendState.value = if (result.isSuccess) SendState.Sent else SendState.Error(result.exceptionOrNull()?.message ?: "Failed")
        }
    }
}

sealed class SendState {
    object Idle : SendState()
    object Sending : SendState()
    object Sent : SendState()
    data class Error(val message: String) : SendState()
}
