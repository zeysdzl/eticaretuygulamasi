package com.zsd.eticaretuygulamasi.data.repo

import android.util.Log
import com.zsd.eticaretuygulamasi.data.datasource.UrunlerDataSource
import com.zsd.eticaretuygulamasi.data.entity.SepetUrun
import com.zsd.eticaretuygulamasi.data.entity.Urun

class UrunlerRepository(private var uds: UrunlerDataSource) {

    suspend fun urunleriYukle(): List<Urun> {
        return uds.urunleriGetir()
    }

    suspend fun sepeteEkle(urun: Urun, siparisAdeti: Int): Boolean {
        Log.d("UrunlerRepository", "sepeteEkle (Urun objesi) çağrıldı: Ürün adı=${urun.ad}")
        return uds.sepeteEkle(
            ad = urun.ad,
            resim = urun.resim,
            fiyat = urun.fiyat,
            kategori = urun.kategori,
            marka = urun.marka,
            siparisAdeti = siparisAdeti
        )
    }

    suspend fun sepetiGetir(): List<SepetUrun> = uds.sepetiGetir()

    suspend fun sepettenSil(sepetId: Int): Boolean {
        Log.d("UrunlerRepository", "sepettenSil fonksiyonu çağrıldı: Sepet ID=$sepetId")
        return uds.sepettenSil(sepetId)
    }
}