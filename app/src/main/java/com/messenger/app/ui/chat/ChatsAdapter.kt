package com.messenger.app.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.R
import com.messenger.app.data.model.Chat
import com.messenger.app.data.model.User
import com.messenger.app.databinding.ItemChatBinding
import com.messenger.app.utils.toInitials
import com.messenger.app.utils.toTimeString

class ChatsAdapter(
    private val myUid: String,
    private val onClick: (Chat, User?) -> Unit
) : ListAdapter<Pair<Chat, User?>, ChatsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: Pair<Chat, User?>) {
            val (chat, user) = pair
            binding.tvName.text = user?.displayName ?: "Unknown"
            binding.tvLastMessage.text = chat.lastMessage.ifEmpty { "No messages yet" }
            binding.tvTime.text = if (chat.lastMessageTime > 0) chat.lastMessageTime.toTimeString() else ""
            binding.tvAvatar.text = (user?.displayName ?: "?").toInitials()

            if (chat.unreadCount > 0) {
                binding.tvUnread.text = chat.unreadCount.toString()
                binding.tvUnread.visibility = android.view.View.VISIBLE
            } else {
                binding.tvUnread.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener { onClick(chat, user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Pair<Chat, User?>>() {
        override fun areItemsTheSame(a: Pair<Chat, User?>, b: Pair<Chat, User?>) =
            a.first.chatId == b.first.chatId
        override fun areContentsTheSame(a: Pair<Chat, User?>, b: Pair<Chat, User?>) =
            a.first.lastMessage == b.first.lastMessage && a.first.lastMessageTime == b.first.lastMessageTime
    }
}
