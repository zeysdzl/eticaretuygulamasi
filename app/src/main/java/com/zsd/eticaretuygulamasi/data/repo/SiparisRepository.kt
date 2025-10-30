package com.zsd.eticaretuygulamasi.data.repo

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zsd.eticaretuygulamasi.data.entity.Siparis
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SiparisRepository @Inject constructor(
    @Named("siparis_prefs") private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    private val SIPARIS_LIST_KEY = "siparis_listesi"

    fun getSiparisler(): List<Siparis> {
        val json = sharedPreferences.getString(SIPARIS_LIST_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Siparis>>() {}.type
            try {
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun saveSiparis(siparis: Siparis) {
        val mevcutSiparisler = getSiparisler().toMutableList()
        mevcutSiparisler.add(0, siparis)
        saveSiparisListesi(mevcutSiparisler)
    }

    private fun saveSiparisListesi(siparisler: List<Siparis>) {
        val json = gson.toJson(siparisler)
        sharedPreferences.edit().putString(SIPARIS_LIST_KEY, json).apply()
    }
}