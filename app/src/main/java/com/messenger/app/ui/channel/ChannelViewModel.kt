package com.messenger.app.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.app.data.model.Channel
import com.messenger.app.data.model.Message
import com.messenger.app.data.repository.ChannelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChannelViewModel : ViewModel() {

    private val channelRepository = ChannelRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _sendState = MutableStateFlow<ChannelSendState>(ChannelSendState.Idle)
    val sendState: StateFlow<ChannelSendState> = _sendState

    private val _createState = MutableStateFlow<ChannelCreateState>(ChannelCreateState.Idle)
    val createState: StateFlow<ChannelCreateState> = _createState

    fun observeMessages(channelId: String) {
        viewModelScope.launch {
            channelRepository.observeMessages(channelId).collect { _messages.value = it }
        }
    }

    fun sendMessage(channelId: String, senderId: String, senderName: String, text: String, adminId: String) {
        if (text.isBlank()) return
        if (senderId != adminId) {
            _sendState.value = ChannelSendState.Error("Only admin can post")
            return
        }
        _sendState.value = ChannelSendState.Sending
        viewModelScope.launch {
            val message = Message(
                senderId = senderId,
                senderName = senderName,
                text = text.trim(),
                timestamp = System.currentTimeMillis()
            )
            val result = channelRepository.sendMessage(channelId, message, adminId)
            _sendState.value = if (result.isSuccess) ChannelSendState.Sent
            else ChannelSendState.Error(result.exceptionOrNull()?.message ?: "Failed")
        }
    }

    fun createChannel(name: String, description: String, adminId: String) {
        if (name.isBlank()) {
            _createState.value = ChannelCreateState.Error("Channel name cannot be empty")
            return
        }
        _createState.value = ChannelCreateState.Loading
        viewModelScope.launch {
            val channel = Channel(
                name = name,
                description = description,
                adminId = adminId,
                subscribers = listOf(adminId)
            )
            val result = channelRepository.createChannel(channel)
            _createState.value = if (result.isSuccess) ChannelCreateState.Success(result.getOrNull()!!)
            else ChannelCreateState.Error(result.exceptionOrNull()?.message ?: "Failed")
        }
    }
}

sealed class ChannelSendState {
    object Idle : ChannelSendState()
    object Sending : ChannelSendState()
    object Sent : ChannelSendState()
    data class Error(val message: String) : ChannelSendState()
}

sealed class ChannelCreateState {
    object Idle : ChannelCreateState()
    object Loading : ChannelCreateState()
    data class Success(val channel: Channel) : ChannelCreateState()
    data class Error(val message: String) : ChannelCreateState()
}
