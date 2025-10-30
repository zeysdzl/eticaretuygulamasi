package com.zsd.eticaretuygulamasi.data.repo

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zsd.eticaretuygulamasi.data.entity.Adres
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AdresRepository @Inject constructor(
    @Named("adres_prefs") private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    private val ADRES_LIST_KEY = "adres_listesi"

    fun getAdresler(): List<Adres> {
        val json = sharedPreferences.getString(ADRES_LIST_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Adres>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getAdresById(adresId: String): Adres? {
        return getAdresler().find { it.id == adresId }
    }

    fun saveAdres(adres: Adres) {
        val mevcutAdresler = getAdresler().toMutableList()
        mevcutAdresler.add(adres)
        saveAdresListesi(mevcutAdresler)
    }

    fun updateAdres(updatedAdres: Adres) {
        val mevcutAdresler = getAdresler().toMutableList()
        val index = mevcutAdresler.indexOfFirst { it.id == updatedAdres.id }
        if (index != -1) {
            mevcutAdresler[index] = updatedAdres
            saveAdresListesi(mevcutAdresler)
        }
    }

    fun deleteAdres(adresId: String) {
        val mevcutAdresler = getAdresler().toMutableList()
        mevcutAdresler.removeAll { it.id == adresId }
        saveAdresListesi(mevcutAdresler)
    }

    private fun saveAdresListesi(adresler: List<Adres>) {
        val json = gson.toJson(adresler)
        sharedPreferences.edit().putString(ADRES_LIST_KEY, json).apply()
    }
}