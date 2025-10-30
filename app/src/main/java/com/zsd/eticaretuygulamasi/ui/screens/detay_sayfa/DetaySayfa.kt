package com.zsd.eticaretuygulamasi.ui.screens.detay_sayfa

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.zsd.eticaretuygulamasi.ui.composables.RatingBar
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfaViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetaySayfa(
    navController: NavController,
    sepetViewModel: SepetSayfaViewModel
) {
    val viewModel: DetaySayfaViewModel = hiltViewModel()
    val urunState by viewModel.urun.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    var siparisAdeti by remember { mutableIntStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


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
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
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


    val urun = urunState!!
    val toplamFiyat = urun.fiyat * siparisAdeti
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"
    val randomRating = remember(urun.id) { Random.nextInt(3, 6) }
    val randomReviewCount = remember(urun.id) { Random.nextInt(50, 500) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {

            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.toggleFavori()
                        scope.launch {
                            val message = if (viewModel.isFavorite.value) {
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
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            DetayBottomAppBar(
                siparisAdeti = siparisAdeti,
                onAdetAzalt = { if (siparisAdeti > 1) siparisAdeti-- },
                onAdetArtir = { siparisAdeti++ },
                onSepeteEkle = {
                    Log.d("DetaySayfa", "Sepete Ekle butonuna tıklandı: Ürün adı=${urun.ad}, Adet=$siparisAdeti")
                    sepetViewModel.optimisticBadgeUpdate(siparisAdeti)
                    viewModel.sepeteEkle(eklenecekAdet = siparisAdeti)
                    scope.launch {
                        delay(700)
                        sepetViewModel.sepetiYukle()
                        snackbarHostState.showSnackbar(
                            message = "${urun.ad} sepete eklendi!",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 72.dp)
        ) {
            item {
                AsyncImage(
                    model = resimUrl,
                    contentDescription = urun.ad,
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = urun.marka,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = urun.ad,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        RatingBar(rating = randomRating, starSize = 18.dp)
                        Text(
                            text = "($randomReviewCount)",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "₺${urun.fiyat}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Kargo Bedava",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF008000)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ürün Özellikleri",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OzellikSatiri(baslik = "Marka", deger = urun.marka)
                    OzellikSatiri(baslik = "Kategori", deger = urun.kategori)
                    OzellikSatiri(baslik = "Bağlantı Türü", deger = "Kablosuz")
                    OzellikSatiri(baslik = "Mikrofon", deger = "Var")
                    OzellikSatiri(baslik = "Garanti Tipi", deger = "Resmi Distribütör Garantili")
                    OzellikSatiri(baslik = "Garanti Süresi", deger = "2 Yıl")
                    OzellikSatiri(baslik = "ANC", deger = "Yok")
                }
            }
        }
    }
}

@Composable
fun OzellikSatiri(baslik: String, deger: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = baslik,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = deger,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun DetayBottomAppBar(
    siparisAdeti: Int,
    onAdetAzalt: () -> Unit,
    onAdetArtir: () -> Unit,
    onSepeteEkle: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        tonalElevation = 0.dp,
        modifier = Modifier.height(65.dp),
        contentPadding = PaddingValues(horizontal = 40.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top= 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = onAdetAzalt,
                    modifier = Modifier.size(30.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Azalt", modifier = Modifier.size(15.dp))
                }
                Text(
                    text = "$siparisAdeti",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                OutlinedIconButton(
                    onClick = onAdetArtir,
                    modifier = Modifier.size(30.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Arttır", modifier = Modifier.size(15.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSepeteEkle,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(45.dp),
                contentPadding = PaddingValues(horizontal = 15.dp)
            ) {
                Text("Sepete Ekle", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}