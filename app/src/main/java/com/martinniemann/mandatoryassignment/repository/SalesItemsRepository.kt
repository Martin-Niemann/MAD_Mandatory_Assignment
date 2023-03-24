package com.martinniemann.mandatoryassignment.repository

import androidx.lifecycle.MutableLiveData
import com.martinniemann.mandatoryassignment.models.SalesItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SalesItemsRepository {
    private val url = "https://anbo-salesitems.azurewebsites.net/api/"

    private val salesItemStoreService: SalesItemStoreService
    val salesItemsLiveData: MutableLiveData<List<SalesItem>> = MutableLiveData<List<SalesItem>>()
    val errorMessageLiveData: MutableLiveData<String> = MutableLiveData()

    // these variables are only meant to be observed on reassignment,
    // and as such, their values hold no meaning
    val updateSalesItemsStatus: MutableLiveData<Boolean> = MutableLiveData()
    val removeSalesItemStatus: MutableLiveData<Boolean> = MutableLiveData()
    val isFinishedFetchingSalesItems: MutableLiveData<Boolean> = MutableLiveData()

    private val sortByPriceDirection: MutableLiveData<Boolean> = MutableLiveData(false)
    private val sortByTimeDirection: MutableLiveData<Boolean> = MutableLiveData(false)
    private val sortByEmailDirection: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        val build: Retrofit = Retrofit
            .Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        salesItemStoreService = build.create(SalesItemStoreService::class.java)
        getAllSalesItems()
    }

    fun getAllSalesItems() {
        salesItemStoreService
            .getAllSalesItems()
            .enqueue(object : Callback<List<SalesItem>> {
                override fun onResponse(call: Call<List<SalesItem>>,
                                        response: Response<List<SalesItem>>)
                {
                    if (response.isSuccessful) {
                        // TODO this hopefully works
                        salesItemsLiveData.postValue(response.body())
                        isFinishedFetchingSalesItems.postValue(true)
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
                        getAllSalesItems()
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

    fun sortByPrice() {
        if (sortByPriceDirection.value == false) {
            sortByPriceDirection.postValue(true)
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedBy { it.price }
        } else if(sortByPriceDirection.value == true)  {
            sortByPriceDirection.postValue(false)
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedByDescending { it.price }
        }
    }

    fun sortByTime() {
        if (sortByTimeDirection.value == false) {
            sortByTimeDirection.postValue(true)
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedBy { it.time }
        } else if(sortByTimeDirection.value == true)  {
            sortByTimeDirection.postValue(false)
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedByDescending { it.time }
        }
    }

    fun sortByEmail() {
        if (sortByEmailDirection.value == false) {
            sortByEmailDirection.postValue(true)
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedBy { it.sellerEmail }
        } else if(sortByEmailDirection.value == true)  {
            sortByEmailDirection.postValue(false)
            salesItemsLiveData.value = salesItemsLiveData.value?.sortedByDescending { it.sellerEmail }
        }
    }

    fun filterByDescription(description: String) {
        if (description.isBlank()) {
            getAllSalesItems()
        } else {
            getAllSalesItems()
            salesItemsLiveData.value = salesItemsLiveData.value?.filter { item -> item.description.contains(description) }
        }
    }

    fun filterByPrice(price: Int) {
        if (price < 0) {
            getAllSalesItems()
        } else {
            getAllSalesItems()
            salesItemsLiveData.value = salesItemsLiveData.value?.filter { item -> item.price <= price }
        }
    }

    fun filterByEmail(email: String) {
        if (email.isBlank()) {
            getAllSalesItems()
        } else {
            getAllSalesItems()
            salesItemsLiveData.value = salesItemsLiveData.value?.filter { item -> item.sellerEmail == email }
        }
    }
}