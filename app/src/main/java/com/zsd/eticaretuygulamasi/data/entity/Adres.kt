package com.zsd.eticaretuygulamasi.data.entity

import java.util.UUID

data class Adres(
    val id: String = UUID.randomUUID().toString(),
    val baslik: String,
    val adresSatiri: String
)