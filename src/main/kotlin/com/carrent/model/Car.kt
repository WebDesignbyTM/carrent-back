package com.carrent.model

import kotlinx.serialization.Serializable

@Serializable
data class Car(
    val id: Int,
    val brand: String,
    val model: String,
)