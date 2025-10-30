package com.zsd.eticaretuygulamasi.ui.screens.profil_sayfa.odeme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.OdemeYontemi
import com.zsd.eticaretuygulamasi.data.repo.OdemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YeniOdemeYontemiViewModel @Inject constructor(
    private val odemeRepo: OdemeRepository
) : ViewModel() {

    fun saveOdemeYontemi(odemeYontemi: OdemeYontemi) {
        viewModelScope.launch {
            odemeRepo.saveOdemeYontemi(odemeYontemi)
        }
    }
}