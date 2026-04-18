package com.messenger.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.messenger.app.R
import com.messenger.app.databinding.ActivityHomeBinding
import com.messenger.app.ui.auth.LoginActivity
import com.messenger.app.ui.channel.CreateChannelActivity
import com.messenger.app.ui.common.NewChatBottomSheet
import com.messenger.app.ui.group.CreateGroupActivity
import com.messenger.app.utils.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Messenger"

        setupViewPager()
        setupFab()
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
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val currentTab = binding.viewPager.currentItem
            when (currentTab) {
                0 -> {
                    val sheet = NewChatBottomSheet()
                    sheet.show(supportFragmentManager, "new_chat")
                }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                sessionManager.clearSession()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
