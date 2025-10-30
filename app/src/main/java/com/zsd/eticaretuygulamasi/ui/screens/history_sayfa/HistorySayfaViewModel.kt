package com.zsd.eticaretuygulamasi.ui.screens.history_sayfa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zsd.eticaretuygulamasi.data.entity.Urun
import com.zsd.eticaretuygulamasi.data.repo.HistoryRepository
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorySayfaViewModel @Inject constructor(
    private val urepo: UrunlerRepository,
    private val historyRepo: HistoryRepository
) : ViewModel() {

    private val _historyUrunler = MutableStateFlow<List<Urun>>(emptyList())
    val historyUrunler: StateFlow<List<Urun>> = _historyUrunler.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                val allProducts = urepo.urunleriYukle()
                val historyIds = historyRepo.getHistory()

                val allProductsMap = allProducts.associateBy { it.id }

                val sortedHistoryList = historyIds.mapNotNull { allProductsMap[it] }

                _historyUrunler.value = sortedHistoryList
                Log.d("HistoryViewModel", "Geçmiş yüklendi: ${sortedHistoryList.size} ürün")
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Geçmiş yüklenirken hata: ${e.message}")
            }
        }
    }
}