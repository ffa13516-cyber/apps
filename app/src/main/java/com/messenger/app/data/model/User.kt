package com.messenger.app.data.model

data class User(
    val uid: String = "",
    val phoneNumber: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val lastSeen: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false
) {
    fun toMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "phoneNumber" to phoneNumber,
        "displayName" to displayName,
        "avatarUrl" to avatarUrl,
        "lastSeen" to lastSeen,
        "isOnline" to isOnline
    )
}
