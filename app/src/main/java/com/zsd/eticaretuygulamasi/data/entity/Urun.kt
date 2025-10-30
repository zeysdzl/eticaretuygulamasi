package com.zsd.eticaretuygulamasi.data.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Urun(
    @SerializedName("id") var id: Int,
    @SerializedName("ad") var ad: String,
    @SerializedName("resim") var resim: String,
    @SerializedName("kategori") var kategori: String,
    @SerializedName("fiyat") var fiyat: Int,
    @SerializedName("marka") var marka: String,
    @Transient var isFavorite: Boolean = false
) : Serializable