package com.zsd.eticaretuygulamasi.data.entity

import java.util.UUID

data class OdemeYontemi(
    val id: String = UUID.randomUUID().toString(),
    val kartTuru: String,
    val sonDortHane: String,
    val kartSahibi: String
) {
    fun getOzet(): String {
        return "$kartTuru *$sonDortHane"
    }
}