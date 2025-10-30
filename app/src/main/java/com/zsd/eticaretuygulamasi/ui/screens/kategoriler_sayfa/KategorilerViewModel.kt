package com.zsd.eticaretuygulamasi.ui.screens.kategoriler_sayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.FavsRepository
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KategorilerViewModel @Inject constructor(
    private val urepo: UrunlerRepository,
    private val favsRepo: FavsRepository
) : ViewModel() {

    private val _tumUrunler = MutableStateFlow<List<Urun>>(emptyList())
    private val _kategoriler = MutableStateFlow<List<String>>(emptyList())
    val kategoriler: StateFlow<List<String>> = _kategoriler.asStateFlow()

    private val _seciliKategori = MutableStateFlow<String?>(null)
    val seciliKategori: StateFlow<String?> = _seciliKategori.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filtrelenmisUrunler = MutableStateFlow<List<Urun>>(emptyList())
    val filtrelenmisUrunler: StateFlow<List<Urun>> = _filtrelenmisUrunler.asStateFlow()

    init {
        loadData()
        observeFilters()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val urunListesi = urepo.urunleriYukle()
                _tumUrunler.value = urunListesi
                _kategoriler.value = urunListesi.map { it.kategori }.distinct().sorted()
                Log.d("KategorilerVM", "Kategoriler yüklendi: ${_kategoriler.value}")
            } catch (e: Exception) {
                Log.e("KategorilerVM", "Veri yüklenirken hata: ${e.message}")
            }
        }
    }

    private fun observeFilters() {
        viewModelScope.launch {
            combine(_tumUrunler, _seciliKategori, _searchQuery) { urunler, kategori, query ->
                val kategoriFiltreli = if (kategori == null) {
                    urunler
                } else {
                    urunler.filter { it.kategori.equals(kategori, ignoreCase = true) }
                }

                if (query.isBlank()) {
                    kategoriFiltreli
                } else {
                    kategoriFiltreli.filter {
                        it.ad.contains(query, ignoreCase = true) ||
                                it.marka.contains(query, ignoreCase = true)
                    }
                }
            }.collect { sonListe ->
                _filtrelenmisUrunler.value = sonListe
            }
        }
    }

    fun selectKategori(kategori: String?) {
        _seciliKategori.value = kategori
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun kategorilerdenSepeteEkle(urun: Urun) {
        Log.d("KategorilerVM", "kategorilerdenSepeteEkle çağrıldı: Ürün adı=${urun.ad}")
        viewModelScope.launch {
            try {
                urepo.sepeteEkle(urun, 1)
            } catch (e: Exception) {
                Log.e("KategorilerVM", "Sepete eklenemedi: ${e.message}")
            }
        }
    }
}