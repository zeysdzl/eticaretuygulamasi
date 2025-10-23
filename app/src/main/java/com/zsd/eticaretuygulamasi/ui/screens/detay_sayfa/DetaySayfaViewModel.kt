package com.zsd.eticaretuygulamasi.ui.screens.detay_sayfa

import android.util.Log
import androidx.lifecycle.SavedStateHandle // SavedStateHandle importu
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson // Gson importu
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.FavorilerRepository
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // asStateFlow importu
import kotlinx.coroutines.flow.update // update importu
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetaySayfaViewModel @Inject constructor(
    private var urepo: UrunlerRepository,
    private val favorilerRepo: FavorilerRepository,
    savedStateHandle: SavedStateHandle // Navigasyondan gelen argümanı almak için
) : ViewModel() {

    // Navigasyondan gelen ürün JSON'ını alıp Urun nesnesine çeviriyoruz
    private val _urun = MutableStateFlow<Urun?>(null) // Başlangıçta null olabilir
    val urun: StateFlow<Urun?> = _urun.asStateFlow() // Dışarıya StateFlow olarak aç

    // Favori durumunu tutacak StateFlow
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow() // Dışarıya StateFlow olarak aç

    init {
        // Navigasyondan gelen 'urun' argümanını al
        val urunJson: String? = savedStateHandle.get<String>("urun")
        if (urunJson != null) {
            try {
                val gelenUrun = Gson().fromJson(urunJson, Urun::class.java)
                _urun.value = gelenUrun // StateFlow'u güncelle
                // Ürün yüklendikten sonra favori durumunu kontrol et
                _isFavorite.value = favorilerRepo.isFavori(gelenUrun.id)
                Log.d("DetaySayfaViewModel", "Ürün yüklendi: ${gelenUrun.ad}, Favori: ${_isFavorite.value}")
            } catch (e: Exception) {
                Log.e("DetaySayfaViewModel", "JSON parse hatası: ${e.message}")
                _urun.value = null // Hata durumunda null yap
            }
        } else {
            Log.e("DetaySayfaViewModel", "Navigasyondan 'urun' argümanı alınamadı.")
            _urun.value = null
        }
    }

    // Favori durumunu değiştirme fonksiyonu
    fun toggleFavori() {
        _urun.value?.let { currentUrun -> // urun null değilse devam et
            val yeniFavoriDurumu = favorilerRepo.toggleFavori(currentUrun.id)
            _isFavorite.value = yeniFavoriDurumu // StateFlow'u güncelle
            Log.d("DetaySayfaViewModel", "${currentUrun.ad} favori durumu değiştirildi: $yeniFavoriDurumu")
        }
    }

    // Sepete ekleme fonksiyonu (artık urun nesnesini parametre olarak almıyor)
    fun sepeteEkle(eklenecekAdet: Int) {
        _urun.value?.let { currentUrun -> // urun null değilse devam et
            Log.d("DetaySayfaViewModel", "sepeteEkle fonksiyonu çağrıldı: Ürün adı=${currentUrun.ad}, Eklenecek Adet=$eklenecekAdet")
            viewModelScope.launch {
                try {
                    val mevcutSepet = urepo.sepetiGetir()
                    val mevcutUrun = mevcutSepet.find { it.ad == currentUrun.ad }

                    if (mevcutUrun != null) {
                        val yeniAdet = mevcutUrun.siparisAdeti + eklenecekAdet
                        Log.d("DetaySayfaViewModel", "${currentUrun.ad} sepette bulundu. Adet güncelleniyor: ${mevcutUrun.siparisAdeti} -> $yeniAdet")
                        val silmeBasarili = urepo.sepettenSil(mevcutUrun.sepetId)
                        if (silmeBasarili) {
                            Log.d("DetaySayfaViewModel", "Eski kayıt silindi. Yeni adetle ekleniyor...")
                            val eklemeBasarili = urepo.sepeteEkle(
                                currentUrun.ad, currentUrun.resim, currentUrun.fiyat, currentUrun.kategori, currentUrun.marka, yeniAdet
                            )
                            if (eklemeBasarili) {
                                Log.d("DetaySayfaViewModel", "${currentUrun.ad} adeti güncellendi.")
                            } else {
                                Log.e("DetaySayfaViewModel", "Adet güncellenirken ekleme başarısız oldu.")
                            }
                        } else {
                            Log.e("DetaySayfaViewModel", "Adet güncellenirken silme başarısız oldu.")
                        }
                    } else {
                        Log.d("DetaySayfaViewModel", "${currentUrun.ad} sepette bulunamadı. Yeni ürün olarak ekleniyor.")
                        val eklemeBasarili = urepo.sepeteEkle(
                            currentUrun.ad, currentUrun.resim, currentUrun.fiyat, currentUrun.kategori, currentUrun.marka, eklenecekAdet
                        )
                        if (eklemeBasarili) {
                            Log.d("DetaySayfaViewModel", "${currentUrun.ad} ($eklenecekAdet adet) sepete eklendi.")
                        } else {
                            Log.e("DetaySayfaViewModel", "Yeni ürün eklenirken hata oluştu.")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DetaySayfaViewModel", "Sepete eklerken genel hata: ${e.message ?: "Bilinmeyen Hata"}", e)
                }
            }
        } ?: Log.e("DetaySayfaViewModel", "Sepete ekleme başarısız: Ürün bilgisi null.") // urun null ise logla
    }
}

