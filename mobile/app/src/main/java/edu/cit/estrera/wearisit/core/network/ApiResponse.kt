package edu.cit.estrera.wearisit.core.network

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ApiError?,
    val timestamp: String
)