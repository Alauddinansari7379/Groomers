package com.example.groomers.sharedpreferences
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val NAME = "NAME"
        private const val MOBILE = "MOBILE"
        private const val EMAIL = "EMAIL"
        private const val IS_LOGIN = "IS_LOGIN"
        private const val USERTYPE = "USERTYPE"
    }
    var accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN, null)
        set(value) {
            prefs.edit().putString(ACCESS_TOKEN, value).apply()
        }
    var userType: String?
        get() = prefs.getString(USERTYPE, null)
        set(value) {
            prefs.edit().putString(USERTYPE, value).apply()
        }

    var username: String?
        get() = prefs.getString(USER_NAME, null)
        set(value) {
            prefs.edit().putString(USER_NAME, value).apply()
        }

    var name: String?
        get() = prefs.getString(NAME, null)
        set(value) {
            prefs.edit().putString(NAME, value).apply()
        }

    var mobile: String?
        get() = prefs.getString(MOBILE, null)
        set(value) {
            prefs.edit().putString(MOBILE, value).apply()
        }

    var email: String?
        get() = prefs.getString(EMAIL, null)
        set(value) {
            prefs.edit().putString(EMAIL, value).apply()
        }

    var isLogin: Boolean
        get() = prefs.getBoolean(IS_LOGIN, false)
        set(value) {
            prefs.edit().putBoolean(IS_LOGIN, value).apply()
        }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

}
