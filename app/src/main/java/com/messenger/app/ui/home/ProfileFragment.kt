package com.messenger.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.messenger.app.R
import com.messenger.app.data.repository.UserRepository
import com.messenger.app.databinding.FragmentProfileBinding
import com.messenger.app.ui.chat.SavedMessagesFragment
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.toast
import com.messenger.app.utils.toInitials
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

        val uid = sessionManager.getUserId()
        val phone = sessionManager.getPhone()

        lifecycleScope.launch {
            val user = userRepository.getUserById(uid).getOrNull()
            val name = user?.displayName ?: sessionManager.getDisplayName()
            val username = user?.username ?: ""

            binding.tvName.text = name
            binding.tvPhone.text = phone
            binding.tvAvatarInitial.text = name.toInitials()
            binding.etName.setText(name)
            binding.etUsername.setText(username)
        }

        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            val newUsername = binding.etUsername.text.toString().trim().lowercase()

            if (newName.isBlank()) {
                requireContext().toast("Name cannot be empty")
                return@setOnClickListener
            }

            if (newUsername.isBlank()) {
                requireContext().toast("Username cannot be empty")
                return@setOnClickListener
            }

            if (!newUsername.matches(Regex("^[a-z0-9_.]+$"))) {
                requireContext().toast("Username can only contain letters, numbers, _ and .")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val currentUser = userRepository.getUserById(uid).getOrNull()
                if (currentUser?.username != newUsername) {
                    val available = userRepository.isUsernameAvailable(newUsername)
                    if (!available) {
                        requireContext().toast("Username @$newUsername is taken!")
                        return@launch
                    }
                }

                currentUser?.let { user ->
                    userRepository.saveUser(
                        user.copy(
                            displayName = newName,
                            username = newUsername
                        )
                    )
                }

                sessionManager.saveSession(uid, phone, newName)
                binding.tvName.text = newName
                binding.tvAvatarInitial.text = newName.toInitials()
                requireContext().toast("Saved! @$newUsername")
            }
        }

        binding.btnSavedMessages.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SavedMessagesFragment())
                .addToBackStack(null)
                .commit()
            requireActivity().title = "Saved Messages"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
