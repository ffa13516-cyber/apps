package com.messenger.app.data.model

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: MessageType = MessageType.TEXT
) {
    fun toMap(): Map<String, Any> = mapOf(
        "messageId" to messageId,
        "senderId" to senderId,
        "senderName" to senderName,
        "text" to text,
        "timestamp" to timestamp,
        "isRead" to isRead,
        "type" to type.name
    )
}

enum class MessageType { TEXT, IMAGE, FILE }
