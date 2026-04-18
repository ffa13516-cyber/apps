package com.messenger.app.data.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {

    val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
        }
    }

    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Realtime Database References
    val usersRef get() = database.getReference("users")
    val chatsRef get() = database.getReference("chats")
    val messagesRef get() = database.getReference("messages")
    val groupsRef get() = database.getReference("groups")
    val groupMessagesRef get() = database.getReference("groupMessages")
    val channelsRef get() = database.getReference("channels")
    val channelMessagesRef get() = database.getReference("channelMessages")

    fun getChatMessagesRef(chatId: String) = messagesRef.child(chatId)
    fun getGroupMessagesRef(groupId: String) = groupMessagesRef.child(groupId)
    fun getChannelMessagesRef(channelId: String) = channelMessagesRef.child(channelId)
}
