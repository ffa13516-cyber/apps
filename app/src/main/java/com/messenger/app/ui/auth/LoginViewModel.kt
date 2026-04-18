package com.messenger.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.app.data.model.User
import com.messenger.app.data.repository.UserRepository
import com.messenger.app.utils.generateUid
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(phoneNumber: String) {
        if (phoneNumber.isBlank() || phoneNumber.length < 7) {
            _loginState.value = LoginState.Error("Enter a valid phone number")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            // Check if user already exists
            val existingUser = userRepository.getUserByPhone(phoneNumber)
            if (existingUser.isSuccess && existingUser.getOrNull() != null) {
                _loginState.value = LoginState.Success(existingUser.getOrNull()!!)
            } else {
                // Create new user
                val displayName = "User ${phoneNumber.takeLast(4)}"
                val uid = generateUid()
                val newUser = User(
                    uid = uid,
                    phoneNumber = phoneNumber,
                    displayName = displayName,
                    isOnline = true
                )
                val result = userRepository.saveUser(newUser)
                if (result.isSuccess) {
                    _loginState.value = LoginState.Success(newUser)
                } else {
                    _loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Login failed")
                }
            }
        }
    }
}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
