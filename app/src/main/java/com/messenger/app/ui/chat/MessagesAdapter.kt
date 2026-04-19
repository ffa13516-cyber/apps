package com.messenger.app.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.data.model.Message
import com.messenger.app.databinding.ItemMessageReceivedBinding
import com.messenger.app.databinding.ItemMessageSentBinding
import com.messenger.app.utils.toFullTimeString
import com.messenger.app.utils.toInitials

class MessagesAdapter(private val myUid: String) :
    ListAdapter<Message, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == myUid) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentViewHolder -> holder.bind(message)
            is ReceivedViewHolder -> holder.bind(message)
        }
    }

    inner class SentViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.text
            binding.tvTime.text = message.timestamp.toFullTimeString()
        }
    }

    inner class ReceivedViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.text
            binding.tvTime.text = message.timestamp.toFullTimeString()

            // Show avatar initial
            binding.tvAvatarInitial.text = message.senderName.toInitials()

            // Show sender name in groups
            if (message.senderName.isNotBlank()) {
                binding.tvSenderName.text = message.senderName
                binding.tvSenderName.visibility = android.view.View.VISIBLE
            } else {
                binding.tvSenderName.visibility = android.view.View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(a: Message, b: Message) = a.messageId == b.messageId
        override fun areContentsTheSame(a: Message, b: Message) = a == b
    }
    }
