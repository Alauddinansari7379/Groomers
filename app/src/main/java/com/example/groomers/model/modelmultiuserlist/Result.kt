package com.example.groomers.model.modelmultiuserlist

data class Result(
    val created_at: String,
    val customer_id: Int,
    val email: String,
    val id: Int,
    val name: String,
    val password: String,
    val status: Int,
    val updated_at: String,
    val username: String
)