package com.zsd.eticaretuygulamasi.ui.screens.anasayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.FavorilerRepository // FavorilerRepository importu
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update // update fonksiyonu için import
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnasayfaViewModel @Inject constructor(
    private var urepo: UrunlerRepository,
    private val favorilerRepo: FavorilerRepository // FavorilerRepository enjekte edildi
) : ViewModel() {
    private val _urunListesi = MutableStateFlow<List<Urun>>(emptyList())
    val urunListesi: StateFlow<List<Urun>> = _urunListesi

    init {
        urunleriYukle()
    }

    private fun urunleriYukle() {
        viewModelScope.launch {
            try {
                // API'den ürünleri çek
                val apiUrunler = urepo.urunleriGetir()
                // Her ürünün favori durumunu kontrol et ve güncelle
                val guncelUrunler = apiUrunler.map { urun ->
                    urun.copy(isFavorite = favorilerRepo.isFavori(urun.id))
                }
                _urunListesi.value = guncelUrunler
            } catch (e: Exception) {
                Log.e("AnasayfaViewModel", "Ürünler yüklenirken hata oluştu: ${e.message ?: "Bilinmeyen Hata"}", e)
            }
        }
    }

    // Favori durumunu değiştirme fonksiyonu
    fun toggleFavori(urun: Urun) {
        val yeniFavoriDurumu = favorilerRepo.toggleFavori(urun.id)
        Log.d("AnasayfaViewModel", "${urun.ad} favori durumu değiştirildi: $yeniFavoriDurumu")

        // UI'daki listeyi anında güncelle
        _urunListesi.update { mevcutListe ->
            mevcutListe.map {
                if (it.id == urun.id) {
                    it.copy(isFavorite = yeniFavoriDurumu)
                } else {
                    it
                }
            }
        }
    }


    fun anasayfadanSepeteEkle(urun: Urun) {
        Log.d("AnasayfaViewModel", "anasayfadanSepeteEkle çağrıldı: Ürün adı=${urun.ad}")
        viewModelScope.launch {
            try {
                val mevcutSepet = urepo.sepetiGetir()
                val mevcutUrun = mevcutSepet.find { it.ad == urun.ad }

                if (mevcutUrun != null) {
                    val yeniAdet = mevcutUrun.siparisAdeti + 1
                    Log.d("AnasayfaViewModel", "${urun.ad} sepette bulundu. Adet güncelleniyor: ${mevcutUrun.siparisAdeti} -> $yeniAdet")
                    val silmeBasarili = urepo.sepettenSil(mevcutUrun.sepetId)
                    if (silmeBasarili) {
                        Log.d("AnasayfaViewModel", "Eski kayıt silindi. Yeni adetle ekleniyor...")
                        val eklemeBasarili = urepo.sepeteEkle(
                            urun.ad, urun.resim, urun.fiyat, urun.kategori, urun.marka, yeniAdet
                        )
                        if (eklemeBasarili) {
                            Log.d("AnasayfaViewModel", "${urun.ad} adeti güncellendi.")
                        } else {
                            Log.e("AnasayfaViewModel", "Adet güncellenirken ekleme başarısız oldu.")
                        }
                    } else {
                        Log.e("AnasayfaViewModel", "Adet güncellenirken silme başarısız oldu.")
                    }
                } else {
                    Log.d("AnasayfaViewModel", "${urun.ad} sepette bulunamadı. Yeni ürün olarak ekleniyor.")
                    val eklemeBasarili = urepo.sepeteEkle(
                        urun.ad, urun.resim, urun.fiyat, urun.kategori, urun.marka, 1
                    )
                    if (eklemeBasarili) {
                        Log.d("AnasayfaViewModel", "${urun.ad} anasayfadan sepete eklendi.")
                    } else {
                        Log.e("AnasayfaViewModel", "Yeni ürün eklenirken hata oluştu.")
                    }
                }
            } catch (e: Exception) {
                Log.e("AnasayfaViewModel", "Anasayfadan sepete eklerken genel hata: ${e.message ?: "Bilinmeyen Hata"}", e)
            }
        }
    }
}

