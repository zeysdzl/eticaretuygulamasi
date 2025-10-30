package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.adres

import androidx.lifecycle.SavedStateHandle
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
class AdresDuzenleViewModel @Inject constructor(
    private val adresRepo: AdresRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _adres = MutableStateFlow<Adres?>(null)
    val adres: StateFlow<Adres?> = _adres.asStateFlow()

    private val adresId: String? = savedStateHandle.get<String>("adresId")

    init {
        adresId?.let { loadAdres(it) }
    }

    private fun loadAdres(id: String) {
        viewModelScope.launch {
            _adres.value = adresRepo.getAdresById(id)
        }
    }

    fun updateAdres(guncellenmisAdres: Adres) {
        viewModelScope.launch {
            adresRepo.updateAdres(guncellenmisAdres)
        }
    }
}