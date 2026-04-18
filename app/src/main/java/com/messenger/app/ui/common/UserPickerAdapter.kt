package com.messenger.app.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.data.model.User
import com.messenger.app.databinding.ItemUserPickerBinding
import com.messenger.app.utils.toInitials

class UserPickerAdapter(
    private val myUid: String,
    private val singleSelect: Boolean = false,
    private val onSingleClick: ((User) -> Unit)? = null
) : ListAdapter<User, UserPickerAdapter.ViewHolder>(DiffCallback()) {

    private val selectedUids = mutableSetOf<String>()

    inner class ViewHolder(private val binding: ItemUserPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvName.text = user.displayName
            binding.tvPhone.text = user.phoneNumber
            binding.tvAvatar.text = user.displayName.toInitials()
            binding.checkbox.isChecked = selectedUids.contains(user.uid)

            if (singleSelect) {
                binding.checkbox.visibility = android.view.View.GONE
                binding.root.setOnClickListener { onSingleClick?.invoke(user) }
            } else {
                binding.checkbox.visibility = android.view.View.VISIBLE
                binding.root.setOnClickListener {
                    if (selectedUids.contains(user.uid)) selectedUids.remove(user.uid)
                    else selectedUids.add(user.uid)
                    binding.checkbox.isChecked = selectedUids.contains(user.uid)
                }
                binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedUids.add(user.uid) else selectedUids.remove(user.uid)
                }
            }
        }
    }

    fun getSelectedUids(): List<String> = selectedUids.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(a: User, b: User) = a.uid == b.uid
        override fun areContentsTheSame(a: User, b: User) = a == b
    }
}
