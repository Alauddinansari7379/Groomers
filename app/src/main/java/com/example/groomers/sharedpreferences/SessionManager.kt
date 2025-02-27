package com.example.groomers.sharedpreferences
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN, null)
        set(value) {
            prefs.edit().putString(ACCESS_TOKEN, value).apply()
        }

    var isLogin: Boolean
        get() = prefs.getBoolean(IS_LOGIN, false)
        set(value) {
            prefs.edit().putBoolean(IS_LOGIN, value).apply()
        }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val IS_LOGIN = "IS_LOGIN"
    }
}
