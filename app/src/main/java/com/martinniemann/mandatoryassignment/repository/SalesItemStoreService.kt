package com.martinniemann.mandatoryassignment.repository

import com.martinniemann.mandatoryassignment.models.SalesItem
import retrofit2.Call
import retrofit2.http.*

interface SalesItemStoreService {
    // this is necessary, as maxPrice cannot be null due to it being an integer,
    // and as maxPrice = 0 returns free items
    // one could change maxPrice into being a string and enforce this being a number
    @GET("SalesItems")
    fun getAllSalesItems(): Call<List<SalesItem>>

    @POST("SalesItems")
    fun postSalesItem(@Body salesItem: SalesItem): Call<SalesItem>

    @DELETE("SalesItems/{id}")
    fun deleteSalesItem(@Path("id") id: Int): Call<SalesItem>
}