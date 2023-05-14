package com.carrent.model

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val name: String,
    val email: String,
    val phone: String,
    val passHash: String,
)