package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.odeme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zsd.eticaretuygulamasi.data.entity.OdemeYontemi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YeniOdemeYontemiSayfa(
    navController: NavController,
    viewModel: YeniOdemeYontemiViewModel = hiltViewModel()
) {
    var kartSahibi by remember { mutableStateOf("") }
    var kartTuru by remember { mutableStateOf("") }
    var sonDortHane by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Ödeme Yöntemi Ekle", fontWeight = FontWeight.Bold) },
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
                    value = kartSahibi,
                    onValueChange = { kartSahibi = it },
                    label = { Text("Kart Sahibinin Adı Soyadı") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = kartTuru,
                    onValueChange = { kartTuru = it },
                    label = { Text("Kart Türü (Visa, Mastercard vb.)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = sonDortHane,
                    onValueChange = { if (it.length <= 4) sonDortHane = it },
                    label = { Text("Kartın Son 4 Hanesi") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true
                )
            }

            Button(
                onClick = {
                    if (kartSahibi.isNotBlank() && kartTuru.isNotBlank() && sonDortHane.length == 4) {
                        val yeniYontem = OdemeYontemi(
                            kartSahibi = kartSahibi,
                            kartTuru = kartTuru,
                            sonDortHane = sonDortHane
                        )
                        viewModel.saveOdemeYontemi(yeniYontem)
                        Toast.makeText(context, "Ödeme yöntemi kaydedildi!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Lütfen tüm alanları doğru doldurun.", Toast.LENGTH_SHORT).show()
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
                Text(text = "Ödeme Yöntemini Kaydet", fontWeight = FontWeight.Bold)
            }
        }
    }
}