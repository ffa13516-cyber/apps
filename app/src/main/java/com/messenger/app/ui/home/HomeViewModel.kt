package com.messenger.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.app.data.model.Channel
import com.messenger.app.data.model.Chat
import com.messenger.app.data.model.Group
import com.messenger.app.data.model.User
import com.messenger.app.data.repository.ChannelRepository
import com.messenger.app.data.repository.ChatRepository
import com.messenger.app.data.repository.GroupRepository
import com.messenger.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val chatRepository = ChatRepository()
    private val groupRepository = GroupRepository()
    private val channelRepository = ChannelRepository()
    private val userRepository = UserRepository()

    private val _chats = MutableStateFlow<List<Pair<Chat, User?>>>(emptyList())
    val chats: StateFlow<List<Pair<Chat, User?>>> = _chats

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels

    fun loadData(uid: String) {
        loadChats(uid)
        loadGroups(uid)
        loadChannels(uid)
    }

    private fun loadChats(uid: String) {
        viewModelScope.launch {
            chatRepository.observeUserChats(uid).collect { chatList ->
                val enriched = chatList.map { chat ->
                    val otherUid = chat.participants.firstOrNull { it != uid } ?: ""
                    val user = userRepository.getUserById(otherUid).getOrNull()
                    Pair(chat, user)
                }
                _chats.value = enriched
            }
        }
    }

    private fun loadGroups(uid: String) {
        viewModelScope.launch {
            groupRepository.observeUserGroups(uid).collect { _groups.value = it }
        }
    }

    private fun loadChannels(uid: String) {
        viewModelScope.launch {
            channelRepository.observeAllChannels(uid).collect { _channels.value = it }
        }
    }
}
