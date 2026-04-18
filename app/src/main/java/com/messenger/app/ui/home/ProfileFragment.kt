package com.messenger.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.messenger.app.data.repository.UserRepository
import com.messenger.app.databinding.FragmentProfileBinding
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.toast
import com.messenger.app.utils.toInitials
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private val userRepository = UserRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager.getInstance(requireContext())

        val name = sessionManager.getDisplayName()
        val phone = sessionManager.getPhone()

        binding.tvName.text = name
        binding.tvPhone.text = phone
        binding.tvAvatarInitial.text = name.toInitials()
        binding.etName.setText(name)

        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            if (newName.isBlank()) {
                requireContext().toast("Name cannot be empty")
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val uid = sessionManager.getUserId()
                userRepository.getUserById(uid).getOrNull()?.let { user ->
                    userRepository.saveUser(user.copy(displayName = newName))
                }
                sessionManager.saveSession(uid, phone, newName)
                binding.tvName.text = newName
                binding.tvAvatarInitial.text = newName.toInitials()
                requireContext().toast("Saved!")
            }
        }

        binding.btnSavedMessages.setOnClickListener {
            requireContext().toast("Coming soon!")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
