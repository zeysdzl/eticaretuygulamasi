package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.adres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Adres
import com.zsd.eticaretuygulamasi.data.repo.AdresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YeniAdresViewModel @Inject constructor(
    private val adresRepo: AdresRepository
) : ViewModel() {

    fun saveAdres(adres: Adres) {
        viewModelScope.launch {
            adresRepo.saveAdres(adres)
        }
    }
}