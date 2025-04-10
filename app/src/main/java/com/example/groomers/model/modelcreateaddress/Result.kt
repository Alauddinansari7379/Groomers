package com.example.groomers.model.modelcreateaddress

data class Result(
    val address: String,
    val city: String,
    val country: String,
    val created_at: String,
    val customer_id: Int,
    val id: Int,
    val state: String,
    val updated_at: String,
    val zip_code: String
)