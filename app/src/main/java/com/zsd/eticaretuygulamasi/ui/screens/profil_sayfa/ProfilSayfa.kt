package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zsd.eticaretuygulamasi.data.entity.ProfilBilgileri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilSayfa(
    navController: NavController,
    viewModel: ProfilSayfaViewModel = hiltViewModel()
) {
    val profilBilgileri by viewModel.profilBilgileri.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfilBilgileri()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hesabım", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(ProfilRotalari.MUSTERI_HIZMETLERI) }) {
                        Icon(
                            Icons.Outlined.SupportAgent,
                            contentDescription = "Müşteri Hizmetleri",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                KullaniciKarti(
                    profil = profilBilgileri,
                    onClick = { navController.navigate(ProfilRotalari.PROFIL_DUZENLE) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ProfilMenuBaslik("Siparişlerin")
                ProfilMenuItem(
                    text = "Önceki Siparişlerin",
                    icon = Icons.Outlined.ShoppingBag,
                    onClick = { navController.navigate(ProfilRotalari.SIPARISLER) }
                )
                ProfilMenuItem(
                    text = "İncelemelerin",
                    icon = Icons.Outlined.StarOutline,
                    onClick = { navController.navigate(ProfilRotalari.INCELEMELER) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProfilMenuBaslik("Hesabın")
                ProfilMenuItem(
                    text = "Önceden Gezdiklerin",
                    icon = Icons.Outlined.History,
                    onClick = { navController.navigate("history_sayfa") }
                )
                ProfilMenuItem(
                    text = "Tanımlanan Kodların",
                    icon = Icons.Outlined.ConfirmationNumber,
                    onClick = { navController.navigate(ProfilRotalari.KUPONLAR) }
                )
                ProfilMenuItem(
                    text = "Mesajlar",
                    icon = Icons.Outlined.Email,
                    onClick = { navController.navigate(ProfilRotalari.MESAJLAR) }
                )
                ProfilMenuItem(
                    text = "Adreslerin",
                    icon = Icons.Outlined.LocationOn,
                    onClick = { navController.navigate(ProfilRotalari.ADRESLER) }
                )
                ProfilMenuItem(
                    text = "Kayıtlı Ödeme Yöntemlerin",
                    icon = Icons.Outlined.CreditCard,
                    onClick = { navController.navigate(ProfilRotalari.ODEME_YONTEMLERI) }
                )
            }
        }
    }
}

@Composable
fun KullaniciKarti(profil: ProfilBilgileri, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Person,
                contentDescription = "Profil",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = profil.isim,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = profil.email,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Profili Düzenle",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}


@Composable
fun ProfilMenuBaslik(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ProfilMenuItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Git",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }
}