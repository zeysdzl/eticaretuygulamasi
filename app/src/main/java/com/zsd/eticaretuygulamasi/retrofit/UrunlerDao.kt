package com.zsd.eticaretuygulamasi.retrofit

import com.zsd.eticaretuygulamasi.data.entity.CRUDCevap
import com.zsd.eticaretuygulamasi.data.entity.SepetUrunlerCevap
import com.zsd.eticaretuygulamasi.data.entity.UrunlerCevap
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface UrunlerDao {
    @GET("urunler/tumUrunleriGetir.php")
    suspend fun urunleriGetir(): UrunlerCevap

    @POST("urunler/sepeteUrunEkle.php")
    @FormUrlEncoded
    suspend fun sepeteEkle(
        @Field("ad") ad: String,
        @Field("resim") resim: String,
        @Field("fiyat") fiyat: Int,
        @Field("kategori") kategori: String,
        @Field("marka") marka: String,
        @Field("siparisAdeti") siparisAdeti: Int,
        @Field("kullaniciAdi") kullaniciAdi: String
    ): CRUDCevap

    @POST("urunler/sepettekiUrunleriGetir.php")
    @FormUrlEncoded
    suspend fun sepetiGetir(
        @Field("kullaniciAdi") kullaniciAdi: String
    ): SepetUrunlerCevap

    @POST("urunler/sepettenUrunSil.php")
    @FormUrlEncoded
    suspend fun sepettenSil(
        @Field("sepetId") sepetId: Int,
        @Field("kullaniciAdi") kullaniciAdi: String
    ): CRUDCevap
}