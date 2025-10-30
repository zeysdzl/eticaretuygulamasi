package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.duzenle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.ProfilBilgileri
import com.zsd.eticaretuygulamasi.data.repo.ProfilRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilDuzenleViewModel @Inject constructor(
    private val profilRepo: ProfilRepository
) : ViewModel() {

    private val _profilBilgileri = MutableStateFlow(ProfilBilgileri())
    val profilBilgileri: StateFlow<ProfilBilgileri> = _profilBilgileri.asStateFlow()

    init {
        loadProfilBilgileri()
    }

    private fun loadProfilBilgileri() {
        viewModelScope.launch {
            _profilBilgileri.value = profilRepo.getProfilBilgileri()
        }
    }

    fun saveProfilBilgileri(yeniBilgiler: ProfilBilgileri) {
        viewModelScope.launch {
            profilRepo.saveProfilBilgileri(yeniBilgiler)
            _profilBilgileri.value = yeniBilgiler
        }
    }
}