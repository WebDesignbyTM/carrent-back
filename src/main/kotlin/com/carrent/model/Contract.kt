package com.carrent.model

import kotlinx.serialization.Serializable

@Serializable
data class Contract(
    val customerName: String,
    val employeeName: String,
    val carId: Int,
)