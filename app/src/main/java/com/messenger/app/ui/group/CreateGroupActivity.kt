package com.messenger.app.ui.group

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.app.databinding.ActivityCreateGroupBinding
import com.messenger.app.ui.common.UserPickerAdapter
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.hide
import com.messenger.app.utils.show
import com.messenger.app.utils.toast
import kotlinx.coroutines.launch

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private val viewModel: GroupViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var userPickerAdapter: UserPickerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "New Group"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        setupUserPicker()
        setupCreateButton()
        observeState()

        viewModel.loadAllUsers()
    }

    private fun setupUserPicker() {
        userPickerAdapter = UserPickerAdapter(sessionManager.getUserId())
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.adapter = userPickerAdapter

        lifecycleScope.launch {
            viewModel.allUsers.collect { users ->
                userPickerAdapter.submitList(users.filter { it.uid != sessionManager.getUserId() })
            }
        }
    }

    private fun setupCreateButton() {
        binding.btnCreate.setOnClickListener {
            val name = binding.etGroupName.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val selectedUids = userPickerAdapter.getSelectedUids()
            viewModel.createGroup(name, description, sessionManager.getUserId(), selectedUids)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.createState.collect { state ->
                when (state) {
                    is GroupCreateState.Loading -> {
                        binding.progressBar.show()
                        binding.btnCreate.isEnabled = false
                    }
                    is GroupCreateState.Success -> {
                        binding.progressBar.hide()
                        toast("Group created!")
                        finish()
                    }
                    is GroupCreateState.Error -> {
                        binding.progressBar.hide()
                        binding.btnCreate.isEnabled = true
                        toast(state.message)
                    }
                    else -> {
                        binding.progressBar.hide()
                        binding.btnCreate.isEnabled = true
                    }
                }
            }
        }
    }
}
