package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zsd.eticaretuygulamasi.data.entity.Adres
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.adres.AdreslerimViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdreslerimSayfa(
    navController: NavController,
    viewModel: AdreslerimViewModel = hiltViewModel()
) {
    val adresListesi by viewModel.adresListesi.collectAsState()
    var silinecekAdres by remember { mutableStateOf<Adres?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAdresler()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adreslerim", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = { navController.navigate(ProfilRotalari.YENI_ADRES) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Yeni Adres Ekle", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (adresListesi.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kayıtlı adresiniz bulunmamaktadır.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(adresListesi, key = { it.id }) { adres ->
                        AdresKarti(
                            adres = adres,
                            onSil = { silinecekAdres = adres },
                            onClick = { navController.navigate("${ProfilRotalari.ADRES_DUZENLE}/${adres.id}") }
                        )
                    }
                }
            }
        }


        silinecekAdres?.let { adres ->
            AlertDialog(
                onDismissRequest = { silinecekAdres = null },
                title = { Text("Adresi Sil") },
                text = { Text("${adres.baslik} başlıklı adresi silmek istediğinize emin misiniz?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteAdres(adres.id)
                            silinecekAdres = null
                        }
                    ) {
                        Text("Sil", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { silinecekAdres = null }) {
                        Text("İptal")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun AdresKarti(adres: Adres, onSil: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = adres.baslik,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = adres.adresSatiri,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onSil, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Adresi Sil",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}