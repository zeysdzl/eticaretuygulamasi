package com.zsd.eticaretuygulamasi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.ui.screens.anasayfa.Anasayfa
import com.zsd.eticaretuygulamasi.ui.screens.banner_detay.BannerDetaySayfa
import com.zsd.eticaretuygulamasi.ui.screens.detay_sayfa.DetaySayfa
import com.zsd.eticaretuygulamasi.ui.screens.encoksatanlar_sayfa.EnCokSatanlarSayfa
import com.zsd.eticaretuygulamasi.ui.screens.favoriler_sayfa.FavorilerSayfa
import com.zsd.eticaretuygulamasi.ui.screens.history_sayfa.HistorySayfa
import com.zsd.eticaretuygulamasi.ui.screens.kategoriler_sayfa.KategorilerSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.AdreslerimSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.KuponlarSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.mesajlar.MesajlarSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.musterihizmetleri.MusteriHizmetleriSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.odeme.OdemeYontemleriSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.PlaceholderSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.ProfilRotalari
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.ProfilSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.adres.AdresDuzenleSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.adres.YeniAdresSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.duzenle.ProfilDuzenleSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.incelemeler.IncelemelerSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.odeme.YeniOdemeYontemiSayfa
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.siparisler.SiparislerSayfa
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfa
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfaViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    sepetViewModel: SepetSayfaViewModel
) {
    val context = LocalContext.current
    val viewModelStoreOwner = context as ViewModelStoreOwner

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Anasayfa.route
    ) {
        composable(BottomNavItem.Anasayfa.route) {
            Anasayfa(navController = navController, sepetViewModel = sepetViewModel)
        }
        composable(BottomNavItem.Favoriler.route) {
            FavorilerSayfa(navController = navController, sepetViewModel = sepetViewModel)
        }
        composable(BottomNavItem.Sepet.route) {
            SepetSayfa(navController = navController, viewModel = sepetViewModel)
        }
        composable(BottomNavItem.Profil.route) {
            ProfilSayfa(navController = navController)
        }


        composable(
            route = "detay_sayfa/{urunJson}",
            arguments = listOf(navArgument("urunJson") { type = NavType.StringType })
        ) {
            DetaySayfa(navController = navController, sepetViewModel = sepetViewModel)
        }

        composable(
            route = "banner_detay/{imageResId}",
            arguments = listOf(navArgument("imageResId") { type = NavType.IntType })
        ) { backStackEntry ->
            val imageResId = backStackEntry.arguments?.getInt("imageResId") ?: 0
            BannerDetaySayfa(navController = navController, imageResId = imageResId)
        }

        composable("history_sayfa") {
            HistorySayfa(navController = navController)
        }

        composable("encoksatanlar_sayfa") {
            EnCokSatanlarSayfa(navController = navController, sepetViewModel = sepetViewModel)
        }
        composable("kategoriler_sayfa") {
            KategorilerSayfa(navController = navController, sepetViewModel = sepetViewModel)
        }


        composable(ProfilRotalari.SIPARISLER) {
            SiparislerSayfa(navController = navController)
        }
        composable(ProfilRotalari.INCELEMELER) {
            IncelemelerSayfa(navController = navController)
        }
        composable(ProfilRotalari.KUPONLAR) {
            KuponlarSayfa(navController = navController)
        }
        composable(ProfilRotalari.MESAJLAR) {
            MesajlarSayfa(navController = navController)
        }
        composable(ProfilRotalari.ADRESLER) {
            AdreslerimSayfa(navController = navController)
        }
        composable(ProfilRotalari.MUSTERI_HIZMETLERI) {
            MusteriHizmetleriSayfa(navController = navController)
        }
        composable(ProfilRotalari.PROFIL_DUZENLE) {
            ProfilDuzenleSayfa(navController = navController)
        }
        composable(ProfilRotalari.YENI_ADRES) {
            YeniAdresSayfa(navController = navController)
        }
        composable(ProfilRotalari.ODEME_YONTEMLERI) {
            OdemeYontemleriSayfa(navController = navController)
        }
        composable(ProfilRotalari.YENI_ODEME_YONTEMI) {
            YeniOdemeYontemiSayfa(navController = navController)
        }
        composable(
            route = "${ProfilRotalari.ADRES_DUZENLE}/{adresId}",
            arguments = listOf(navArgument("adresId") { type = NavType.StringType })
        ) {
            AdresDuzenleSayfa(navController = navController)
        }
    }
}