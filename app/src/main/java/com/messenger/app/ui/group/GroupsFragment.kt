package com.messenger.app.ui.group

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.app.databinding.FragmentGroupsBinding
import com.messenger.app.ui.home.HomeViewModel
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.hide
import com.messenger.app.utils.show
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels({ requireActivity() })
    private lateinit var adapter: GroupsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroupsAdapter { group ->
            val intent = Intent(requireContext(), GroupChatActivity::class.java).apply {
                putExtra("GROUP_ID", group.groupId)
                putExtra("GROUP_NAME", group.name)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.groups.collect { groups ->
                if (groups.isEmpty()) {
                    binding.emptyView.show()
                    binding.recyclerView.hide()
                } else {
                    binding.emptyView.hide()
                    binding.recyclerView.show()
                    adapter.submitList(groups)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
