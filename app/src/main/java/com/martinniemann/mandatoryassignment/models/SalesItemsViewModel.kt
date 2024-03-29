package com.martinniemann.mandatoryassignment.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.martinniemann.mandatoryassignment.repository.SalesItemsRepository
import com.martinniemann.mandatoryassignment.repository.SalesItemsRepository.FilterFunction

class SalesItemsViewModel : ViewModel() {
    private val repository = SalesItemsRepository()
    val salesItemsLiveData: LiveData<List<SalesItem>> = repository.salesItemsLiveData
    val updateStatusLiveData: LiveData<Boolean> = repository.updateSalesItemsStatus
    val removeStatusLiveData: LiveData<Boolean> = repository.removeSalesItemStatus
    val errorMessageLiveData: LiveData<String> = repository.errorMessageLiveData
    val hasFetchedLiveData: LiveData<Boolean> = repository.isFinishedFetchingSalesItems

    operator fun get(index: Int): SalesItem? {
        return salesItemsLiveData.value?.get(index)
    }

    fun reload() {
        repository.getAllSalesItems(null, null)
    }

    fun add(salesItem: SalesItem) {
        repository.addSalesItem(salesItem)
    }

    fun delete(id: Int) {
        repository.removeSalesItem(id)
    }

    fun sortByPrice(direction: String) {
        repository.sortByPrice(direction)
    }

    fun sortByTime(direction: String) {
        repository.sortByTime(direction)
    }

    fun filterByDescription(description: String) {
        repository.getAllSalesItems(FilterFunction.DESCRIPTION, description)
    }

    fun filterByPrice(price: Int) {
        repository.getAllSalesItems(FilterFunction.PRICE, price.toString())
    }

    fun filterByEmail(email: String) {
        repository.getAllSalesItems(FilterFunction.EMAIL, email)
    }
}