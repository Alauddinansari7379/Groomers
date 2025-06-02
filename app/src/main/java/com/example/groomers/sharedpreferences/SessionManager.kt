package com.example.groomers.sharedpreferences
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val MAIN_ACCESS_TOKEN = "MAIN_ACCESS_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_ID = "USER_ID"
        private const val NAME = "NAME"
        private const val MOBILE = "MOBILE"
        private const val EMAIL = "EMAIL"
        private const val IS_LOGIN = "IS_LOGIN"
        private const val USERTYPE = "USERTYPE"
        private const val PROFILE_PICTURE_URL = "PROFILE_PICTURE_URL"
    }
    var accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN, null)
        set(value) {
            prefs.edit { putString(ACCESS_TOKEN, value) }
        }
    var mainAccessToken: String?
        get() = prefs.getString(MAIN_ACCESS_TOKEN, null)
        set(value) {
            prefs.edit { putString(MAIN_ACCESS_TOKEN, value) }
        }
    var userType: String?
        get() = prefs.getString(USERTYPE, null)
        set(value) {
            prefs.edit { putString(USERTYPE, value) }
        }

    var username: String?
        get() = prefs.getString(USER_NAME, null)
        set(value) {
            prefs.edit { putString(USER_NAME, value) }
        }
    var userId: String?
        get() = prefs.getString(USER_ID, null)
        set(value) {
            prefs.edit { putString(USER_ID, value) }
        }

    var name: String?
        get() = prefs.getString(NAME, null)
        set(value) {
            prefs.edit { putString(NAME, value) }
        }

    var mobile: String?
        get() = prefs.getString(MOBILE, null)
        set(value) {
            prefs.edit { putString(MOBILE, value) }
        }

    var email: String?
        get() = prefs.getString(EMAIL, null)
        set(value) {
            prefs.edit { putString(EMAIL, value) }
        }

    var isLogin: Boolean
        get() = prefs.getBoolean(IS_LOGIN, false)
        set(value) {
            prefs.edit { putBoolean(IS_LOGIN, value) }
        }

    fun clearSession() {
        prefs.edit { clear() }
    }
    var profilePictureUrl: String?
        get() = prefs.getString(PROFILE_PICTURE_URL, null)
        set(value) {
            prefs.edit { putString(PROFILE_PICTURE_URL, value) }
        }
}
