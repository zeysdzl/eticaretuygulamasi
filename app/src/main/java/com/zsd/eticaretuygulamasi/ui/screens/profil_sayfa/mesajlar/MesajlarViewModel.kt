package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.mesajlar

import androidx.lifecycle.ViewModel
import com.zsd.eticaretuygulamasi.data.entity.Mesaj
import com.zsd.eticaretuygulamasi.data.entity.MesajTipi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MesajlarViewModel @Inject constructor() : ViewModel() {

    private val _mesajListesi = MutableStateFlow<List<Mesaj>>(emptyList())
    val mesajListesi: StateFlow<List<Mesaj>> = _mesajListesi.asStateFlow()

    init {
        loadDummyMessages()
    }

    private fun loadDummyMessages() {
        _mesajListesi.value = listOf(
            Mesaj(
                tip = MesajTipi.SIPARIS,
                baslik = "Siparişin Oluşturuldu!",
                kisaIcerik = "ABC123XYZ numaralı siparişin başarıyla oluşturuldu.",
                okundu = false
            ),
            Mesaj(
                tip = MesajTipi.KAMPANYA,
                baslik = "Aksesuar İndirimi Başladı!",
                kisaIcerik = "Tüm aksesuarlarda %30 indirim fırsatını kaçırma!",
                okundu = false
            ),
            Mesaj(
                tip = MesajTipi.SATICI,
                baslik = "Satıcıdan Mesajın Var",
                kisaIcerik = "Sipariş ettiğin ürünle ilgili satıcı sana bir mesaj gönderdi.",
                okundu = true
            ),
            Mesaj(
                tip = MesajTipi.BILGILENDIRME,
                baslik = "Hesap Bilgileri Güncellemesi",
                kisaIcerik = "Gizlilik politikamız güncellenmiştir.",
                okundu = true
            ),
            Mesaj(
                tip = MesajTipi.SIPARIS,
                baslik = "Siparişin Kargolandı",
                kisaIcerik = "DEF456LMN numaralı siparişin kargoya verildi.",
                okundu = true
            )
        ).sortedByDescending { it.tarih }
    }

    fun markAsRead(mesajId: String) {
        _mesajListesi.update { list ->
            list.map {
                if (it.id == mesajId) it.copy(okundu = true) else it
            }
        }
    }
}