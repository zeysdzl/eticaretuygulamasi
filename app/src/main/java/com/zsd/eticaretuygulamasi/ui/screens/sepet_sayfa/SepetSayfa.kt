package com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.data.entity.Adres
import com.zsd.eticaretuygulamasi.data.entity.OdemeYontemi
import com.zsd.eticaretuygulamasi.data.entity.SepetUrun
import com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.ProfilRotalari
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SepetSayfa(
    navController: NavController,
    viewModel: SepetSayfaViewModel
) {

    val sepetListesi by viewModel.sepetListesi.collectAsState()
    val seciliUrunIdleri by viewModel.seciliUrunIdleri.collectAsState()
    val subtotal by viewModel.toplamFiyat.collectAsState()
    val indirimTutari by viewModel.indirimTutari.collectAsState()
    val kuponKodu by viewModel.kuponKodu.collectAsState()
    val seciliAdres by viewModel.seciliAdres.collectAsState()
    val adresListesi by viewModel.adresListesi.collectAsState()
    val seciliOdemeYontemi by viewModel.seciliOdemeYontemi.collectAsState()
    val odemeListesi by viewModel.odemeListesi.collectAsState()

    var urunSilOnayDialogu by remember { mutableStateOf<SepetUrun?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val seciliUrunSayisi = sepetListesi.filter { seciliUrunIdleri.contains(it.sepetId) }.size
    val isSepetBos = sepetListesi.isEmpty()

    LaunchedEffect(Unit) {
        viewModel.adresleriYukle()
        viewModel.odemeYontemleriniYukle()
    }

    val shipping = "Ücretsiz Standart | 3-4 gün"
    val total = subtotal - indirimTutari
    val shippingCost = 0

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Sepetim") },
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
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            if (seciliAdres == null) {
                                snackbarHostState.showSnackbar(message = "Lütfen bir gönderim adresi seçin.", duration = SnackbarDuration.Short)
                                return@launch
                            }
                            if (seciliOdemeYontemi == null) {
                                snackbarHostState.showSnackbar(message = "Lütfen bir ödeme yöntemi seçin.", duration = SnackbarDuration.Short)
                                return@launch
                            }
                            if (seciliUrunSayisi > 0) {
                                val basarili = viewModel.siparisVer()
                                if (basarili) snackbarHostState.showSnackbar(message = "Siparişiniz Başarıyla Oluşturuldu!", duration = SnackbarDuration.Short)
                                else snackbarHostState.showSnackbar(message = "Hata: Sipariş tamamlama başarısız oldu.", duration = SnackbarDuration.Short)
                            } else {
                                snackbarHostState.showSnackbar(message = "Sipariş vermek için en az bir ürün seçmelisin.", duration = SnackbarDuration.Short)
                            }
                        }
                    },

                    enabled = !isSepetBos,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Siparişi Tamamla", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    AdresSecimDropdown(
                        adresListesi = adresListesi,
                        seciliAdres = seciliAdres,
                        onAdresSecildi = { viewModel.adresSec(it) },
                        onAdresEkleGit = { navController.navigate(ProfilRotalari.ADRESLER) }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
                    CheckoutRow(title = "TESLİMAT", value = shipping, onClick = {})
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
                    OdemeSecimDropdown(
                        odemeListesi = odemeListesi,
                        seciliOdemeYontemi = seciliOdemeYontemi,
                        onOdemeSecildi = { viewModel.odemeYontemiSec(it) },
                        onOdemeEkleGit = { navController.navigate(ProfilRotalari.ODEME_YONTEMLERI) }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
                    KuponRow(
                        kuponKodu = kuponKodu,
                        onApply = viewModel::kuponKoduUygula,
                        onClear = viewModel::kuponuTemizle
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end= 16.dp, top = 8.dp, bottom = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ÜRÜNLER", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    if (!isSepetBos) {
                        Text("FİYAT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isSepetBos) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Sepetin boş. Lütfen Anasayfadan ürün ekle.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = sepetListesi,
                    key = { urun -> urun.sepetId }
                ) { urun ->
                    SepetCheckoutKarti(
                        urun = urun,
                        isChecked = seciliUrunIdleri.contains(urun.sepetId),
                        onAdetDegistir = viewModel::adetGuncelle,
                        onSecimDegistir = viewModel::urunSeciminiDegistir,
                        onUrunSilClick = { urunSilOnayDialogu = urun },
                        onUrunTikla = {
                            val tamUrun = viewModel.getUrunByAd(urun.ad)
                            if (tamUrun != null) {
                                try {
                                    val urunJson = Gson().toJson(tamUrun)
                                    navController.navigate("detay_sayfa/$urunJson")
                                } catch (e: Exception) {
                                    Log.e("SepetSayfa", "Detay sayfasına gidilemedi: ${e.message}")
                                    scope.launch { snackbarHostState.showSnackbar("Ürün detayı açılamadı.", duration = SnackbarDuration.Short) }
                                }
                            } else {
                                Log.e("SepetSayfa", "Ürün detayı bulunamadı.")
                                scope.launch { snackbarHostState.showSnackbar("Ürün detayı bulunamadı.", duration = SnackbarDuration.Short) }
                            }
                        }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                }

                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        SummaryRow(title = "Ara Toplam ($seciliUrunSayisi)", value = "₺${String.format("%.2f", subtotal)}")
                        if (indirimTutari > 0) {
                            SummaryRow(title = "İndirim", value = "- ₺${String.format("%.2f", indirimTutari)}", valueColor = MaterialTheme.colorScheme.primary)
                        }
                        SummaryRow(title = "Kargo Toplamı", value = if (shippingCost == 0) "Ücretsiz" else "₺$shippingCost")

                        Spacer(modifier = Modifier.height(16.dp))

                        SummaryRow(title = "Toplam", value = "₺${String.format("%.2f", total)}", isTotal = true)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        urunSilOnayDialogu?.let { urun ->
            SilmeOnayDialogu(
                urun = urun,
                onDismiss = { urunSilOnayDialogu = null },
                onSil = {
                    viewModel.sepettenUrunSil(urun.sepetId)
                    urunSilOnayDialogu = null
                },
                onSilVeFavoriEkle = {
                    viewModel.sepettenSilVeFavorilereEkle(urun)
                    urunSilOnayDialogu = null
                }
            )
        }
    }
}

@Composable
fun SilmeOnayDialogu(
    urun: SepetUrun,
    onDismiss: () -> Unit,
    onSil: () -> Unit,
    onSilVeFavoriEkle: () -> Unit
) {
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Sepetten Kaldır", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
                    ) {
                        AsyncImage(
                            model = resimUrl,
                            contentDescription = urun.ad,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize().padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(urun.ad, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Text("Ürünü sepetten kaldırmak istediğine emin misiniz?", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom=8.dp, end = 16.dp, start = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onSilVeFavoriEkle,
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Sil ve Favorilere Ekle", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onSil,
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Sadece Sil", color = MaterialTheme.colorScheme.error, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            Row(modifier = Modifier.fillMaxWidth().padding(start=16.dp, bottom = 8.dp)){
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("İptal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdresSecimDropdown(
    adresListesi: List<Adres>,
    seciliAdres: Adres?,
    onAdresSecildi: (Adres) -> Unit,
    onAdresEkleGit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dropdownMenuOffsetY = 8.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "GÖNDERİM ADRESİ",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Box {
            Row(
                modifier = Modifier
                    .clickable {
                        if (adresListesi.isEmpty()) {
                            onAdresEkleGit()
                        } else {
                            expanded = true
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = seciliAdres?.baslik ?: "Adres Seç/Ekle",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
            ) {
                adresListesi.forEach { adres ->
                    DropdownMenuItem(
                        text = {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(adres.baslik, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Text(
                                    adres.adresSatiri,
                                    fontSize = 13.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        },
                        onClick = {
                            onAdresSecildi(adres)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Ekle", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Yeni Adres Ekle", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    onClick = {
                        expanded = false
                        onAdresEkleGit()
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdemeSecimDropdown(
    odemeListesi: List<OdemeYontemi>,
    seciliOdemeYontemi: OdemeYontemi?,
    onOdemeSecildi: (OdemeYontemi) -> Unit,
    onOdemeEkleGit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "ÖDEME",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Box {
            Row(
                modifier = Modifier
                    .clickable {
                        if (odemeListesi.isEmpty()) {
                            onOdemeEkleGit()
                        } else {
                            expanded = true
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = seciliOdemeYontemi?.getOzet() ?: "Yöntem Seç/Ekle",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            ) {
                odemeListesi.forEach { yontem ->
                    DropdownMenuItem(
                        text = { Text(yontem.getOzet(), fontSize = 15.sp) },
                        onClick = {
                            onOdemeSecildi(yontem)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Ekle", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Yeni Ödeme Yöntemi Ekle", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    onClick = {
                        expanded = false
                        onOdemeEkleGit()
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun CheckoutRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuponRow(
    kuponKodu: String?,
    onApply: (String) -> Unit,
    onClear: () -> Unit
) {
    var kuponText by remember { mutableStateOf(kuponKodu ?: "") }
    val isApplyEnabled = kuponText.isNotBlank() && kuponKodu == null
    val buttonColor = if (isApplyEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("PROMOSYON KODU", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))

        if (kuponKodu != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(kuponKodu, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                IconButton(onClick = onClear, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Kuponu Temizle", tint = MaterialTheme.colorScheme.primary)
                }
            }
        } else {
            TextField(
                value = kuponText,
                onValueChange = { kuponText = it },
                placeholder = { Text("Kupon Kodu", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                singleLine = true,
                modifier = Modifier.width(180.dp).height(48.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    Button(
                        onClick = { onApply(kuponText) },
                        enabled = isApplyEnabled,
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text("Uygula", fontSize = 12.sp)
                    }
                }
            )
        }
    }
}

@Composable
fun SepetCheckoutKarti(
    urun: SepetUrun,
    isChecked: Boolean,
    onAdetDegistir: (eskiSepetId: Int, yeniAdet: Int) -> Unit,
    onSecimDegistir: (sepetId: Int, isChecked: Boolean) -> Unit,
    onUrunSilClick: (urun: SepetUrun) -> Unit,
    onUrunTikla: () -> Unit
) {
    val resimUrl = "http://kasimadalan.pe.hu/urunler/resimler/${urun.resim}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onSecimDegistir(urun.sepetId, it) },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )

        Card(
            modifier = Modifier.size(70.dp).padding(start = 8.dp)
                .clickable { onUrunTikla() },
            shape = RoundedCornerShape(8.dp)
        ) {
            AsyncImage(
                model = resimUrl,
                contentDescription = urun.ad,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
                .clickable { onUrunTikla() }
        ) {
            Text(urun.marka, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text(urun.ad, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
            Text("Miktar: ${urun.siparisAdeti}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                IconButton(
                    onClick = { onAdetDegistir(urun.sepetId, urun.siparisAdeti - 1) },
                    modifier = Modifier.size(24.dp),
                    enabled = urun.siparisAdeti > 1
                ) {
                    Icon(
                        Icons.Default.RemoveCircleOutline,
                        contentDescription = "Azalt",
                        tint = if (urun.siparisAdeti > 1) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                Text("${urun.siparisAdeti}", modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { onAdetDegistir(urun.sepetId, urun.siparisAdeti + 1) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Artır", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            IconButton(
                onClick = { onUrunSilClick(urun) },
                modifier = Modifier.size(24.dp).padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Sil", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }

            Text(
                "₺${urun.fiyat * urun.siparisAdeti}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SummaryRow(title: String, value: String, isTotal: Boolean = false, valueColor: Color = MaterialTheme.colorScheme.onBackground) {
    val color = if (isTotal) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val weight = if (isTotal) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = color, fontWeight = weight, fontSize = 14.sp)
        Text(value, color = valueColor, fontWeight = weight, fontSize = 14.sp)
    }
}