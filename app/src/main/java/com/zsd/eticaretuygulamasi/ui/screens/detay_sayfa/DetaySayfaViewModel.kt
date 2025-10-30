package com.zsd.eticaretuygulamasi.ui.screens.detay_sayfa

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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
class DetaySayfaViewModel @Inject constructor(
    private val urepo: UrunlerRepository,
    private val favsRepo: FavsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _urun = MutableStateFlow<Urun?>(null)
    val urun: StateFlow<Urun?> = _urun.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        savedStateHandle.get<String>("urunJson")?.let { urunJson ->
            val urun = Gson().fromJson(urunJson, Urun::class.java)
            _urun.value = urun
            checkIfFavorite(urun)
        }
    }

    private fun checkIfFavorite(urun: Urun) {
        viewModelScope.launch {
            _isFavorite.value = favsRepo.isFavori(urun.id)
        }
    }

    fun toggleFavori() {
        _urun.value?.let { urun ->
            viewModelScope.launch {
                if (_isFavorite.value) {
                    favsRepo.removeFavori(urun)
                } else {
                    favsRepo.addFavori(urun)
                }
                _isFavorite.value = !_isFavorite.value
            }
        }
    }


    fun sepeteEkle(eklenecekAdet: Int) {
        _urun.value?.let { guncelUrun ->
            viewModelScope.launch {
                try {
                    val mevcutSepet = urepo.sepetiGetir()
                    val mevcutUrunSepette = mevcutSepet.find { it.ad == guncelUrun.ad }

                    if (mevcutUrunSepette != null) {

                        val yeniAdet = mevcutUrunSepette.siparisAdeti + eklenecekAdet
                        Log.d("DetaySayfaViewModel", "Ürün sepette bulundu. Adet güncelleniyor: ${mevcutUrunSepette.ad}, Yeni Adet: $yeniAdet")
                        val silmeBasarili = urepo.sepettenSil(mevcutUrunSepette.sepetId)
                        if (silmeBasarili) {
                            urepo.sepeteEkle(guncelUrun, yeniAdet)
                        } else {
                            Log.e("DetaySayfaViewModel", "Sepetteki eski ürün silinemedi: ${mevcutUrunSepette.ad}")
                            throw Exception("Sepetteki eski ürün silinemedi.")
                        }
                    } else {
                        Log.d("DetaySayfaViewModel", "Ürün sepette yok. Yeni ekleniyor: ${guncelUrun.ad}, Adet: $eklenecekAdet")
                        urepo.sepeteEkle(guncelUrun, eklenecekAdet)
                    }
                    Log.d("DetaySayfaViewModel", "Sepete ekleme işlemi API'ye gönderildi.")

                } catch (e: Exception) {
                    Log.e("DetaySayfaViewModel", "Sepete eklenirken/güncellenirken hata: ${e.message}", e)
                }
            }
        }
    }
}