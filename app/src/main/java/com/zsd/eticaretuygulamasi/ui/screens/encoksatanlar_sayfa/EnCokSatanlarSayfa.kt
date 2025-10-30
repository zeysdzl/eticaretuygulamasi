package com.zsd.eticaretuygulamasi.ui.screens.encoksatanlar_sayfa

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.ui.composables.RatingBar
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnCokSatanlarSayfa(
    navController: NavController,
    sepetViewModel: SepetSayfaViewModel
) {
    val viewModel: EnCokSatanlarViewModel = hiltViewModel()
    val urunListesi by viewModel.urunListesi.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("En Çok Satanlar", fontWeight = FontWeight.Bold) },
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("En çok satanlarda ara...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara", modifier = Modifier.size(20.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha=0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f),
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f),
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f),
                ),
                singleLine = true
            )

            if (urunListesi.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Info, contentDescription = "Bilgi", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = if(searchQuery.isBlank()) "Ürün bulunamadı." else "Arama sonucu bulunamadı.", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), textAlign = TextAlign.Center)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp)
                ) {
                    items(items = urunListesi, key = { urun -> urun.id }) { urun ->
                        EnCokSatanUrunKarti(
                            urun = urun,
                            onSepeteEkle = { secilenUrun ->
                                sepetViewModel.optimisticBadgeUpdate(1)
                                viewModel.enCokSatanlardanSepeteEkle(secilenUrun)
                                scope.launch {
                                    kotlinx.coroutines.delay(500)
                                    sepetViewModel.sepetiYukle()
                                    snackbarHostState.showSnackbar(message = "${secilenUrun.ad} sepete eklendi!", duration = SnackbarDuration.Short)
                                }
                            },
                            onUrunTikla = { secilenUrun ->
                                try {
                                    val urunJson = Gson().toJson(secilenUrun)
                                    navController.navigate("detay_sayfa/$urunJson")
                                } catch (e: Exception){
                                    Log.e("EnCokSatanlarSayfa", "Ürün JSON'a çevrilirken hata: ${e.message}")
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message = "Detay sayfasına gidilemedi.", duration = SnackbarDuration.Short)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnCokSatanUrunKarti(
    urun: Urun,
    onSepeteEkle: (Urun) -> Unit,
    onUrunTikla: (Urun) -> Unit
) {
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"
    val randomRating = remember(urun.id) { Random.nextInt(4, 6) }
    val randomReviewCount = remember(urun.id) { Random.nextInt(100, 1000) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUrunTikla(urun) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.1f))
            ) {
                AsyncImage(
                    model = resimUrl,
                    contentDescription = urun.ad,
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                    contentScale = ContentScale.Fit
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "Çok Satan",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = urun.marka,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = urun.ad,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    RatingBar(rating = randomRating, starSize = 13.dp)
                    Text(
                        text = "(${randomReviewCount})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "₺${urun.fiyat}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = { onSepeteEkle(urun) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.3f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Sepete Ekle",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}