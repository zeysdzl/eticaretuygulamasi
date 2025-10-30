package com.zsd.eticaretuygulamasi.data.entity

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Siparis(
    val id: String = UUID.randomUUID().toString(),
    val tarih: String = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("tr")).format(Date()),
    val urunler: List<SepetUrun>
)