package com.carrent.model

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val name: String,
    val email: String,
    val passHash: String,
    val adminFlag: Boolean,
)