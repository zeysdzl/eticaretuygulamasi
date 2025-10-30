package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.siparisler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zsd.eticaretuygulamasi.data.entity.Siparis
import com.zsd.eticaretuygulamasi.data.entity.SepetUrun

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiparislerSayfa(
    navController: NavController,
    viewModel: SiparislerViewModel = hiltViewModel()
) {
    val siparisListesi by viewModel.siparisListesi.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Önceki Siparişlerim", fontWeight = FontWeight.Bold) },
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
        if (siparisListesi.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.ShoppingBag,
                        contentDescription = "Sipariş Yok",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Henüz sipariş vermedin.", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(siparisListesi, key = { it.id }) { siparis ->
                    SiparisKarti(siparis = siparis)
                }
            }
        }
    }
}

@Composable
fun SiparisKarti(siparis: Siparis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sipariş Tarihi: ${siparis.tarih}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            siparis.urunler.forEach { urun ->
                SiparisUrunSatiri(urun = urun)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SiparisUrunSatiri(urun: SepetUrun) {
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            AsyncImage(
                model = resimUrl,
                contentDescription = urun.ad,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(urun.marka, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text(urun.ad, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
            Text("Adet: ${urun.siparisAdeti}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
        Text(
            "₺${urun.fiyat * urun.siparisAdeti}",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}