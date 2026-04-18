package com.messenger.app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.messenger.app.data.firebase.FirebaseManager
import com.messenger.app.data.model.Channel
import com.messenger.app.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChannelRepository {

    private val channelsRef = FirebaseManager.channelsRef
    private val channelMessagesRef = FirebaseManager.channelMessagesRef

    suspend fun createChannel(channel: Channel): Result<Channel> = runCatching {
        val ref = channelsRef.push()
        val channelWithId = channel.copy(channelId = ref.key ?: "")
        ref.setValue(channelWithId.toMap()).await()
        channelWithId
    }

    suspend fun getChannel(channelId: String): Result<Channel?> = runCatching {
        val snapshot = channelsRef.child(channelId).get().await()
        snapshot.getValue(Channel::class.java)
    }

    // Only admin can send messages to channel
    suspend fun sendMessage(channelId: String, message: Message, adminId: String): Result<Unit> = runCatching {
        val channel = getChannel(channelId).getOrThrow()
            ?: throw IllegalStateException("Channel not found")
        if (channel.adminId != adminId) throw SecurityException("Only admin can post to channel")

        val msgRef = channelMessagesRef.child(channelId).push()
        val msgWithId = message.copy(messageId = msgRef.key ?: "")
        msgRef.setValue(msgWithId.toMap()).await()

        channelsRef.child(channelId).updateChildren(
            mapOf(
                "lastMessage" to message.text,
                "lastMessageTime" to message.timestamp
            )
        ).await()
    }

    fun observeMessages(channelId: String): Flow<List<Message>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        channelMessagesRef.child(channelId).addValueEventListener(listener)
        awaitClose { channelMessagesRef.child(channelId).removeEventListener(listener) }
    }

    fun observeAllChannels(uid: String): Flow<List<Channel>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val channels = snapshot.children
                    .mapNotNull { it.getValue(Channel::class.java) }
                    .filter { it.subscribers.contains(uid) || it.adminId == uid }
                    .sortedByDescending { it.lastMessageTime }
                trySend(channels)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        channelsRef.addValueEventListener(listener)
        awaitClose { channelsRef.removeEventListener(listener) }
    }

    suspend fun subscribeToChannel(channelId: String, uid: String): Result<Unit> = runCatching {
        val channel = getChannel(channelId).getOrThrow() ?: return@runCatching
        val newSubs = channel.subscribers.toMutableList().apply { if (!contains(uid)) add(uid) }
        channelsRef.child(channelId).child("subscribers").setValue(newSubs).await()
    }
}
