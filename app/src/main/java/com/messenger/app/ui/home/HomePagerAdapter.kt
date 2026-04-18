package com.messenger.app.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.messenger.app.ui.channel.ChannelsFragment
import com.messenger.app.ui.chat.ChatsFragment
import com.messenger.app.ui.group.GroupsFragment

class HomePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ChatsFragment()
        1 -> GroupsFragment()
        2 -> ChannelsFragment()
        else -> ChatsFragment()
    }
}
