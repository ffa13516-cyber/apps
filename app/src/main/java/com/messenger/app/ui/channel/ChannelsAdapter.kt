package com.messenger.app.ui.channel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.data.model.Channel
import com.messenger.app.databinding.ItemChannelBinding
import com.messenger.app.utils.toInitials
import com.messenger.app.utils.toTimeString

class ChannelsAdapter(private val onClick: (Channel) -> Unit) :
    ListAdapter<Channel, ChannelsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(channel: Channel) {
            binding.tvName.text = channel.name
            binding.tvLastMessage.text = channel.lastMessage.ifEmpty { "No posts yet" }
            binding.tvTime.text = if (channel.lastMessageTime > 0) channel.lastMessageTime.toTimeString() else ""
            binding.tvAvatar.text = channel.name.toInitials()
            binding.tvSubscriberCount.text = "${channel.subscribers.size} subscribers"
            binding.root.setOnClickListener { onClick(channel) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(a: Channel, b: Channel) = a.channelId == b.channelId
        override fun areContentsTheSame(a: Channel, b: Channel) = a == b
    }
}
