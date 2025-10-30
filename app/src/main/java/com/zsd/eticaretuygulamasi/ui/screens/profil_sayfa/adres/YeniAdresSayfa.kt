package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.adres

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zsd.eticaretuygulamasi.data.entity.Adres

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YeniAdresSayfa(
    navController: NavController,
    viewModel: YeniAdresViewModel = hiltViewModel()
) {
    var adresBaslik by remember { mutableStateOf("") }
    var adresSatiri by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Adres Ekle", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                OutlinedTextField(
                    value = adresBaslik,
                    onValueChange = { adresBaslik = it },
                    label = { Text("Adres Başlığı (Örn: Ev, İş)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = adresSatiri,
                    onValueChange = { adresSatiri = it },
                    label = { Text("Adres Satırı") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }

            Button(
                onClick = {
                    if (adresBaslik.isNotBlank() && adresSatiri.isNotBlank()) {
                        val yeniAdres = Adres(baslik = adresBaslik, adresSatiri = adresSatiri)
                        viewModel.saveAdres(yeniAdres)
                        Toast.makeText(context, "Adres kaydedildi!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = "Adresi Kaydet", fontWeight = FontWeight.Bold)
            }
        }
    }
}