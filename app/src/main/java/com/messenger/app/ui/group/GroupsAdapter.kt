package com.messenger.app.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.data.model.Group
import com.messenger.app.databinding.ItemGroupBinding
import com.messenger.app.utils.toInitials
import com.messenger.app.utils.toTimeString

class GroupsAdapter(private val onClick: (Group) -> Unit) :
    ListAdapter<Group, GroupsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            binding.tvName.text = group.name
            binding.tvLastMessage.text = group.lastMessage.ifEmpty { "No messages yet" }
            binding.tvTime.text = if (group.lastMessageTime > 0) group.lastMessageTime.toTimeString() else ""
            binding.tvAvatar.text = group.name.toInitials()
            binding.tvMemberCount.text = "${group.members.size} members"
            binding.root.setOnClickListener { onClick(group) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(a: Group, b: Group) = a.groupId == b.groupId
        override fun areContentsTheSame(a: Group, b: Group) = a == b
    }
}
