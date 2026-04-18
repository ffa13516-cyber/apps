package com.messenger.app.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.app.databinding.FragmentChatsBinding
import com.messenger.app.ui.home.HomeViewModel
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.hide
import com.messenger.app.utils.show
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels({ requireActivity() })
    private lateinit var adapter: ChatsAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager.getInstance(requireContext())

        val myUid = sessionManager.getUserId()
        adapter = ChatsAdapter(myUid) { chat, user ->
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra("CHAT_ID", chat.chatId)
                putExtra("OTHER_USER_ID", user?.uid ?: "")
                putExtra("OTHER_USER_NAME", user?.displayName ?: "Unknown")
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.loadData(myUid)

        lifecycleScope.launch {
            viewModel.chats.collect { chats ->
                if (chats.isEmpty()) {
                    binding.emptyView.show()
                    binding.recyclerView.hide()
                } else {
                    binding.emptyView.hide()
                    binding.recyclerView.show()
                    adapter.submitList(chats)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
