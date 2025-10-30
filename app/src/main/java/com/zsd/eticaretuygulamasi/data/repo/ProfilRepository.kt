package com.zsd.eticaretuygulamasi.data.repo

import android.content.SharedPreferences
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.data.entity.ProfilBilgileri
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ProfilRepository @Inject constructor(
    @Named("profil_prefs") private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    private val PROFIL_KEY = "profil_bilgileri"

    fun getProfilBilgileri(): ProfilBilgileri {
        val json = sharedPreferences.getString(PROFIL_KEY, null)
        return if (json != null) {
            try {
                gson.fromJson(json, ProfilBilgileri::class.java) ?: ProfilBilgileri()
            } catch (e: Exception) {
                ProfilBilgileri()
            }
        } else {
            ProfilBilgileri()
        }
    }

    fun saveProfilBilgileri(profil: ProfilBilgileri) {
        val json = gson.toJson(profil)
        sharedPreferences.edit().putString(PROFIL_KEY, json).apply()
    }
}