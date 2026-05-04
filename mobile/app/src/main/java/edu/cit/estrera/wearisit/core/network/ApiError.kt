package edu.cit.estrera.wearisit.data.remote
data class ApiError(
    val code: String,
    val message: String,
    val details: Any?
)