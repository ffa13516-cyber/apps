package com.messenger.app.data.model

data class Group(
    val groupId: String = "",
    val name: String = "",
    val description: String = "",
    val adminId: String = "",
    val members: List<String> = emptyList(),
    val avatarUrl: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "groupId" to groupId,
        "name" to name,
        "description" to description,
        "adminId" to adminId,
        "members" to members,
        "avatarUrl" to avatarUrl,
        "lastMessage" to lastMessage,
        "lastMessageTime" to lastMessageTime,
        "createdAt" to createdAt
    )
}
