package com.zsd.eticaretuygulamasi.ui.screens.detay_sayfa

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite // Dolu kalp ikonu eklendi
import androidx.compose.material.icons.filled.FavoriteBorder // Boş kalp ikonu eklendi
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zsd.eticaretuygulamasi.data.entity.Urun
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay // Gecikme için import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetaySayfa(
    navController: NavController
) {
    // ViewModel'i hiltViewModel ile alıyoruz, backStackEntry'den değil
    val viewModel: DetaySayfaViewModel = hiltViewModel()
    // ViewModel'deki urun State'ini kullanıyoruz
    val urunState by viewModel.urun.collectAsState() // collectAsState olarak alıyoruz
    // Favori durumunu ViewModel'den bir State olarak alalım ki değişiklikleri anlık görelim
    val isFavorite by viewModel.isFavorite.collectAsState() // collectAsState olarak alıyoruz

    var siparisAdeti by remember { mutableIntStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Toast veya başka context işlemleri için

    // Gelen ürün null ise veya yüklenirken bir hata mesajı gösterilebilir
    if (urunState == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Ürün Yüklenemedi") },
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
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Ürün bilgileri yüklenirken bir hata oluştu.")
            }
        }
        return
    }

    // Ürün null değilse devam et
    val urun = urunState!!
    val toplamFiyat = urun.fiyat * siparisAdeti
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Ürün Detayı") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = { // Favori ikonunu actions kısmına ekliyoruz
                    IconButton(onClick = {
                        viewModel.toggleFavori()
                        scope.launch {
                            val message = if (viewModel.isFavorite.value) { // .value ile erişiyoruz
                                "${urun.ad} favorilere eklendi."
                            } else {
                                "${urun.ad} favorilerden çıkarıldı."
                            }
                            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorilere Ekle/Çıkar",
                            tint = if (isFavorite) Color(0xFFE91E63) else Color.Gray // Rengi State'e göre ayarla
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFE4E1),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black // Action ikonlarının rengi (kalp ikonu dahil)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFFFE4E1))
            ) {
                AsyncImage(
                    model = resimUrl,
                    contentDescription = urun.ad,
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = urun.ad, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${urun.kategori} - ${urun.marka}", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedIconButton(onClick = { if (siparisAdeti > 1) siparisAdeti-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Azalt")
                }
                Text(text = "$siparisAdeti", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                OutlinedIconButton(onClick = { siparisAdeti++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Arttır")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Toplam: ₺$toplamFiyat", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = {
                        Log.d("DetaySayfa", "Sepete Ekle butonuna tıklandı: Ürün adı=${urun.ad}, Adet=$siparisAdeti")
                        viewModel.sepeteEkle(eklenecekAdet = siparisAdeti) // ViewModel'deki urun bilgisini kullanacak
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${urun.ad} ($siparisAdeti adet) sepete eklendi/güncellendi!",
                                duration = SnackbarDuration.Short
                            )
                            delay(500) // Snackbar'ın görünmesi için kısa bir bekleme
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text("Sepete Ekle", fontSize = 18.sp)
                }
            }
        }
    }
}

