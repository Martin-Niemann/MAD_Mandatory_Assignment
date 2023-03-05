package com.martinniemann.mandatoryassignment.repository

import com.martinniemann.mandatoryassignment.models.SalesItem
import retrofit2.Call
import retrofit2.http.*

interface SalesItemStoreService {
    @GET("SalesItems")
    fun getAllSalesItems(@Query("description") description: String,
                         @Query("maxPrice") maxPrice: Int,
                         @Query("sellerEmail") sellerEmail: String,
                         @Query("sort_by") sort_by: String): Call<List<SalesItem>>

    @POST("SalesItems")
    fun postSalesItem(@Body salesItem: SalesItem): Call<SalesItem>

    @DELETE("SalesItems/{id}")
    fun deleteSalesItem(@Path("id") id: Int): Call<SalesItem>
}