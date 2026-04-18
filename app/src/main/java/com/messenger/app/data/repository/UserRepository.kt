package com.messenger.app.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.messenger.app.data.firebase.FirebaseManager
import com.messenger.app.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val usersRef = FirebaseManager.usersRef

    suspend fun saveUser(user: User): Result<Unit> = runCatching {
        usersRef.child(user.uid).setValue(user.toMap()).await()
    }

    suspend fun getUserById(uid: String): Result<User?> = runCatching {
        val snapshot = usersRef.child(uid).get().await()
        snapshot.getValue(User::class.java)
    }

    suspend fun getUserByPhone(phoneNumber: String): Result<User?> = runCatching {
        val snapshot = usersRef
            .orderByChild("phoneNumber")
            .equalTo(phoneNumber)
            .get()
            .await()
        snapshot.children.firstOrNull()?.getValue(User::class.java)
    }

    suspend fun getUserByUsername(username: String): Result<User?> = runCatching {
        val snapshot = usersRef
            .orderByChild("username")
            .equalTo(username.lowercase())
            .get()
            .await()
        snapshot.children.firstOrNull()?.getValue(User::class.java)
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        val result = getUserByUsername(username)
        return result.getOrNull() == null
    }

    suspend fun getAllUsers(): Result<List<User>> = runCatching {
        val snapshot = usersRef.get().await()
        snapshot.children.mapNotNull { it.getValue(User::class.java) }
    }

    fun observeUser(uid: String): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(User::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        usersRef.child(uid).addValueEventListener(listener)
        awaitClose { usersRef.child(uid).removeEventListener(listener) }
    }

    suspend fun updateOnlineStatus(uid: String, isOnline: Boolean) {
        usersRef.child(uid).child("isOnline").setValue(isOnline).await()
        usersRef.child(uid).child("lastSeen").setValue(System.currentTimeMillis()).await()
    }
}
