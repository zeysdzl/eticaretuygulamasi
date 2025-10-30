package com.zsd.eticaretuygulamasi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Anasayfa : BottomNavItem("Anasayfa", "anasayfa", Icons.Filled.Home, Icons.Outlined.Home)
    object Favoriler : BottomNavItem("Favoriler", "favoriler_sayfa", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object Sepet : BottomNavItem("Sepet", "sepet_sayfa", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Profil : BottomNavItem("Profil", "profil_sayfa", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)

    companion object {
        val items: List<BottomNavItem> = listOf(
            Anasayfa,
            Favoriler,
            Sepet,
            Profil
        )
    }
}