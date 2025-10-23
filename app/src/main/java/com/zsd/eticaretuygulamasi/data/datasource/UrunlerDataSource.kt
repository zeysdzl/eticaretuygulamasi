package com.zsd.eticaretuygulamasi.data.datasource

import android.util.Log
import com.zsd.eticaretuygulamasi.data.entity.CRUDCevap
import com.zsd.eticaretuygulamasi.data.entity.SepetUrun
import com.zsd.eticaretuygulamasi.data.entity.SepetUrunlerCevap
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.retrofit.UrunlerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val KULLANICI_ADI = "zeynep_soydan" // Sabit kullanıcı adı

class UrunlerDataSource(private var udao: UrunlerDao) {
    suspend fun urunleriGetir(): List<Urun> = withContext(Dispatchers.IO) {
        return@withContext udao.urunleriGetir().urunler
    }

    // İşlem sonucunu (Boolean) döndürecek şekilde güncellendi
    suspend fun sepeteEkle(
        ad: String, resim: String, fiyat: Int, kategori: String, marka: String, siparisAdeti: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val cevap: CRUDCevap = udao.sepeteEkle(ad, resim, fiyat, kategori, marka, siparisAdeti, KULLANICI_ADI)
            Log.d("UrunlerDataSource", "Sepete Ekle Cevap: Success=${cevap.success}, Message=${cevap.message}")
            return@withContext cevap.success == 1 // Başarılı ise true döndür
        } catch (e: Exception) {
            Log.e("UrunlerDataSource", "Sepete Ekle sırasında API hatası: ${e.message ?: "Bilinmeyen Hata"}", e)
            return@withContext false // Hata durumunda false döndür
        }
    }

    suspend fun sepetiGetir(): List<SepetUrun> = withContext(Dispatchers.IO) {
        try {
            val cevap: SepetUrunlerCevap = udao.sepetiGetir(KULLANICI_ADI)
            Log.d("UrunlerDataSource", "Sepeti Getir Cevap: Success=${cevap.success}, Ürün Sayısı=${cevap.sepet_urunler?.size ?: 0}")
            if (cevap.sepet_urunler == null) {
                Log.w("UrunlerDataSource", "API'den gelen sepet listesi null.")
            }
            return@withContext cevap.sepet_urunler ?: emptyList()
        } catch (e: Exception) {
            Log.e("UrunlerDataSource", "Sepeti Getir sırasında API hatası: ${e.message ?: "Bilinmeyen Hata"}", e)
            return@withContext emptyList()
        }
    }

    // İşlem sonucunu (Boolean) döndürecek şekilde güncellendi
    suspend fun sepettenSil(sepetId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val cevap: CRUDCevap = udao.sepettenSil(sepetId, KULLANICI_ADI)
            Log.d("UrunlerDataSource", "Sepetten Sil Cevap: Success=${cevap.success}, Message=${cevap.message}")
            return@withContext cevap.success == 1 // Başarılı ise true döndür
        } catch (e: Exception) {
            Log.e("UrunlerDataSource", "Sepetten Sil sırasında API hatası: ${e.message ?: "Bilinmeyen Hata"}", e)
            return@withContext false // Hata durumunda false döndür
        }
    }
}

