package com.messenger.app.ui.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.messenger.app.data.repository.ChatRepository
import com.messenger.app.data.repository.UserRepository
import com.messenger.app.databinding.BottomSheetNewChatBinding
import com.messenger.app.ui.chat.ChatActivity
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.toast
import kotlinx.coroutines.launch

class NewChatBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetNewChatBinding? = null
    private val binding get() = _binding!!
    private val userRepository = UserRepository()
    private val chatRepository = ChatRepository()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: UserPickerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetNewChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager.getInstance(requireContext())

        adapter = UserPickerAdapter(sessionManager.getUserId(), singleSelect = true) { user ->
            openChat(user.uid, user.displayName)
        }

        binding.rvUsers.adapter = adapter
        binding.rvUsers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val users = userRepository.getAllUsers().getOrNull() ?: emptyList()
            adapter.submitList(users.filter { it.uid != sessionManager.getUserId() })
        }
    }

    private fun openChat(otherUid: String, otherName: String) {
        lifecycleScope.launch {
            val myUid = sessionManager.getUserId()
            val result = chatRepository.getOrCreateChat(myUid, otherUid)
            if (result.isSuccess) {
                val chat = result.getOrNull()!!
                val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra("CHAT_ID", chat.chatId)
                    putExtra("OTHER_USER_ID", otherUid)
                    putExtra("OTHER_USER_NAME", otherName)
                }
                startActivity(intent)
                dismiss()
            } else {
                requireContext().toast("Failed to open chat")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
