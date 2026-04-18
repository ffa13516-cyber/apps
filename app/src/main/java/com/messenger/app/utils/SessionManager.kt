package com.messenger.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "messenger_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PHONE = "phone_number"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun saveSession(uid: String, phone: String, name: String) {
        prefs.edit {
            putString(KEY_USER_ID, uid)
            putString(KEY_PHONE, phone)
            putString(KEY_DISPLAY_NAME, name)
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }

    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""
    fun getPhone(): String = prefs.getString(KEY_PHONE, "") ?: ""
    fun getDisplayName(): String = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit { clear() }
    }
}
