package com.zsd.eticaretuygulamasi.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel // hiltViewModel importu eklendi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zsd.eticaretuygulamasi.ui.screens.anasayfa.Anasayfa
import com.zsd.eticaretuygulamasi.ui.screens.detay_sayfa.DetaySayfa
import com.zsd.eticaretuygulamasi.ui.screens.detay_sayfa.DetaySayfaViewModel // ViewModel importu eklendi
import com.zsd.eticaretuygulamasi.ui.screens.favoriler_sayfa.FavorilerSayfa // FavorilerSayfa importu
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfa

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "anasayfa") {
        composable("anasayfa") {
            Anasayfa(navController = navController)
        }
        composable(
            "detay_sayfa/{urun}",
            arguments = listOf(navArgument("urun") { type = NavType.StringType })
        ) { backStackEntry -> // backStackEntry'yi alıyoruz
            // ViewModel'i hiltViewModel ile oluşturup DetaySayfa'ya iletiyoruz
            // Bu sayede SavedStateHandle argümanı otomatik alır
            val viewModel: DetaySayfaViewModel = hiltViewModel(backStackEntry)
            DetaySayfa(navController = navController) // Sadece navController gönderiyoruz
        }
        composable("sepet_sayfa") {
            SepetSayfa(navController = navController)
        }
        // Favoriler sayfası için yeni rota
        composable("favoriler_sayfa") {
            FavorilerSayfa(navController = navController)
        }
    }
}