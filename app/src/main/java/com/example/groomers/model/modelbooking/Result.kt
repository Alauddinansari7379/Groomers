package com.example.groomers.model.modelbooking

data class Result(
    val created_at: String,
    val customer_id: String,
    val date: String,
    val id: Int,
    val notes: String,
    val payment_mode: String,
    val service_id: String,
    val slot_id: String,
    val time: String,
    val total: String,
    val updated_at: String,
    val vendor_id: String
)