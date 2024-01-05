package com.margdarshakendra.margdarshak.utils

import android.content.Context
import android.util.Log
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreference @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)

    fun saveDetail(key : String,value : Any, dataType : String){
        val editor = prefs.edit()
        when (dataType) {
            "Boolean" -> editor.putBoolean(key,value as Boolean)
            "Int" -> editor.putInt(key, value as Int)
            "String" -> editor.putString(key, value as String)
            "Long" -> editor.putLong(key, value as Long)
            "Float" -> editor.putFloat(key, value as Float)
            else -> editor.putStringSet(key, value as MutableSet<String>)
        }
        editor.apply()

        Log.d(TAG, " $key saved in prefrences")

    }

    fun getDetail(key : String, dataType : String?) : Any?{
        Log.d(TAG, "$key  gotton in prefrences")
        return when (dataType) {
            "Boolean" -> prefs.getBoolean(key, false)
            "Int" -> prefs.getInt(key, -1)
            "String" -> prefs.getString(key, null)
            "Long" -> prefs.getLong(key, 0L)
            "Float" -> prefs.getFloat(key, 0.0f)
            else -> prefs.getStringSet(key, HashSet())

        }
    }

    fun deleteDetail(key: String){
        val editor = prefs.edit()
        editor.remove(key)
        editor.apply()
        Log.d(TAG, "$key deleted from prefrences")
    }

    fun clearAll(){
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    /*fun saveProfileUpdatedFlag(profileUpdatedFlag: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(Constants.PROFILE_UPDATED, profileUpdatedFlag)
        editor.apply()
    }

    fun getProfileUpdatedFlag(): Boolean {
        return prefs.getBoolean(Constants.PROFILE_UPDATED, false)
    }
*/

}