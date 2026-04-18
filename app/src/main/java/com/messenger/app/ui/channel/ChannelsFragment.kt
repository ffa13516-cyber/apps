package com.messenger.app.ui.channel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.app.databinding.FragmentChannelsBinding
import com.messenger.app.ui.home.HomeViewModel
import com.messenger.app.utils.hide
import com.messenger.app.utils.show
import kotlinx.coroutines.launch

class ChannelsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels({ requireActivity() })
    private lateinit var adapter: ChannelsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChannelsAdapter { channel ->
            val intent = Intent(requireContext(), ChannelActivity::class.java).apply {
                putExtra("CHANNEL_ID", channel.channelId)
                putExtra("CHANNEL_NAME", channel.name)
                putExtra("ADMIN_ID", channel.adminId)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.channels.collect { channels ->
                if (channels.isEmpty()) {
                    binding.emptyView.show()
                    binding.recyclerView.hide()
                } else {
                    binding.emptyView.hide()
                    binding.recyclerView.show()
                    adapter.submitList(channels)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
