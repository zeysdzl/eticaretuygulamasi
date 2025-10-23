package com.zsd.eticaretuygulamasi.data.entity

import com.google.gson.annotations.SerializedName

data class UrunlerCevap(
    @SerializedName("urunler") var urunler: List<Urun>,
    @SerializedName("success") var success: Int
)