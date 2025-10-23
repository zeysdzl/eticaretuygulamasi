package com.zsd.eticaretuygulamasi.ui.screens.anasayfa

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // LaunchedEffect importu
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Anasayfa(
    navController: NavController,
    viewModel: AnasayfaViewModel = hiltViewModel(),
    sepetViewModel: SepetSayfaViewModel = hiltViewModel()
) {
    val urunListesi by viewModel.urunListesi.collectAsState()
    val sepetListesi by sepetViewModel.sepetListesi.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Sayfa açıldığında veya SepetViewModel değiştiğinde sepeti yükle
    LaunchedEffect(Unit, sepetViewModel) {
        sepetViewModel.sepetiYukle()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Pembe Dükkan") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFE4E1),
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                actions = {
                    // Favoriler İkonu
                    IconButton(onClick = { navController.navigate("favoriler_sayfa") }) {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = "Favorilerim")
                    }
                    // Sepet İkonu ve Rozet
                    BadgedBox(
                        badge = {
                            if (sepetListesi.isNotEmpty()) {
                                Badge(
                                    containerColor = Color(0xFFE91E63),
                                    contentColor = Color.White
                                ) {
                                    Text("${sepetListesi.size}")
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate("sepet_sayfa") }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Sepetim")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (urunListesi.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "Bilgi",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gösterilecek ürün bulunamadı.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = urunListesi,
                    key = { urun -> urun.id }
                ) { urun ->
                    UrunKarti(
                        urun = urun,
                        onSepeteEkle = { secilenUrun ->
                            viewModel.anasayfadanSepeteEkle(secilenUrun)
                            scope.launch {
                                // Sepeti yenilemek için kısa bir gecikme sonrası yükleme
                                kotlinx.coroutines.delay(300) // API işlemi için küçük bekleme
                                sepetViewModel.sepetiYukle()
                                snackbarHostState.showSnackbar(
                                    message = "${secilenUrun.ad} sepete eklendi!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        onUrunTikla = { secilenUrun ->
                            try {
                                val urunJson = Gson().toJson(secilenUrun)
                                navController.navigate("detay_sayfa/$urunJson")
                            } catch (e: Exception){
                                Log.e("Anasayfa", "Ürün JSON'a çevrilirken hata: ${e.message}")
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Detay sayfasına gidilemedi.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        onFavoriTikla = { secilenUrun ->
                            viewModel.toggleFavori(secilenUrun)
                            // Favori durumuna göre Snackbar mesajı
                            scope.launch {
                                val message = if (secilenUrun.isFavorite) {
                                    "${secilenUrun.ad} favorilerden çıkarıldı."
                                } else {
                                    "${secilenUrun.ad} favorilere eklendi."
                                }
                                snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UrunKarti(
    urun: Urun,
    onSepeteEkle: (Urun) -> Unit,
    onUrunTikla: (Urun) -> Unit,
    onFavoriTikla: (Urun) -> Unit
) {
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"

    Card(
        modifier = Modifier.clickable { onUrunTikla(urun) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFFFE4E1))
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = resimUrl,
                    contentDescription = urun.ad,
                    modifier = Modifier
                        .height(130.dp)
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { onFavoriTikla(urun) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (urun.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorilere Ekle/Çıkar",
                        tint = if (urun.isFavorite) Color(0xFFE91E63) else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = urun.ad,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₺${urun.fiyat}",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onSepeteEkle(urun) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    contentDescription = "Sepete Ekle",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Sepete Ekle")
            }
        }
    }
}

