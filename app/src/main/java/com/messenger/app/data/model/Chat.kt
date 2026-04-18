package com.messenger.app.data.model

data class Chat(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val lastMessageSenderId: String = "",
    val unreadCount: Int = 0
) {
    fun toMap(): Map<String, Any> = mapOf(
        "chatId" to chatId,
        "participants" to participants,
        "lastMessage" to lastMessage,
        "lastMessageTime" to lastMessageTime,
        "lastMessageSenderId" to lastMessageSenderId,
        "unreadCount" to unreadCount
    )
}
