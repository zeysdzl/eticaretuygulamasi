package com.zsd.eticaretuygulamasi.ui.screens.anasayfa

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.R
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.ui.composables.RatingBar
import com.zsd.eticaretuygulamasi.ui.composables.UrunKarti
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.border
import androidx.compose.ui.text.TextStyle


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Anasayfa(
    navController: NavController,
    viewModel: AnasayfaViewModel = hiltViewModel(),
    sepetViewModel: SepetSayfaViewModel
) {
    val urunListesi by viewModel.urunListesi.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit, sepetViewModel) {
        sepetViewModel.sepetiYukle()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(this.maxLineSpan) }) {
                Image(
                    painter = painterResource(id = R.drawable.pinkify_home),
                    contentDescription = "Pinkify Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(37.dp)
                        .padding(bottom = 3.dp),
                    alignment = Alignment.Center
                )
            }

            item(span = { GridItemSpan(this.maxLineSpan) }) {
                Column {
                    TopSection(
                        navController = navController,
                        searchQuery = searchQuery,
                        onSearchQueryChanged = viewModel::onSearchQueryChanged
                    )
                    BannerSection(navController = navController)
                }
            }

            if (urunListesi.isNotEmpty()) {
                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    TitleRow(title = "Ürünler")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }


            if (urunListesi.isEmpty()) {
                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = "Bilgi",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isBlank()) "Gösterilecek ürün bulunamadı." else "Arama sonucu bulunamadı.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(items = urunListesi, key = { urun -> urun.id }) { urun ->
                    UrunKarti(
                        urun = urun,
                        onSepeteEkle = { secilenUrun ->
                            viewModel.anasayfadanSepeteEkle(secilenUrun)
                            scope.launch {
                                kotlinx.coroutines.delay(300)
                                sepetViewModel.sepetiYukle()
                                snackbarHostState.showSnackbar(message = "${secilenUrun.ad} sepete eklendi!", duration = SnackbarDuration.Short)
                            }
                        },
                        onUrunTikla = { secilenUrun ->
                            try {
                                val urunJson = Gson().toJson(secilenUrun)
                                navController.navigate("detay_sayfa/$urunJson")
                            } catch (e: Exception){
                                Log.e("Anasayfa", "Ürün JSON'a çevrilirken hata: ${e.message}")
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

@Composable
fun TitleRow(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Tümünü Gör",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TopSection(
    navController: NavController,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {

    val filters = listOf("Önceden Gezdiklerin", "Kategoriler", "En Çok Satanlar")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()

                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Ara",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        )

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(text = filter, navController = navController)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSection(navController: NavController) {
    val bannerImages = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
    val pagerState = rememberPagerState(pageCount = { bannerImages.size })

    LaunchedEffect(Unit) {
        while(true) {
            delay(3000L)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                try { pagerState.animateScrollToPage(nextPage) } catch (e: Exception) { Log.w("Banner", "Animasyon iptal edildi.") }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 16.dp)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            BannerCard(
                imageRes = bannerImages[page],
                onClick = {
                    navController.navigate("banner_detay/${bannerImages[page]}")
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(pagerState.pageCount) { index ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}

@Composable
fun BannerCard(imageRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).aspectRatio(2.5f).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Image(painter = painterResource(id = imageRes), contentDescription = "Banner Image", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(text: String, navController: NavController) {
    val iconColor = MaterialTheme.colorScheme.primary

    AssistChip(
        onClick = {
            when (text) {
                "Önceden Gezdiklerin" -> navController.navigate("history_sayfa")
                "Kategoriler" -> navController.navigate("kategoriler_sayfa")
                "En Çok Satanlar" -> navController.navigate("encoksatanlar_sayfa")
            }
        },
        label = { Text(text) },
        leadingIcon = {
            when (text) {
                "Önceden Gezdiklerin" -> Icon(Icons.Outlined.History, contentDescription = "History", tint = iconColor)
                "Kategoriler" -> {
                    Icon(imageVector = Icons.Filled.List, contentDescription = "Kategoriler", tint = iconColor, modifier = Modifier.size(20.dp))
                }
                "En Çok Satanlar" -> Icon(Icons.Filled.Star, contentDescription = "En Çok Satanlar", tint = iconColor, modifier = Modifier.size(20.dp))
            }
        },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    )
}