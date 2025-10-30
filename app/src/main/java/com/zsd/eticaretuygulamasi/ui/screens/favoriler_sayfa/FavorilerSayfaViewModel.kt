package com.zsd.eticaretuygulamasi.ui.screens.favoriler_sayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.FavsRepository
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavorilerSayfaViewModel @Inject constructor(
    private val urepo: UrunlerRepository,
    private val favorilerRepo: FavsRepository
) : ViewModel() {

    private val _tumUrunler = MutableStateFlow<List<Urun>>(emptyList())
    val favoriUrunler: StateFlow<List<Urun>> get() = _favoriUrunler
    private val _favoriUrunler = MutableStateFlow<List<Urun>>(emptyList())

    private val _favoriIdleri = MutableStateFlow<Set<String>>(emptySet())


    init {
        tumUrunleriYukle()

        viewModelScope.launch {
            combine(_tumUrunler, _favoriIdleri) { urunler, idler ->
                urunler.filter { idler.contains(it.id.toString()) }
            }.collect { filtrelenmisListe ->
                _favoriUrunler.value = filtrelenmisListe
                Log.d("FavorilerViewModel", "Favori listesi güncellendi: ${filtrelenmisListe.size} ürün")
            }
        }
    }

    fun favoriIdleriniYukle() {
        viewModelScope.launch {
            _favoriIdleri.value = favorilerRepo.getFavoriIds()
            Log.d("FavorilerViewModel", "Favori ID'leri yüklendi/güncellendi: ${_favoriIdleri.value.size} adet")
        }
    }

    private fun tumUrunleriYukle() {
        viewModelScope.launch {
            try {
                _tumUrunler.value = urepo.urunleriYukle()
                Log.d("FavorilerViewModel", "Tüm ürünler yüklendi: ${_tumUrunler.value.size} ürün")
            } catch (e: Exception) {
                Log.e("FavorilerViewModel", "Tüm ürünler yüklenirken hata oluştu: ${e.message ?: "Bilinmeyen Hata"}", e)
            }
        }
    }

    fun toggleFavori(urun: Urun) {
        viewModelScope.launch {
            favorilerRepo.toggleFavori(urun)
            favoriIdleriniYukle()
        }
    }

    fun favorilerdenSepeteEkle(urun: Urun) {
        Log.d("FavorilerViewModel", "favorilerdenSepeteEkle çağrıldı: Ürün adı=${urun.ad}")
        viewModelScope.launch {
            try {
                val mevcutSepet = urepo.sepetiGetir()
                val mevcutUrun = mevcutSepet.find { it.ad == urun.ad }

                if (mevcutUrun != null) {
                    val yeniAdet = mevcutUrun.siparisAdeti + 1
                    val silmeBasarili = urepo.sepettenSil(mevcutUrun.sepetId)
                    if (silmeBasarili) {
                        urepo.sepeteEkle(urun, yeniAdet)
                    }
                } else {
                    urepo.sepeteEkle(urun, 1)
                }
            } catch (e: Exception) {
                Log.e("FavorilerViewModel", "Sepete eklerken genel hata: ${e.message ?: "Bilinmeyen Hata"}", e)
            }
        }
    }
}