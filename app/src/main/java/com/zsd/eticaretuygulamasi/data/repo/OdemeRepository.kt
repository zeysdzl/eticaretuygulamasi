package com.zsd.eticaretuygulamasi.data.repo

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zsd.eticaretuygulamasi.data.entity.OdemeYontemi
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class OdemeRepository @Inject constructor(
    @Named("odeme_prefs") private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    private val ODEME_LIST_KEY = "odeme_yontemleri_listesi"

    fun getOdemeYontemleri(): List<OdemeYontemi> {
        val json = sharedPreferences.getString(ODEME_LIST_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<OdemeYontemi>>() {}.type
            try {
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun saveOdemeYontemi(odemeYontemi: OdemeYontemi) {
        val mevcutYontemler = getOdemeYontemleri().toMutableList()
        mevcutYontemler.add(odemeYontemi)
        saveOdemeListesi(mevcutYontemler)
    }

    fun deleteOdemeYontemi(odemeId: String) {
        val mevcutYontemler = getOdemeYontemleri().toMutableList()
        mevcutYontemler.removeAll { it.id == odemeId }
        saveOdemeListesi(mevcutYontemler)
    }

    private fun saveOdemeListesi(yontemler: List<OdemeYontemi>) {
        val json = gson.toJson(yontemler)
        sharedPreferences.edit().putString(ODEME_LIST_KEY, json).apply()
    }
}