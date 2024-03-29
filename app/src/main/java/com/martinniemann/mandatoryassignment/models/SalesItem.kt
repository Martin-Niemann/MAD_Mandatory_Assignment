package com.martinniemann.mandatoryassignment.models

import android.icu.text.DateFormat
import java.io.Serializable

data class SalesItem(val id: Int, val description: String, val price: Int, val sellerEmail: String,
                     val sellerPhone: String, val time: Long, val pictureUrl: String)
    : Serializable {
        constructor(description: String, price: Int, sellerEmail: String, sellerPhone: String, time: Long)
                : this(0, description, price, sellerEmail, sellerPhone, time, "")

    fun humanDate(): String {
        val formatter = DateFormat.getDateInstance()
        return formatter.format(time * 1000L)
    }
}