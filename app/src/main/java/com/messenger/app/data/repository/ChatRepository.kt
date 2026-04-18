package com.messenger.app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.messenger.app.data.firebase.FirebaseManager
import com.messenger.app.data.model.Chat
import com.messenger.app.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val chatsRef = FirebaseManager.chatsRef
    private val messagesRef = FirebaseManager.messagesRef

    fun getChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }

    suspend fun getOrCreateChat(myUid: String, otherUid: String): Result<Chat> = runCatching {
        val chatId = getChatId(myUid, otherUid)
        val snapshot = chatsRef.child(chatId).get().await()
        if (snapshot.exists()) {
            snapshot.getValue(Chat::class.java) ?: Chat(chatId = chatId, participants = listOf(myUid, otherUid))
        } else {
            val chat = Chat(chatId = chatId, participants = listOf(myUid, otherUid))
            chatsRef.child(chatId).setValue(chat.toMap()).await()
            chat
        }
    }

    suspend fun sendMessage(chatId: String, message: Message): Result<Unit> = runCatching {
        val msgRef = messagesRef.child(chatId).push()
        val msgWithId = message.copy(messageId = msgRef.key ?: "")
        msgRef.setValue(msgWithId.toMap()).await()

        // Update chat's last message
        chatsRef.child(chatId).updateChildren(
            mapOf(
                "lastMessage" to message.text,
                "lastMessageTime" to message.timestamp,
                "lastMessageSenderId" to message.senderId
            )
        ).await()
    }

    fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { child ->
                    child.getValue(Message::class.java)
                }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        messagesRef.child(chatId).addValueEventListener(listener)
        awaitClose { messagesRef.child(chatId).removeEventListener(listener) }
    }

    fun observeUserChats(uid: String): Flow<List<Chat>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = snapshot.children
                    .mapNotNull { it.getValue(Chat::class.java) }
                    .filter { it.participants.contains(uid) }
                    .sortedByDescending { it.lastMessageTime }
                trySend(chats)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        chatsRef.addValueEventListener(listener)
        awaitClose { chatsRef.removeEventListener(listener) }
    }
}
