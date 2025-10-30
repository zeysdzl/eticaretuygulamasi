package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.adres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Adres
import com.zsd.eticaretuygulamasi.data.repo.AdresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdreslerimViewModel @Inject constructor(
    private val adresRepo: AdresRepository
) : ViewModel() {

    private val _adresListesi = MutableStateFlow<List<Adres>>(emptyList())
    val adresListesi: StateFlow<List<Adres>> = _adresListesi.asStateFlow()

    init {
        loadAdresler()
    }

    fun loadAdresler() {
        viewModelScope.launch {
            _adresListesi.value = adresRepo.getAdresler()
        }
    }

    fun deleteAdres(adresId: String) {
        viewModelScope.launch {
            adresRepo.deleteAdres(adresId)
            loadAdresler()
        }
    }
}