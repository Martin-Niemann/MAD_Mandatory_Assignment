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
                        // this hopefully works
                        salesItemsLiveData.postValue(response.body())
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

    fun getSalesItemsByQueries(description: String, maxPrice: Int,
                               sellerEmail: String, sortBy: String) {
        salesItemStoreService
            .getSalesItemsByQueries(description, maxPrice, sellerEmail, sortBy)
            .enqueue(object : Callback<List<SalesItem>> {
                override fun onResponse(call: Call<List<SalesItem>>,
                                        response: Response<List<SalesItem>>)
                {
                    if (response.isSuccessful) {
                        // this hopefully works
                        salesItemsLiveData.postValue(response.body())
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
    }
}