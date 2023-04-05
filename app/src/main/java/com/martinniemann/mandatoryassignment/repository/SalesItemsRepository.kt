package com.martinniemann.mandatoryassignment.repository

import androidx.lifecycle.MutableLiveData
import com.martinniemann.mandatoryassignment.models.SalesItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SalesItemsRepository {
    enum class FilterFunction() {
        DESCRIPTION(),
        PRICE(),
        EMAIL()
    }

    private val url = "https://anbo-salesitems.azurewebsites.net/api/"

    private val salesItemStoreService: SalesItemStoreService
    val salesItemsLiveData: MutableLiveData<List<SalesItem>> = MutableLiveData<List<SalesItem>>()
    val errorMessageLiveData: MutableLiveData<String> = MutableLiveData()

    // these variables are only meant to be observed on reassignment,
    // and as such, their values hold no meaning
    val updateSalesItemsStatus: MutableLiveData<Boolean> = MutableLiveData()
    val removeSalesItemStatus: MutableLiveData<Boolean> = MutableLiveData()
    val isFinishedFetchingSalesItems: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val build: Retrofit = Retrofit
            .Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        salesItemStoreService = build.create(SalesItemStoreService::class.java)
    }

    fun getAllSalesItems(filterFunction: FilterFunction?, filterFunctionArgument: String?) {
        salesItemStoreService
            .getAllSalesItems()
            .enqueue(object : Callback<List<SalesItem>> {
                override fun onResponse(call: Call<List<SalesItem>>,
                                        response: Response<List<SalesItem>>)
                {
                    if (response.isSuccessful) {
                        if(response.body().isNullOrEmpty()) {
                            errorMessageLiveData.value = "Server returned no items."
                            return
                        }

                        if (filterFunction != null && filterFunctionArgument != null) {
                            when(filterFunction) {
                                FilterFunction.DESCRIPTION -> {filterByDescription(response.body()!!, filterFunctionArgument)}
                                FilterFunction.PRICE -> {filterByPrice(response.body()!!, filterFunctionArgument)}
                                FilterFunction.EMAIL -> {filterByEmail(response.body()!!, filterFunctionArgument)}
                            }
                            isFinishedFetchingSalesItems.value = true
                        } else {
                            salesItemsLiveData.value = response.body()
                            isFinishedFetchingSalesItems.value = true
                        }

                    } else {
                        errorMessageLiveData.postValue(
                            response.code().toString() + " " + response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<List<SalesItem>>, t: Throwable) {
                    errorMessageLiveData.postValue(t.message)
                }
            })
    }

    fun addSalesItem(salesItem: SalesItem) {
        salesItemStoreService
            .postSalesItem(salesItem)
            .enqueue(object : Callback<SalesItem> {
                override fun onResponse(call: Call<SalesItem>,
                                        response: Response<SalesItem>)
                {
                    if(response.isSuccessful) {
                        updateSalesItemsStatus.postValue(true)
                    } else {
                        errorMessageLiveData.postValue(
                            response.code().toString() + " " + response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<SalesItem>, t: Throwable) {
                    errorMessageLiveData.postValue(t.message)
                }
            })
    }

    fun removeSalesItem(id: Int) {
        salesItemStoreService
            .deleteSalesItem(id)
            .enqueue(object : Callback<SalesItem> {
                override fun onResponse(call: Call<SalesItem>,
                                        response: Response<SalesItem>)
                {
                    if(response.isSuccessful) {
                        removeSalesItemStatus.postValue(true)
                    } else {
                        errorMessageLiveData.postValue(
                            response.code().toString() + " " + response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<SalesItem>, t: Throwable) {
                    errorMessageLiveData.postValue(t.message)
                }
            })
    }

    fun sortByPrice(direction: String) {
        if (direction == "Lowest") {
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedBy { it.price }
        } else if(direction == "Highest")  {
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedByDescending { it.price }
        }
    }

    fun sortByTime(direction: String) {
        if (direction == "Lowest") {
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedBy { it.time }
        } else if(direction == "Highest")  {
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedByDescending { it.time }
        }
    }

    private fun filterByDescription(serverResponse: List<SalesItem>, description: String) {
        salesItemsLiveData.value = serverResponse.filter { item -> item.description.contains(description) }
    }

    private fun filterByPrice(serverResponse: List<SalesItem>, price: String) {
        val priceToInt = price.toInt()
        salesItemsLiveData.value = serverResponse.filter { item -> item.price <= priceToInt }
    }

    private fun filterByEmail(serverResponse: List<SalesItem>, email: String) {
        salesItemsLiveData.value = serverResponse.filter { item -> item.sellerEmail == email }
    }
}