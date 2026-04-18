package com.messenger.app.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Long.toTimeString(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this))
        else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(this))
    }
}

fun Long.toFullTimeString(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this))
}

fun generateUid(): String = UUID.randomUUID().toString().replace("-", "").substring(0, 20)

fun String.toInitials(): String {
    val parts = trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].firstOrNull()?.uppercaseChar() ?: ""}${parts[1].firstOrNull()?.uppercaseChar() ?: ""}"
        parts.isNotEmpty() && parts[0].isNotEmpty() -> "${parts[0][0].uppercaseChar()}"
        else -> "?"
    }
}
