package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.duzenle

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilDuzenleSayfa(
    navController: NavController,
    viewModel: ProfilDuzenleViewModel = hiltViewModel()
) {
    val profilBilgileri by viewModel.profilBilgileri.collectAsState()
    val context = LocalContext.current

    var isim by remember(profilBilgileri) { mutableStateOf(profilBilgileri.isim) }
    var email by remember(profilBilgileri) { mutableStateOf(profilBilgileri.email) }
    var telefon by remember(profilBilgileri) { mutableStateOf(profilBilgileri.telefon) }

    var isEmailEditing by remember { mutableStateOf(false) }
    var isTelefonEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profili Düzenle", fontWeight = FontWeight.Bold) },
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
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfilDuzenleSatiri(
                etiket = "Fotoğraf",
                icerik = {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PersonOutline,
                            contentDescription = "Profil Fotoğrafı",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                aksiyon = {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Değiştir",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                onClick = {
                    Toast.makeText(context, "Fotoğraf ekleyin", Toast.LENGTH_SHORT).show()
                    // TODO: Gerçek fotoğraf seçme (gallery/camera) ve yükleme işlevi eklenecek
                }
            )

            Divider()

            ProfilDuzenleInputSatiri(
                etiket = "İsim",
                value = isim,
                onValueChange = { isim = it },
                enabled = true,
                aksiyon = null
            )

            Divider()

            ProfilDuzenleInputSatiri(
                etiket = "E-posta",
                value = email,
                onValueChange = { email = it },
                enabled = isEmailEditing,
                aksiyon = {
                    Button(
                        onClick = {
                            if (isEmailEditing) {
                                val yeniBilgiler = profilBilgileri.copy(email = email)
                                viewModel.saveProfilBilgileri(yeniBilgiler)
                                Toast.makeText(context, "E-posta kaydedildi!", Toast.LENGTH_SHORT).show()
                            }
                            isEmailEditing = !isEmailEditing
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEmailEditing) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (isEmailEditing) "Kaydet" else "Düzenle")
                    }
                }
            )

            Divider()

            ProfilDuzenleInputSatiri(
                etiket = "Cep Telefonu Numarası",
                value = telefon,
                onValueChange = { telefon = it },
                enabled = isTelefonEditing,
                aksiyon = {
                    Button(
                        onClick = {
                            if (isTelefonEditing) {
                                val yeniBilgiler = profilBilgileri.copy(telefon = telefon)
                                viewModel.saveProfilBilgileri(yeniBilgiler)
                                Toast.makeText(context, "Telefon kaydedildi!", Toast.LENGTH_SHORT).show()
                            }
                            isTelefonEditing = !isTelefonEditing
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTelefonEditing) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (isTelefonEditing) "Kaydet" else "Düzenle")
                    }
                }
            )
            Divider()

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val sonBilgiler = profilBilgileri.copy(
                        isim = isim,
                        email = if (!isEmailEditing) profilBilgileri.email else email,
                        telefon = if (!isTelefonEditing) profilBilgileri.telefon else telefon
                    )
                    viewModel.saveProfilBilgileri(sonBilgiler)
                    Toast.makeText(context, "Profil bilgileri kaydedildi!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Değişiklikleri Kaydet", fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun ProfilDuzenleSatiri(
    etiket: String,
    icerik: @Composable () -> Unit,
    aksiyon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiket,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            icerik()
            Spacer(modifier = Modifier.width(8.dp))
            aksiyon?.invoke()
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilDuzenleInputSatiri(
    etiket: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    aksiyon: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiket,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.weight(0.4f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(0.6f),
            enabled = enabled,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = Color.Transparent,
                unfocusedBorderColor = if(enabled) MaterialTheme.colorScheme.outline.copy(alpha=0.5f) else Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )

        aksiyon?.let {
            Spacer(modifier = Modifier.width(8.dp))
            it()
        }
    }
}