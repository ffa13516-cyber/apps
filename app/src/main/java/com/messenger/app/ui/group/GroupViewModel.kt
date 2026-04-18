package com.messenger.app.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.app.data.model.Group
import com.messenger.app.data.model.Message
import com.messenger.app.data.repository.GroupRepository
import com.messenger.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {

    private val groupRepository = GroupRepository()
    private val userRepository = UserRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _sendState = MutableStateFlow<GroupSendState>(GroupSendState.Idle)
    val sendState: StateFlow<GroupSendState> = _sendState

    private val _createState = MutableStateFlow<GroupCreateState>(GroupCreateState.Idle)
    val createState: StateFlow<GroupCreateState> = _createState

    private val _allUsers = MutableStateFlow<List<com.messenger.app.data.model.User>>(emptyList())
    val allUsers: StateFlow<List<com.messenger.app.data.model.User>> = _allUsers

    fun observeMessages(groupId: String) {
        viewModelScope.launch {
            groupRepository.observeMessages(groupId).collect { _messages.value = it }
        }
    }

    fun sendMessage(groupId: String, senderId: String, senderName: String, text: String) {
        if (text.isBlank()) return
        _sendState.value = GroupSendState.Sending
        viewModelScope.launch {
            val message = Message(
                senderId = senderId,
                senderName = senderName,
                text = text.trim(),
                timestamp = System.currentTimeMillis()
            )
            val result = groupRepository.sendMessage(groupId, message)
            _sendState.value = if (result.isSuccess) GroupSendState.Sent
            else GroupSendState.Error(result.exceptionOrNull()?.message ?: "Failed")
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().getOrNull()?.let { _allUsers.value = it }
        }
    }

    fun createGroup(name: String, description: String, adminId: String, memberUids: List<String>) {
        if (name.isBlank()) {
            _createState.value = GroupCreateState.Error("Group name cannot be empty")
            return
        }
        _createState.value = GroupCreateState.Loading
        viewModelScope.launch {
            val allMembers = (memberUids + adminId).distinct()
            val group = Group(name = name, description = description, adminId = adminId, members = allMembers)
            val result = groupRepository.createGroup(group)
            _createState.value = if (result.isSuccess) GroupCreateState.Success(result.getOrNull()!!)
            else GroupCreateState.Error(result.exceptionOrNull()?.message ?: "Failed")
        }
    }
}

sealed class GroupSendState {
    object Idle : GroupSendState()
    object Sending : GroupSendState()
    object Sent : GroupSendState()
    data class Error(val message: String) : GroupSendState()
}

sealed class GroupCreateState {
    object Idle : GroupCreateState()
    object Loading : GroupCreateState()
    data class Success(val group: Group) : GroupCreateState()
    data class Error(val message: String) : GroupCreateState()
}
