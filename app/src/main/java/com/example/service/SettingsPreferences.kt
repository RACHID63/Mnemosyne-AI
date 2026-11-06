package com.example.service

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mnemosyne_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_KEY = "deepseek_api_key"
        private const val KEY_MODEL = "deepseek_model"
        private const val KEY_CUSTOM_URL = "custom_api_url"
        private const val KEY_DARK_MODE = "dark_mode"
        const val DEFAULT_MODEL = "deepseek-chat"
        const val DEFAULT_URL = "https://api.deepseek.com/chat/completions"
    }

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value.trim()).apply()

    var model: String
        get() = prefs.getString(KEY_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
        set(value) = prefs.edit().putString(KEY_MODEL, value).apply()

    var customUrl: String
        get() = prefs.getString(KEY_CUSTOM_URL, DEFAULT_URL) ?: DEFAULT_URL
        set(value) = prefs.edit().putString(KEY_CUSTOM_URL, value.trim()).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    val hasApiKey: Boolean
        get() = apiKey.isNotBlank()
}
