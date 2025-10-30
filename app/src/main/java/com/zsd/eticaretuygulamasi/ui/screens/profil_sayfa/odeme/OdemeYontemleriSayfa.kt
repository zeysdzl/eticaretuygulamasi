package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.odeme

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
import com.zsd.eticaretuygulamasi.data.entity.OdemeYontemi
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.ProfilRotalari

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdemeYontemleriSayfa(
    navController: NavController,
    viewModel: OdemeYontemleriViewModel = hiltViewModel()
) {
    val odemeListesi by viewModel.odemeListesi.collectAsState()
    var silinecekOdeme by remember { mutableStateOf<OdemeYontemi?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadOdemeYontemleri()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kayıtlı Ödeme Yöntemlerim", fontWeight = FontWeight.Bold) },
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
                onClick = { navController.navigate(ProfilRotalari.YENI_ODEME_YONTEMI) },
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
                Text(text = "Yeni Ödeme Yöntemi Ekle", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (odemeListesi.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kayıtlı ödeme yönteminiz bulunmamaktadır.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    items(odemeListesi, key = { it.id }) { odeme ->
                        OdemeYontemiKarti(
                            odemeYontemi = odeme,
                            onSil = { silinecekOdeme = odeme }
                        )
                    }
                }
            }
        }

        silinecekOdeme?.let { odeme ->
            AlertDialog(
                onDismissRequest = { silinecekOdeme = null },
                title = { Text("Ödeme Yöntemini Sil") },
                text = { Text("${odeme.getOzet()} ödeme yöntemini silmek istediğinize emin misiniz?") },
                confirmButton = {
                    TextButton(
                        onClick = {

                            viewModel.deleteOdemeYontemi(odeme.id)
                            silinecekOdeme = null
                        }
                    ) {
                        Text("Sil", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { silinecekOdeme = null }) {
                        Text("İptal")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}


@Composable
fun OdemeYontemiKarti(odemeYontemi: OdemeYontemi, onSil: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = odemeYontemi.kartSahibi,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = odemeYontemi.getOzet(),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onSil, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Ödeme Yöntemini Sil",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}