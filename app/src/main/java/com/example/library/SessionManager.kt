package com.example.library

import android.content.Context

object SessionManager {

    private const val PREFS = "user_session"
    private const val KEY_NAME = "nomeCompleto"
    private const val KEY_USER_ID = "userId"
    private const val KEY_ADMIN = "isAdmin"

    fun saveUser(context: Context, id: Int, name: String, isAdmin: Boolean) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_USER_ID, id)
            .putString(KEY_NAME, name)
            .putBoolean(KEY_ADMIN, isAdmin)
            .apply()
    }

    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val isAdmin = prefs.getBoolean(KEY_ADMIN, false)

        return prefs.getString(KEY_NAME, "Usuário") ?: "Usuário"
    }

    fun getUserId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun isAdmin(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ADMIN, false)
    }
}
