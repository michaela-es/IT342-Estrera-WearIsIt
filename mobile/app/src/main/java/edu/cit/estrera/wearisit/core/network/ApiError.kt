package edu.cit.estrera.wearisit.core.network
data class ApiError(
    val code: String,
    val message: String,
    val details: Any?
)