package com.zsd.eticaretuygulamasi.ui.screens.favoriler_sayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.FavorilerRepository
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine // combine importu
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavorilerSayfaViewModel @Inject constructor(
    private val urepo: UrunlerRepository,
    private val favorilerRepo: FavorilerRepository
) : ViewModel() {

    // Tüm ürünleri tutacak StateFlow (API'den gelen orijinal liste)
    private val _tumUrunler = MutableStateFlow<List<Urun>>(emptyList())
    // Sadece favori olan ürünleri tutacak StateFlow (Filtrelenmiş liste)
    val favoriUrunler: StateFlow<List<Urun>> get() = _favoriUrunler
    private val _favoriUrunler = MutableStateFlow<List<Urun>>(emptyList())

    // Favori ID'lerini tutacak ve değişiklikleri takip edecek StateFlow
    private val _favoriIdleri = MutableStateFlow<Set<String>>(emptySet())


    init {
        favoriIdleriniYukle()
        tumUrunleriYukle()

        // _tumUrunler veya _favoriIdleri her değiştiğinde _favoriUrunler listesini güncelle
        viewModelScope.launch {
            combine(_tumUrunler, _favoriIdleri) { urunler, idler ->
                urunler.filter { idler.contains(it.id.toString()) }
                    .map { it.copy(isFavorite = true) }
            }.collect { filtrelenmisListe ->
                _favoriUrunler.value = filtrelenmisListe
                Log.d("FavorilerViewModel", "Favori listesi güncellendi: ${filtrelenmisListe.size} ürün")
            }
        }
    }

    // Favori ID'lerini SharedPreferences'dan yükle
    fun favoriIdleriniYukle() { // Public yapıldı
        _favoriIdleri.value = favorilerRepo.getFavoriIds()
        Log.d("FavorilerViewModel", "Favori ID'leri yüklendi: ${_favoriIdleri.value.size} adet")
    }

    private fun tumUrunleriYukle() {
        viewModelScope.launch {
            try {
                _tumUrunler.value = urepo.urunleriGetir()
                Log.d("FavorilerViewModel", "Tüm ürünler yüklendi: ${_tumUrunler.value.size} ürün")
            } catch (e: Exception) {
                Log.e("FavorilerViewModel", "Tüm ürünler yüklenirken hata oluştu: ${e.message ?: "Bilinmeyen Hata"}", e)
            }
        }
    }

    // Favori durumunu değiştirme (Favoriler sayfasında kullanılır)
    fun toggleFavori(urun: Urun) {
        favorilerRepo.toggleFavori(urun.id)
        favoriIdleriniYukle() // Favori ID'lerini tekrar yükleyerek state'i güncelle
    }
}

