package com.martinniemann.mandatoryassignment.models

import java.io.Serializable

data class SalesItem(val id: Int, val description: String, val price: Int, val sellerEmail: String,
                     val sellerPhone: Int, val time: Long, val pictureUrl: String)
    : Serializable {
        constructor(description: String, price: Int, sellerEmail: String, sellerPhone: Int, time: Long)
                : this(0, description, price, sellerEmail, sellerPhone, time, "")
}