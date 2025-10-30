package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.odeme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.OdemeYontemi
import com.zsd.eticaretuygulamasi.data.repo.OdemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OdemeYontemleriViewModel @Inject constructor(
    private val odemeRepo: OdemeRepository
) : ViewModel() {

    private val _odemeListesi = MutableStateFlow<List<OdemeYontemi>>(emptyList())
    val odemeListesi: StateFlow<List<OdemeYontemi>> = _odemeListesi.asStateFlow()

    init {
        loadOdemeYontemleri()
    }

    fun loadOdemeYontemleri() {
        viewModelScope.launch {
            _odemeListesi.value = odemeRepo.getOdemeYontemleri()
        }
    }

    fun deleteOdemeYontemi(odemeId: String) {
        viewModelScope.launch {
            odemeRepo.deleteOdemeYontemi(odemeId)
            loadOdemeYontemleri()
        }
    }
}