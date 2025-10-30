package com.zsd.eticaretuygulamasi.data.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.SimpleDateFormat
import java.util.*

enum class MesajTipi { SIPARIS, SATICI, KAMPANYA, BILGILENDIRME }

data class Mesaj(
    val id: String = UUID.randomUUID().toString(),
    val tip: MesajTipi,
    val baslik: String,
    val kisaIcerik: String,
    val tarih: String = SimpleDateFormat("dd MMM, HH:mm", Locale("tr")).format(Date()),
    val okundu: Boolean = false
) {
    fun getIcon(): ImageVector {
        return when (tip) {
            MesajTipi.SIPARIS -> Icons.Outlined.ShoppingBag
            MesajTipi.SATICI -> Icons.Outlined.Storefront
            MesajTipi.KAMPANYA -> Icons.Outlined.LocalOffer
            MesajTipi.BILGILENDIRME -> Icons.Outlined.Info
        }
    }
}