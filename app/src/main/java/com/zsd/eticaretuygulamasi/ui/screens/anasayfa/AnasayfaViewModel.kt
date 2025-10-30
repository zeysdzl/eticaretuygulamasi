package com.zsd.eticaretuygulamasi.ui.screens.anasayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.FavsRepository
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnasayfaViewModel @Inject constructor(
    private var urepo: UrunlerRepository,
    private var favsRepo: FavsRepository
) : ViewModel() {

    private val _urunListesi = MutableStateFlow<List<Urun>>(emptyList())
    val urunListesi: StateFlow<List<Urun>> = _urunListesi.asStateFlow()

    private var _tumUrunlerListesi = MutableStateFlow<List<Urun>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        urunleriYukle()
        observeSearchQuery()
    }

    private fun urunleriYukle() {
        viewModelScope.launch {
            try {
                val liste = urepo.urunleriYukle()
                _tumUrunlerListesi.value = liste
                _urunListesi.value = liste
            } catch (e: Exception) {
                Log.e("AnasayfaViewModel", "Ürünler yüklenemedi: ${e.message}")
                _tumUrunlerListesi.value = emptyList()
                _urunListesi.value = emptyList()
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery.collect { query ->
                val filteredList = if (query.isBlank()) {
                    _tumUrunlerListesi.value
                } else {
                    _tumUrunlerListesi.value.filter {
                        it.ad.contains(query, ignoreCase = true) ||
                                it.marka.contains(query, ignoreCase = true) ||
                                it.kategori.contains(query, ignoreCase = true)
                    }
                }
                _urunListesi.value = filteredList
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavori(urun: Urun) {
        viewModelScope.launch {
            val yeniFavoriDurumu = favsRepo.toggleFavori(urun)
            Log.d("AnasayfaViewModel", "Favori durumu değiştirildi: ${urun.ad}. Yeni Durum: $yeniFavoriDurumu")
        }
    }

    fun anasayfadanSepeteEkle(urun: Urun) {
        Log.d("AnasayfaViewModel", "anasayfadanSepeteEkle çağrıldı: Ürün adı=${urun.ad}")
        viewModelScope.launch {
            try {
                val mevcutSepet = urepo.sepetiGetir()
                val mevcutUrunSepette = mevcutSepet.find { it.ad == urun.ad }

                if (mevcutUrunSepette != null) {
                    val yeniAdet = mevcutUrunSepette.siparisAdeti + 1
                    Log.d("AnasayfaViewModel", "Ürün sepette bulundu. Adet güncelleniyor: ${mevcutUrunSepette.ad}, Yeni Adet: $yeniAdet")
                    val silmeBasarili = urepo.sepettenSil(mevcutUrunSepette.sepetId)
                    if (silmeBasarili) {
                        urepo.sepeteEkle(urun, yeniAdet)
                    } else {
                        Log.e("AnasayfaViewModel", "Sepetteki eski ürün silinemedi: ${mevcutUrunSepette.ad}")
                        throw Exception("Sepetteki eski ürün silinemedi.")
                    }
                } else {
                    Log.d("AnasayfaViewModel", "Ürün sepette yok. Yeni ekleniyor: ${urun.ad}, Adet: 1")
                    urepo.sepeteEkle(urun, 1)
                }
            } catch (e: Exception) {
                Log.e("AnasayfaViewModel", "Sepete eklenirken/güncellenirken hata: ${e.message}", e)
            }
        }
    }
}