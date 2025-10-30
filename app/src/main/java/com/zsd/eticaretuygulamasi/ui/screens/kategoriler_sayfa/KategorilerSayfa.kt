package com.zsd.eticaretuygulamasi.ui.screens.kategoriler_sayfa

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.ui.composables.UrunKarti
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategorilerSayfa(
    navController: NavController,
    sepetViewModel: SepetSayfaViewModel
) {
    val viewModel: KategorilerViewModel = hiltViewModel()
    val kategoriler by viewModel.kategoriler.collectAsState()
    val seciliKategori by viewModel.seciliKategori.collectAsState()
    val filtrelenmisUrunler by viewModel.filtrelenmisUrunler.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Kategoriler", fontWeight = FontWeight.Bold) },
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
                placeholder = { Text("Kategoride ara...", fontSize = 14.sp) },
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
            Divider()

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                KategoriListesi(
                    kategoriler = kategoriler,
                    seciliKategori = seciliKategori,
                    onKategoriSec = { viewModel.selectKategori(it) },
                    modifier = Modifier.weight(0.35f)
                )

                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                UrunIzgarasi(
                    urunler = filtrelenmisUrunler,
                    seciliKategoriBaslik = seciliKategori ?: "Tüm Ürünler",
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    sepetViewModel = sepetViewModel,
                    onSepeteEkle = { viewModel.kategorilerdenSepeteEkle(it) },
                    modifier = Modifier.weight(0.65f)
                )
            }
        }
    }
}

@Composable
fun KategoriListesi(
    kategoriler: List<String>,
    seciliKategori: String?,
    onKategoriSec: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            KategoriSatiri(
                kategoriAdi = "Tümü",
                seciliMi = seciliKategori == null,
                onClick = { onKategoriSec(null) }
            )
        }
        items(kategoriler) { kategori ->
            KategoriSatiri(
                kategoriAdi = kategori,
                seciliMi = seciliKategori == kategori,
                onClick = { onKategoriSec(kategori) }
            )
        }
    }
}

@Composable
fun KategoriSatiri(
    kategoriAdi: String,
    seciliMi: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (seciliMi) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
    val contentColor = if (seciliMi) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor, RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.List,
            contentDescription = null,
            tint = contentColor.copy(alpha = if(seciliMi) 1f else 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = kategoriAdi,
            color = contentColor,
            fontWeight = if (seciliMi) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}



@Composable
fun UrunIzgarasi(
    urunler: List<Urun>,
    seciliKategoriBaslik: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    sepetViewModel: SepetSayfaViewModel,
    onSepeteEkle: (Urun) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxHeight()
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(this.maxLineSpan) }) {
            Text(
                text = seciliKategoriBaslik,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )
        }

        if (urunler.isEmpty()) {
            item(span = { GridItemSpan(this.maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Info, contentDescription = "Bilgi", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Bu kategoride ürün bulunamadı.", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(items = urunler, key = { urun -> urun.id }) { urun ->
                UrunKarti(
                    urun = urun,
                    onSepeteEkle = { secilenUrun ->
                        onSepeteEkle(secilenUrun)
                        scope.launch {
                            delay(300)
                            sepetViewModel.sepetiYukle()
                            snackbarHostState.showSnackbar(message = "${secilenUrun.ad} sepete eklendi!", duration = SnackbarDuration.Short)
                        }
                    },
                    onUrunTikla = { secilenUrun ->
                        try {
                            val urunJson = Gson().toJson(secilenUrun)
                            navController.navigate("detay_sayfa/$urunJson")
                        } catch (e: Exception){
                            Log.e("KategorilerSayfa", "Ürün JSON'a çevrilirken hata: ${e.message}")
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