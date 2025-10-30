package com.zsd.eticaretuygulamasi.data.repo

import android.content.SharedPreferences
import android.util.Log
import com.zsd.eticaretuygulamasi.data.entity.Urun
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FavsRepository @Inject constructor(
    @Named("favori_prefs") private val sharedPreferences: SharedPreferences
) {

    private val FAVORITES_KEY = "favoriler"

    fun getFavoriIds(): MutableSet<String> {
        return sharedPreferences.getStringSet(FAVORITES_KEY, HashSet<String>())?.toMutableSet() ?: mutableSetOf()
    }

    private fun saveFavoriIds(ids: Set<String>) {
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, ids).apply()
        Log.d("FavsRepository", "Favori ID'leri kaydedildi: ${ids.joinToString()}")
    }

    suspend fun isFavori(urunId: Int): Boolean {
        return getFavoriIds().contains(urunId.toString())
    }

    suspend fun addFavori(urun: Urun) {
        val ids = getFavoriIds()
        val eklendiMi = ids.add(urun.id.toString())
        saveFavoriIds(ids)
        Log.d("FavsRepository", "addFavori çağrıldı: ${urun.ad} (ID: ${urun.id}). Set'e eklendi mi: $eklendiMi")
    }

    suspend fun removeFavori(urun: Urun) {
        val ids = getFavoriIds()
        ids.remove(urun.id.toString())
        saveFavoriIds(ids)
    }

    suspend fun toggleFavori(urun: Urun): Boolean {
        val isCurrentlyFavorite = isFavori(urun.id)
        if (isCurrentlyFavorite) {
            removeFavori(urun)
            return false
        } else {
            addFavori(urun)
            return true
        }
    }
}