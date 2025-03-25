package com.example.groomers.model.modelslotbooking

data class Result(
    val IsActive: Int,
    val active: Int,
    val created_at: String,
    val day: String,
    val end_time: String,
    val forService: String,
    val id: Int,
    val seat_available: Int,
    val service_id: Int,
    val start_time: String,
    val status: Int,
    val updated_at: String,
    val user_id: String
)