package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.siparisler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Siparis
import com.zsd.eticaretuygulamasi.data.repo.SiparisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SiparislerViewModel @Inject constructor(
    private val siparisRepo: SiparisRepository
) : ViewModel() {

    private val _siparisListesi = MutableStateFlow<List<Siparis>>(emptyList())
    val siparisListesi: StateFlow<List<Siparis>> = _siparisListesi.asStateFlow()

    init {
        loadSiparisler()
    }

    fun loadSiparisler() {
        viewModelScope.launch {
            _siparisListesi.value = siparisRepo.getSiparisler()
        }
    }
}