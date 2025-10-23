package com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.SepetUrun
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KULLANICI_ADI = "zeynep_soydan" // Sabit kullanıcı adı

@HiltViewModel
class SepetSayfaViewModel @Inject constructor(private var urepo: UrunlerRepository) : ViewModel() {
    private val _sepetListesi = MutableStateFlow<List<SepetUrun>>(emptyList()) // Başlangıç değeri boş liste
    val sepetListesi: StateFlow<List<SepetUrun>> = _sepetListesi

    init {
        sepetiYukle() // ViewModel oluşturulduğunda sepeti yükle
    }

    fun sepetiYukle() {
        Log.d("SepetSayfaViewModel", "sepetiYukle fonksiyonu çağrıldı.")
        viewModelScope.launch {
            try {
                _sepetListesi.value = urepo.sepetiGetir()
                Log.d("SepetSayfaViewModel", "Sepet başarıyla yüklendi: ${_sepetListesi.value.size} ürün.")
            } catch (e: Exception) {
                // Hata mesajını null kontrolü yaparak yazdır
                Log.e("SepetSayfaViewModel", "Sepet yüklenirken hata oluştu: ${e.message ?: "Bilinmeyen Hata"}", e) // e objesini de logla
                _sepetListesi.value = emptyList() // Hata durumunda listeyi boşalt
            }
        }
    }

    fun sepettenSil(sepetId: Int) {
        Log.d("SepetSayfaViewModel", "sepettenSil fonksiyonu çağrıldı: Sepet ID=$sepetId")
        viewModelScope.launch {
            try {
                urepo.sepettenSil(sepetId)
                Log.d("SepetSayfaViewModel", "Ürün başarıyla silindi (API). Sepet yenileniyor...")
                sepetiYukle() // Silme işleminden sonra sepeti tekrar yükle
            } catch (e: Exception) {
                // Hata mesajını null kontrolü yaparak yazdır
                Log.e("SepetSayfaViewModel", "Ürün silinirken hata oluştu: ${e.message ?: "Bilinmeyen Hata"}", e) // e objesini de logla
            }
        }
    }
}

