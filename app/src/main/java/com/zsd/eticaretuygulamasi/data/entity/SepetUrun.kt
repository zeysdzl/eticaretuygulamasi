package com.zsd.eticaretuygulamasi.data.entity

import com.google.gson.annotations.SerializedName

data class SepetUrun(
    @SerializedName("sepetId") var sepetId: Int,
    @SerializedName("ad") var ad: String,
    @SerializedName("resim") var resim: String,
    @SerializedName("kategori") var kategori: String,
    @SerializedName("fiyat") var fiyat: Int,
    @SerializedName("marka") var marka: String,
    @SerializedName("siparisAdeti") var siparisAdeti: Int,
    @SerializedName("kullaniciAdi") var kullaniciAdi: String
)