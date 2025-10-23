package com.zsd.eticaretuygulamasi.data.repo

import android.util.Log
import com.zsd.eticaretuygulamasi.data.datasource.UrunlerDataSource
import com.zsd.eticaretuygulamasi.data.entity.SepetUrun
import com.zsd.eticaretuygulamasi.data.entity.Urun

class UrunlerRepository(private var uds: UrunlerDataSource) {
    suspend fun urunleriGetir(): List<Urun> = uds.urunleriGetir()

    // İşlem sonucunu (Boolean) döndürecek şekilde güncellendi
    suspend fun sepeteEkle(
        ad: String, resim: String, fiyat: Int, kategori: String, marka: String, siparisAdeti: Int
    ): Boolean {
        Log.d("UrunlerRepository", "sepeteEkle fonksiyonu çağrıldı: Ürün adı=$ad")
        return uds.sepeteEkle(ad, resim, fiyat, kategori, marka, siparisAdeti)
    }

    suspend fun sepetiGetir(): List<SepetUrun> = uds.sepetiGetir()

    // İşlem sonucunu (Boolean) döndürecek şekilde güncellendi
    suspend fun sepettenSil(sepetId: Int): Boolean {
        Log.d("UrunlerRepository", "sepettenSil fonksiyonu çağrıldı: Sepet ID=$sepetId")
        return uds.sepettenSil(sepetId)
    }
}