
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CustomerPurchaseHistoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val customerId: String = savedStateHandle.get<String>("customerId")!!

    val sales = saleRepository.getSalesForCustomer(customerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}
