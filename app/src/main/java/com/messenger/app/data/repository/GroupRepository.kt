package com.messenger.app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.messenger.app.data.firebase.FirebaseManager
import com.messenger.app.data.model.Group
import com.messenger.app.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GroupRepository {

    private val groupsRef = FirebaseManager.groupsRef
    private val groupMessagesRef = FirebaseManager.groupMessagesRef

    suspend fun createGroup(group: Group): Result<Group> = runCatching {
        val ref = groupsRef.push()
        val groupWithId = group.copy(groupId = ref.key ?: "")
        ref.setValue(groupWithId.toMap()).await()
        groupWithId
    }

    suspend fun getGroup(groupId: String): Result<Group?> = runCatching {
        val snapshot = groupsRef.child(groupId).get().await()
        snapshot.getValue(Group::class.java)
    }

    suspend fun sendMessage(groupId: String, message: Message): Result<Unit> = runCatching {
        val msgRef = groupMessagesRef.child(groupId).push()
        val msgWithId = message.copy(messageId = msgRef.key ?: "")
        msgRef.setValue(msgWithId.toMap()).await()

        groupsRef.child(groupId).updateChildren(
            mapOf(
                "lastMessage" to message.text,
                "lastMessageTime" to message.timestamp
            )
        ).await()
    }

    fun observeMessages(groupId: String): Flow<List<Message>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        groupMessagesRef.child(groupId).addValueEventListener(listener)
        awaitClose { groupMessagesRef.child(groupId).removeEventListener(listener) }
    }

    fun observeUserGroups(uid: String): Flow<List<Group>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groups = snapshot.children
                    .mapNotNull { it.getValue(Group::class.java) }
                    .filter { it.members.contains(uid) }
                    .sortedByDescending { it.lastMessageTime }
                trySend(groups)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        groupsRef.addValueEventListener(listener)
        awaitClose { groupsRef.removeEventListener(listener) }
    }

    suspend fun addMember(groupId: String, uid: String): Result<Unit> = runCatching {
        val group = getGroup(groupId).getOrThrow() ?: return@runCatching
        val newMembers = group.members.toMutableList().apply { if (!contains(uid)) add(uid) }
        groupsRef.child(groupId).child("members").setValue(newMembers).await()
    }
}
