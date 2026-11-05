package com.bhh.base.sp

import android.content.Context
import com.bhh.tools.app.IApplicationHandler

@Suppress("UNCHECKED_CAST")
class SharedPreferences constructor(fileName: String): IApplicationHandler {

    private val sharePreferences = application!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)!!

    fun <T : Any> get(key: String, default: T): T {

        return when (default) {
            is Boolean -> sharePreferences.getBoolean(key, default as Boolean) as T

            is Int -> sharePreferences.getInt(key, default as Int) as T

            is Float -> sharePreferences.getFloat(key, default as Float) as T

            is Long -> sharePreferences.getLong(key, default as Long) as T

            is String -> sharePreferences.getString(key, default as String) as T

            is Set<*> -> sharePreferences.getStringSet(key, default as Set<String>) as T

            else -> Unit as T
        }
    }

    fun remove(key: String) {
        val edit = sharePreferences.edit()
        edit.remove(key)
        edit.apply()
    }

    fun <T : Any> put(key: String, value: T) {
        val edit = sharePreferences.edit()
        when (value) {

            is Boolean -> edit.putBoolean(key, value as Boolean)

            is Int -> edit.putInt(key, value as Int)

            is Float -> edit.putFloat(key, value as Float)

            is Long -> edit.putLong(key, value as Long)

            is String -> edit.putString(key, value as String)

            is Set<*> -> edit.putStringSet(key, value as Set<String>)
        }
        edit.apply()
    }
}