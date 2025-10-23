package com.zsd.eticaretuygulamasi.ui.screens.favoriler_sayfa

import android.util.Log // Log importu
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // Liste için LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson // Gson importu (detaya gitmek için)
import com.zsd.eticaretuygulamasi.data.entity.Urun
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavorilerSayfa(navController: NavController) {
    val viewModel: FavorilerSayfaViewModel = hiltViewModel()
    val favoriListesi by viewModel.favoriUrunler.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Favorilerim") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFE4E1),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        if (favoriListesi.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.FavoriteBorder, // Boş kalp ikonu
                        contentDescription = "Favori Yok",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Henüz favori ürününüz yok.",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Beğendiğiniz ürünleri kalp ikonuna tıklayarak ekleyebilirsiniz.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Kartlar arası boşluk
            ) {
                items(
                    items = favoriListesi,
                    key = { urun -> urun.id }
                ) { urun ->
                    FavoriUrunKarti(
                        urun = urun,
                        onFavoriTikla = { secilenUrun ->
                            viewModel.toggleFavori(secilenUrun)
                            // Favori durumuna göre Snackbar mesajı
                            scope.launch {
                                // Bu sayfada her zaman favoriden çıkarılacak
                                snackbarHostState.showSnackbar(
                                    message = "${secilenUrun.ad} favorilerden çıkarıldı.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        // Karta tıklanınca Detay Sayfasına gitme
                        onUrunTikla = { secilenUrun ->
                            try {
                                val urunJson = Gson().toJson(secilenUrun)
                                navController.navigate("detay_sayfa/$urunJson")
                            } catch (e: Exception){
                                Log.e("FavorilerSayfa", "Ürün JSON'a çevrilirken hata: ${e.message}")
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Detay sayfasına gidilemedi.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    )
                    Divider(color = Color(0xFFFDE0DC)) // Ayırıcı çizgi
                }
            }
        }
    }
}


@Composable
fun FavoriUrunKarti(
    urun: Urun,
    onFavoriTikla: (Urun) -> Unit,
    onUrunTikla: (Urun) -> Unit
) {
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUrunTikla(urun) } // Satıra tıklama
            .padding(vertical = 12.dp), // Dikey padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = resimUrl,
            contentDescription = urun.ad,
            modifier = Modifier
                .size(80.dp) // Resim boyutu sabit
                .padding(end = 12.dp),
            contentScale = ContentScale.Fit
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = urun.ad, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = urun.kategori, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₺${urun.fiyat}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE91E63)
            )
        }
        // Favoriden çıkarma butonu
        IconButton(onClick = { onFavoriTikla(urun) }) {
            Icon(
                Icons.Filled.Favorite, // Her zaman dolu kalp
                contentDescription = "Favorilerden Çıkar",
                tint = Color(0xFFE91E63) // Her zaman pembe
            )
        }
    }
}

