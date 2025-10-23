package com.zsd.eticaretuygulamasi.data.repo

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

// SharedPreferences kullanarak favori yönetimini yapar
@Singleton
class FavorilerRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val FAVORITES_KEY = "favoriler"

    // Fonksiyon 'public' yapıldı (private kaldırıldı)
    // Favori ID'lerini Set<String> olarak alır
    fun getFavoriIds(): MutableSet<String> {
        return sharedPreferences.getStringSet(FAVORITES_KEY, HashSet<String>())?.toMutableSet() ?: mutableSetOf()
    }

    // Bu fonksiyon private kalabilir, sadece bu sınıf içinde kullanılıyor
    private fun saveFavoriIds(ids: Set<String>) {
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, ids).apply()
    }

    // Bir ürünün favori olup olmadığını kontrol eder
    fun isFavori(urunId: Int): Boolean {
        return getFavoriIds().contains(urunId.toString())
    }

    // Bir ürünü favorilere ekler
    fun favoriEkle(urunId: Int) {
        val ids = getFavoriIds()
        ids.add(urunId.toString())
        saveFavoriIds(ids)
    }

    // Bir ürünü favorilerden çıkarır
    fun favoriCikar(urunId: Int) {
        val ids = getFavoriIds()
        ids.remove(urunId.toString())
        saveFavoriIds(ids)
    }

    // Favori durumunu değiştirir (varsa çıkarır, yoksa ekler)
    fun toggleFavori(urunId: Int): Boolean {
        val isCurrentlyFavorite = isFavori(urunId)
        if (isCurrentlyFavorite) {
            favoriCikar(urunId)
            return false // Yeni durum: favori değil
        } else {
            favoriEkle(urunId)
            return true // Yeni durum: favori
        }
    }
}

