package com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCartCheckout // Boş sepet ikonu için import
import androidx.compose.material3.*
import androidx.compose.runtime.* // rememberCoroutineScope için
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // TextAlign için import
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zsd.eticaretuygulamasi.data.entity.SepetUrun
import kotlinx.coroutines.launch // Coroutine launch için

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SepetSayfa(navController: NavController) {
    val viewModel = hiltViewModel<SepetSayfaViewModel>()
    val sepetListesi by viewModel.sepetListesi.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() } // Snackbar state
    val scope = rememberCoroutineScope() // Snackbar göstermek için scope

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // SnackbarHost eklendi
        topBar = {
            TopAppBar(
                title = { Text(text = "Sepetim") },
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
        },
        bottomBar = {
            // Sepet boşsa alt barı gösterme
            if (sepetListesi.isNotEmpty()) {
                val toplamTutar = remember(sepetListesi) {
                    sepetListesi.sumOf { it.fiyat * it.siparisAdeti } // Null kontrolüne gerek yok, liste boş değilse burası çalışır
                }
                BottomAppBar(
                    containerColor = Color(0xFFFFE4E1)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Toplam: ₺$toplamTutar",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Button(
                            onClick = {
                                Log.d("SepetSayfa", "Sepeti Onayla butonuna tıklandı.")
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Siparişiniz alındı! (Simülasyon)",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text("SEPETİ ONAYLA")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Sepet listesi boşsa farklı bir görünüm göster
        if (sepetListesi.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp), // Kenarlardan boşluk
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.ShoppingCartCheckout, // Daha uygun bir ikon
                        contentDescription = "Boş Sepet",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp) // Daha büyük ikon
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sepetiniz şu anda boş.",
                        fontSize = 18.sp, // Daha büyük yazı
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hemen alışverişe başlayın!",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Sepet listesi doluysa normal listeyi göster
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Dikey padding azaltıldı
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = sepetListesi,
                    key = { sepetUrun -> sepetUrun.sepetId }
                ) { sepetUrun ->
                    SepetUrunKarti(
                        sepetUrun = sepetUrun,
                        onSilTikla = {
                            viewModel.sepettenSil(sepetUrun.sepetId)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${sepetUrun.ad} sepetten silindi.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        })
                    Divider() // Kartlar arasına ayırıcı eklendi
                }
            }
        }
    }
}


@Composable
fun SepetUrunKarti(sepetUrun: SepetUrun, onSilTikla: () -> Unit) {
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${sepetUrun.resim}"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Gölge kaldırıldı
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Arka plan transparan
        // border = BorderStroke(1.dp, Color(0xFFFDE0DC)) // Kenarlık kaldırıldı
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp), // Dikey padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = resimUrl,
                contentDescription = sepetUrun.ad,
                modifier = Modifier
                    .size(60.dp) // Resim biraz daha küçültüldü
                    .padding(end = 12.dp),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sepetUrun.ad, fontWeight = FontWeight.Medium, fontSize = 16.sp) // Yazı kalınlığı azaltıldı
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Adet: ${sepetUrun.siparisAdeti}", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₺${sepetUrun.fiyat * sepetUrun.siparisAdeti}",
                    fontSize = 15.sp, // Fiyat boyutu azaltıldı
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black // Fiyat rengi siyah yapıldı
                )
            }
            IconButton(onClick = onSilTikla) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = Color.Gray
                )
            }
        }
    }
}

