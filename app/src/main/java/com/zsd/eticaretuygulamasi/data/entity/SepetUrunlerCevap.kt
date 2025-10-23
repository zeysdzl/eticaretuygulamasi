package com.zsd.eticaretuygulamasi.data.entity

import com.google.gson.annotations.SerializedName

data class SepetUrunlerCevap(
    @SerializedName("urunler_sepeti") var sepet_urunler: List<SepetUrun>?,
    @SerializedName("success") var success: Int
)