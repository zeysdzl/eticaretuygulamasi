package com.zsd.eticaretuygulamasi.ui.screens.encoksatanlar_sayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnCokSatanlarViewModel @Inject constructor(
    private val urepo: UrunlerRepository
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
                Log.e("EnCokSatanlarVM", "Ürünler yüklenemedi: ${e.message}")
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

    fun enCokSatanlardanSepeteEkle(urun: Urun) {
        Log.d("EnCokSatanlarVM", "enCokSatanlardanSepeteEkle çağrıldı: Ürün adı=${urun.ad}")
        viewModelScope.launch {
            try {
                val mevcutSepet = urepo.sepetiGetir()
                val mevcutUrun = mevcutSepet.find { it.ad == urun.ad }

                if (mevcutUrun != null) {
                    val yeniAdet = mevcutUrun.siparisAdeti + 1
                    val silmeBasarili = urepo.sepettenSil(mevcutUrun.sepetId)
                    if (silmeBasarili) {
                        urepo.sepeteEkle(urun, yeniAdet)
                        Log.d("EnCokSatanlarVM", "${urun.ad} adedi artırıldı: $yeniAdet")
                    } else {
                        Log.e("EnCokSatanlarVM", "Sepetteki eski ürün silinemedi: ${mevcutUrun.ad}")
                    }
                } else {
                    urepo.sepeteEkle(urun, 1)
                    Log.d("EnCokSatanlarVM", "${urun.ad} sepete yeni eklendi.")
                }
            } catch (e: Exception) {
                Log.e("EnCokSatanlarVM", "Sepete eklenemedi: ${e.message}", e)
            }
        }
    }
}