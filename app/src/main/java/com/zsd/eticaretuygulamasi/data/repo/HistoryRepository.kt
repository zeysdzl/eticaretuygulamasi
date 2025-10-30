package com.zsd.eticaretuygulamasi.data.repo

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    @Named("history_prefs") private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    private val HISTORY_KEY = "history_list"
    private val MAX_HISTORY_SIZE = 15

    suspend fun addHistory(urunId: Int) {
        withContext(Dispatchers.IO) {
            val history = getHistory().toMutableList()
            history.remove(urunId)
            history.add(0, urunId)
            val trimmedHistory = history.take(MAX_HISTORY_SIZE)

            val jsonHistory = gson.toJson(trimmedHistory)
            sharedPreferences.edit().putString(HISTORY_KEY, jsonHistory).apply()
        }
    }

    suspend fun getHistory(): List<Int> {
        return withContext(Dispatchers.IO) {
            val jsonHistory = sharedPreferences.getString(HISTORY_KEY, null)
            if (jsonHistory == null) {
                emptyList()
            } else {
                val type = object : TypeToken<List<Int>>() {}.type
                gson.fromJson<List<Int>>(jsonHistory, type) ?: emptyList()
            }
        }
    }
}