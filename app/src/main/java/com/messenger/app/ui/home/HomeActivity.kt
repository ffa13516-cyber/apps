package com.messenger.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.messenger.app.R
import com.messenger.app.databinding.ActivityHomeBinding
import com.messenger.app.ui.channel.CreateChannelActivity
import com.messenger.app.ui.common.NewChatBottomSheet
import com.messenger.app.ui.group.CreateGroupActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Messenger"

        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        val pagerAdapter = HomePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 3

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Chats"
                1 -> "Groups"
                2 -> "Channels"
                else -> ""
            }
            tab.setIcon(when (position) {
                0 -> R.drawable.ic_chat
                1 -> R.drawable.ic_group
                2 -> R.drawable.ic_channel
                else -> 0
            })
        }.attach()

        setupFab()
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val currentTab = binding.viewPager.currentItem
            when (currentTab) {
                0 -> NewChatBottomSheet().show(supportFragmentManager, "new_chat")
                1 -> startActivity(Intent(this, CreateGroupActivity::class.java))
                2 -> startActivity(Intent(this, CreateChannelActivity::class.java))
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.fab.setImageResource(
                    when (position) {
                        0 -> R.drawable.ic_new_chat
                        1 -> R.drawable.ic_group_add
                        2 -> R.drawable.ic_add_channel
                        else -> R.drawable.ic_new_chat
                    }
                )
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chats -> {
                    binding.viewPager.visibility = View.VISIBLE
                    binding.tabLayout.visibility = View.VISIBLE
                    binding.fragmentContainer.visibility = View.GONE
                    binding.fab.show()
                    supportActionBar?.title = "Messenger"
                    true
                }
                R.id.nav_profile -> {
                    binding.viewPager.visibility = View.GONE
                    binding.tabLayout.visibility = View.GONE
                    binding.fragmentContainer.visibility = View.VISIBLE
                    binding.fab.hide()
                    supportActionBar?.title = "Profile"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProfileFragment())
                        .commit()
                    true
                }
                R.id.nav_settings -> {
                    binding.viewPager.visibility = View.GONE
                    binding.tabLayout.visibility = View.GONE
                    binding.fragmentContainer.visibility = View.VISIBLE
                    binding.fab.hide()
                    supportActionBar?.title = "Settings"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, SettingsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
