package com.messenger.app.data.model

data class Channel(
    val channelId: String = "",
    val name: String = "",
    val description: String = "",
    val adminId: String = "",
    val subscribers: List<String> = emptyList(),
    val avatarUrl: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "channelId" to channelId,
        "name" to name,
        "description" to description,
        "adminId" to adminId,
        "subscribers" to subscribers,
        "avatarUrl" to avatarUrl,
        "lastMessage" to lastMessage,
        "lastMessageTime" to lastMessageTime,
        "createdAt" to createdAt
    )
}
