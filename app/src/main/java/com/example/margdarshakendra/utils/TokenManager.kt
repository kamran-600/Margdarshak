package com.example.margdarshakendra.utils

import android.content.Context
import com.example.margdarshakendra.utils.Constants.PREF_FILE
import com.example.margdarshakendra.utils.Constants.USER_AUTH_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private var prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_AUTH_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_AUTH_TOKEN, null)
    }


}