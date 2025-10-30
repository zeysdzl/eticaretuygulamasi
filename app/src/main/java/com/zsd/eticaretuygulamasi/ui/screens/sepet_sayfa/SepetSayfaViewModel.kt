package com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Adres
import com.zsd.eticaretuygulamasi.data.entity.OdemeYontemi
import com.zsd.eticaretuygulamasi.data.entity.SepetUrun
import com.zsd.eticaretuygulamasi.data.entity.Siparis
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.AdresRepository
import com.zsd.eticaretuygulamasi.data.repo.FavsRepository
import com.zsd.eticaretuygulamasi.data.repo.OdemeRepository
import com.zsd.eticaretuygulamasi.data.repo.SiparisRepository
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SepetSayfaViewModel @Inject constructor(
    private val urepo: UrunlerRepository,
    private val favsRepo: FavsRepository,
    private val adresRepo: AdresRepository,
    private val siparisRepo: SiparisRepository,
    private val odemeRepo: OdemeRepository
) : ViewModel() {


    private val _sepetListesi = MutableStateFlow<List<SepetUrun>>(emptyList())
    val sepetListesi: StateFlow<List<SepetUrun>> = _sepetListesi.asStateFlow()

    private val _seciliUrunIdleri = MutableStateFlow<List<Int>>(emptyList())
    val seciliUrunIdleri: StateFlow<List<Int>> = _seciliUrunIdleri.asStateFlow()

    private val _toplamFiyat = MutableStateFlow(0.0)
    val toplamFiyat: StateFlow<Double> = _toplamFiyat.asStateFlow()

    private val _indirimTutari = MutableStateFlow(0.0)
    val indirimTutari: StateFlow<Double> = _indirimTutari.asStateFlow()

    private val _kuponKodu = MutableStateFlow<String?>(null)
    val kuponKodu: StateFlow<String?> = _kuponKodu.asStateFlow()

    private val _toplamUrunAdedi = MutableStateFlow(0)
    val toplamUrunAdedi: StateFlow<Int> = _toplamUrunAdedi.asStateFlow()

    private var _tumUrunlerListesi = emptyList<Urun>()
    private val KUPONLAR = mapOf(
        "PNKFYT10" to ("Teknoloji" to 0.10),
        "PNKFYA30" to ("Aksesuar" to 0.30),
        "PNKFYK25" to ("Kozmetik" to 0.25)
    )

    private val _adresListesi = MutableStateFlow<List<Adres>>(emptyList())
    val adresListesi: StateFlow<List<Adres>> = _adresListesi.asStateFlow()
    private val _seciliAdres = MutableStateFlow<Adres?>(null)
    val seciliAdres: StateFlow<Adres?> = _seciliAdres.asStateFlow()

    private val _odemeListesi = MutableStateFlow<List<OdemeYontemi>>(emptyList())
    val odemeListesi: StateFlow<List<OdemeYontemi>> = _odemeListesi.asStateFlow()
    private val _seciliOdemeYontemi = MutableStateFlow<OdemeYontemi?>(null)
    val seciliOdemeYontemi: StateFlow<OdemeYontemi?> = _seciliOdemeYontemi.asStateFlow()


    init {
        sepetiYukle()
        tumUrunleriGetir()
        adresleriYukle()
        odemeYontemleriniYukle()
    }


    fun optimisticBadgeUpdate(eklenecekAdet: Int) {
        val mevcutAdet = _toplamUrunAdedi.value
        _toplamUrunAdedi.value = mevcutAdet + eklenecekAdet
        Log.d("SepetVM", "Optimistic badge update: +$eklenecekAdet -> ${_toplamUrunAdedi.value}")
    }


    private fun tumUrunleriGetir() {
        viewModelScope.launch {
            try {
                _tumUrunlerListesi = urepo.urunleriYukle()
            } catch (e: Exception) {
                Log.e("SepetVM", "Tüm ürünler yüklenemedi: ${e.message}")
            }
        }
    }

    fun getUrunByAd(ad: String): Urun? {
        return _tumUrunlerListesi.find { it.ad == ad }
    }

    private fun sepetUrunToUrun(sepetUrun: SepetUrun): Urun? {
        val eslesenUrun = getUrunByAd(sepetUrun.ad)
        if (eslesenUrun == null) {
            Log.e("SepetVM", "${sepetUrun.ad} adlı ürün tüm ürünler listesinde bulunamadı.")
            return null
        }
        return eslesenUrun
    }

    fun sepetiYukle() {
        viewModelScope.launch {
            try {
                val mevcutSecim = _seciliUrunIdleri.value
                val liste = urepo.sepetiGetir().sortedBy { it.sepetId }
                _sepetListesi.value = liste

                val yeniEklenenIdler = liste.map { it.sepetId }.filterNot { mevcutSecim.contains(it) }

                _seciliUrunIdleri.value = if(mevcutSecim.isEmpty() && liste.isNotEmpty()) {
                    liste.map { it.sepetId }
                } else {
                    (mevcutSecim + yeniEklenenIdler).distinct().filter { id -> liste.any { it.sepetId == id } }
                }

                val toplamAdetApi = liste.sumOf { it.siparisAdeti }
                _toplamUrunAdedi.value = toplamAdetApi
                Log.d("SepetVM", "Sepet yüklendi, gerçek toplam adet: $toplamAdetApi")

                hesaplamalariGuncelle()
            } catch (e: Exception) {
                _sepetListesi.value = emptyList()
                _seciliUrunIdleri.value = emptyList()
                _toplamUrunAdedi.value = 0
                hesaplamalariGuncelle()
                Log.e("SepetVM", "Sepet yüklenemedi: ${e.message}")
            }
        }
    }

    fun adresleriYukle() {
        viewModelScope.launch {
            _adresListesi.value = adresRepo.getAdresler()
            if (_adresListesi.value.isNotEmpty() && _seciliAdres.value == null) {
                _seciliAdres.value = _adresListesi.value.first()
            } else if (_adresListesi.value.isEmpty()) {
                _seciliAdres.value = null
            }
        }
    }

    fun adresSec(adres: Adres) {
        _seciliAdres.value = adres
    }

    fun adresSecById(adresId: String) {
        val adres = _adresListesi.value.find { it.id == adresId }
        adres?.let { _seciliAdres.value = it }
    }

    fun odemeYontemleriniYukle() {
        viewModelScope.launch {
            _odemeListesi.value = odemeRepo.getOdemeYontemleri()
            if (_odemeListesi.value.isNotEmpty() && _seciliOdemeYontemi.value == null) {
                _seciliOdemeYontemi.value = _odemeListesi.value.first()
            } else if (_odemeListesi.value.isEmpty()){
                _seciliOdemeYontemi.value = null
            }
        }
    }

    fun odemeYontemiSec(yontem: OdemeYontemi) {
        _seciliOdemeYontemi.value = yontem
    }

    private fun hesaplamalariGuncelle() {
        val seciliUrunler = _sepetListesi.value.filter { _seciliUrunIdleri.value.contains(it.sepetId) }
        val araToplam = seciliUrunler.sumOf { it.fiyat * it.siparisAdeti.toDouble() }
        _toplamFiyat.value = araToplam

        var hesaplananIndirim = 0.0
        val aktifKuponKodu = _kuponKodu.value
        if (aktifKuponKodu != null && KUPONLAR.containsKey(aktifKuponKodu)) {
            val (hedefKategori, indirimOrani) = KUPONLAR[aktifKuponKodu]!!
            seciliUrunler.forEach { sepetUrun ->
                val tamUrun = sepetUrunToUrun(sepetUrun)
                if (tamUrun != null && tamUrun.kategori.equals(hedefKategori, ignoreCase = true)) {
                    hesaplananIndirim += (sepetUrun.fiyat * sepetUrun.siparisAdeti) * indirimOrani
                }
            }
        }
        _indirimTutari.value = hesaplananIndirim

    }

    fun adetGuncelle(sepetId: Int, yeniAdet: Int) {
        if (yeniAdet < 1) return

        _sepetListesi.update { mevcutListe ->
            var adetDegisimi = 0
            val guncellenmisListe = mevcutListe.map { sepetUrun ->
                if (sepetUrun.sepetId == sepetId) {
                    adetDegisimi = yeniAdet - sepetUrun.siparisAdeti
                    sepetUrun.copy(siparisAdeti = yeniAdet)
                } else {
                    sepetUrun
                }
            }
            if (adetDegisimi != 0) {
                optimisticBadgeUpdate(adetDegisimi)
            }
            guncellenmisListe
        }
        hesaplamalariGuncelle()
        Log.d("SepetVM", "UI Adet Güncellendi: ID=$sepetId, Yeni Adet=$yeniAdet (API Çağrısı Yok)")
    }

    fun urunSeciminiDegistir(sepetId: Int, isChecked: Boolean) {
        _seciliUrunIdleri.update { mevcutListe ->
            if (isChecked) {
                if (!mevcutListe.contains(sepetId)) mevcutListe + sepetId else mevcutListe
            } else {
                mevcutListe.filterNot { it == sepetId }
            }
        }
        hesaplamalariGuncelle()
    }

    fun sepettenUrunSil(sepetId: Int) {
        viewModelScope.launch {
            try {
                val silinenUrun = _sepetListesi.value.find { it.sepetId == sepetId }
                val silmeBasarili = urepo.sepettenSil(sepetId)
                if (silmeBasarili) {
                    _seciliUrunIdleri.update { it.filterNot { id -> id == sepetId } }
                    silinenUrun?.let { optimisticBadgeUpdate(-it.siparisAdeti) }
                    sepetiYukle()
                } else {
                    Log.w("SepetVM", "API'den ürün silinemedi: ID=$sepetId")
                }
            } catch (e: Exception) {
                Log.e("SepetVM", "Ürün silinemedi: ${e.message}")
            }
        }
    }


    fun sepettenSilVeFavorilereEkle(urun: SepetUrun) {
        viewModelScope.launch {
            val tamUrun = sepetUrunToUrun(urun)
            var favoriEklendi = false
            if (tamUrun != null) {
                try {
                    Log.d("SepetVM", "Favorilere ekleniyor: ${tamUrun.ad}")
                    favsRepo.addFavori(tamUrun)
                    favoriEklendi = true
                    Log.d("SepetVM", "Favorilere eklendi: ${tamUrun.ad}")
                } catch (e: Exception) {
                    Log.e("SepetVM", "Favoriye eklenemedi: ${e.message}", e)
                }
            } else {
                Log.e("SepetVM", "Favoriye eklenemedi, ürün bilgisi bulunamadı: ${urun.ad}")
            }

            if (favoriEklendi || tamUrun == null) {
                Log.d("SepetVM", "Sepetten siliniyor: ${urun.ad} (ID: ${urun.sepetId})")
                sepettenUrunSil(urun.sepetId)
            } else {
                Log.w("SepetVM", "Favoriye ekleme başarısız olduğu için sepetten silme işlemi yapılmadı: ${urun.ad}")
            }
        }
    }

    fun kuponKoduUygula(kod: String) {
        val upperCaseKod = kod.uppercase()
        if (KUPONLAR.containsKey(upperCaseKod)) {
            _kuponKodu.value = upperCaseKod
            Log.d("SepetVM", "Geçerli kupon uygulandı: $upperCaseKod")
        } else {
            Log.d("SepetVM", "Geçersiz kupon denendi: $kod")
            _kuponKodu.value = null
        }
        hesaplamalariGuncelle()
    }

    fun kuponuTemizle() {
        _kuponKodu.value = null
        hesaplamalariGuncelle()
    }

    suspend fun siparisVer(): Boolean {
        val seciliUrunler = _sepetListesi.value.filter { _seciliUrunIdleri.value.contains(it.sepetId) }
        if (seciliUrunler.isEmpty()) {
            Log.w("SepetVM", "Sipariş verilecek seçili ürün yok.")
            return false
        }
        if (_seciliAdres.value == null) {
            Log.w("SepetVM", "Sipariş için seçili adres yok.")
            return false
        }
        if (_seciliOdemeYontemi.value == null) {
            Log.w("SepetVM", "Sipariş için seçili ödeme yöntemi yok.")
            return false
        }

        try {
            val yeniSiparis = Siparis(urunler = seciliUrunler)
            siparisRepo.saveSiparis(yeniSiparis)
            Log.i("SepetVM", "Yeni sipariş kaydedildi. ID: ${yeniSiparis.id}")

            seciliUrunler.forEach { urun ->
                val silmeBasarili = urepo.sepettenSil(urun.sepetId)
                if(!silmeBasarili) Log.w("SepetVM", "Sipariş verirken ${urun.ad} (ID: ${urun.sepetId}) silinemedi.")
            }
            _seciliUrunIdleri.value = emptyList()
            sepetiYukle()
            Log.i("SepetVM", "Sipariş başarıyla verildi. Adres: ${_seciliAdres.value?.baslik}, Ödeme: ${_seciliOdemeYontemi.value?.getOzet()}")
            return true
        } catch (e: Exception) {
            Log.e("SepetVM", "Sipariş verilirken hata: ${e.message}")
            return false
        }
    }

}