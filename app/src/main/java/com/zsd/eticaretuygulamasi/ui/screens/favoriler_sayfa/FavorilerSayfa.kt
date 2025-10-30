package com.zsd.eticaretuygulamasi.ui.screens.favoriler_sayfa

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.ui.composables.RatingBar
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfaViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavorilerSayfa(
    navController: NavController,
    sepetViewModel: SepetSayfaViewModel
) {
    val viewModel: FavorilerSayfaViewModel = hiltViewModel()
    val favoriListesi by viewModel.favoriUrunler.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.favoriIdleriniYukle()
    }

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
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
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
                        Icons.Filled.FavoriteBorder,
                        contentDescription = "Favori Yok",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Henüz favori ürünün yok.", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(items = favoriListesi, key = { urun -> urun.id }) { urun ->
                    FavoriUrunKartiDetay(
                        urun = urun,
                        onSepeteEkle = { secilenUrun ->
                            sepetViewModel.optimisticBadgeUpdate(1)
                            viewModel.favorilerdenSepeteEkle(secilenUrun)
                            scope.launch {
                                kotlinx.coroutines.delay(500)
                                sepetViewModel.sepetiYukle()
                                snackbarHostState.showSnackbar(message = "${secilenUrun.ad} sepete eklendi!", duration = SnackbarDuration.Short)
                            }
                        },
                        onFavoriTikla = { secilenUrun ->
                            viewModel.toggleFavori(secilenUrun)
                            scope.launch {
                                snackbarHostState.showSnackbar(message = "${secilenUrun.ad} favorilerden çıkarıldı.", duration = SnackbarDuration.Short)
                            }
                        },
                        onUrunTikla = { secilenUrun ->
                            try {
                                val urunJson = Gson().toJson(secilenUrun)
                                navController.navigate("detay_sayfa/$urunJson")
                            } catch (e: Exception){
                                Log.e("FavorilerSayfa", "Detay sayfasına gidilemedi.")
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun FavoriUrunKartiDetay(
    urun: Urun,
    onSepeteEkle: (Urun) -> Unit,
    onFavoriTikla: (Urun) -> Unit,
    onUrunTikla: (Urun) -> Unit
) {
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"
    val randomRating = remember(urun.id) { Random.nextInt(3, 6) }
    val randomReviewCount = remember(urun.id) { Random.nextInt(50, 500) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUrunTikla(urun) }
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                AsyncImage(
                    model = resimUrl,
                    contentDescription = urun.ad,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { onFavoriTikla(urun) },
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Favorilerden Çıkar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = urun.marka,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = urun.ad,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    RatingBar(rating = randomRating, starSize = 14.dp)
                    Text(
                        text = "(${randomReviewCount})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "₺${urun.fiyat}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Button(
                    onClick = { onSepeteEkle(urun) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Sepete Ekle",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Ekle", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    starSize: Dp = 14.dp,
    starColor: Color = Color(0xFFFFC107)
) {
    Row(modifier = modifier) {
        repeat(rating) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }
        repeat(5 - rating) {
            Icon(
                imageVector = Icons.Filled.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(starSize)
            )
        }
    }
}