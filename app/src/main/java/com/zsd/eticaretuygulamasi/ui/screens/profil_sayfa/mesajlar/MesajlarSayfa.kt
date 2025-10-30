package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.mesajlar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zsd.eticaretuygulamasi.data.entity.Mesaj

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesajlarSayfa(
    navController: NavController,
    viewModel: MesajlarViewModel = hiltViewModel()
) {
    val mesajListesi by viewModel.mesajListesi.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mesajlar", fontWeight = FontWeight.Bold) },
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
        if (mesajListesi.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = "Mesaj Yok",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Henüz mesajın yok.", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(mesajListesi, key = { it.id }) { mesaj ->
                    MesajKarti(
                        mesaj = mesaj,
                        onClick = {
                            // TODO: Mesaj detayına gitme veya okundu işaretleme
                            viewModel.markAsRead(mesaj.id)
                        }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(start = 72.dp))
                }
            }
        }
    }
}

@Composable
fun MesajKarti(mesaj: Mesaj, onClick: () -> Unit) {
    val iconColor = if (mesaj.okundu) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary
    val baslikWeight = if (mesaj.okundu) FontWeight.Normal else FontWeight.Bold
    val contentAlpha = if (mesaj.okundu) 0.7f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = mesaj.getIcon(),
                contentDescription = mesaj.tip.name,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mesaj.baslik,
                    fontWeight = baslikWeight,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = contentAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill=false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = mesaj.tarih,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = mesaj.kisaIcerik,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha * 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}