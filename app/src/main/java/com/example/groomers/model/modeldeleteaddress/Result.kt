package com.example.groomers.model.modeldeleteaddress

data class Result(
    val address: String,
    val city: Int,
    val country: Int,
    val created_at: String,
    val customer_id: Int,
    val id: Int,
    val state: Int,
    val status: Int,
    val updated_at: String,
    val zip_code: Int
)